package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import java.net.URI;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

public class JiraHistoryParser {
	
	private static Regex                    skipRegex = new Regex("The issue you are trying to view does not exist");
	
	private final SortedSet<HistoryElement> history   = new TreeSet<HistoryElement>();
	
	private final String                    reportId;
	
	private final URI                       uri;
	private Person                          resolver  = null;
	
	public JiraHistoryParser(final String reportId, final URI uri) {
		this.reportId = reportId;
		this.uri = uri;
	}
	
	public SortedSet<HistoryElement> getHistory() {
		return this.history;
	}
	
	public Person getResolver() {
		return this.resolver;
	}
	
	public boolean parse() {
		// PRECONDITIONS
		
		try {
			try {
				final RawContent rawContent = IOUtils.fetch(this.uri);
				final List<List<RegexGroup>> findAll = skipRegex.findAll(rawContent.getContent());
				if ((findAll != null) && !findAll.isEmpty()) {
					return true;
				}
				
				final Document document = Jsoup.parse(rawContent.getContent());
				
				final Element activityModuleHeading = document.getElementById("activitymodule_heading");
				if (activityModuleHeading == null) {
					if (Logger.logError()) {
						Logger.error("Could not find <div id=\"activitymodule_heading\"> in JIRA history tab. reportId="
						        + this.reportId);
					}
					return false;
				}
				final Element activityContent = activityModuleHeading.nextElementSibling();
				if (activityContent == null) {
					if (Logger.logError()) {
						Logger.error("Could not find to <div id=\"activitymodule_heading\">: reportId=" + this.reportId);
					}
					return false;
				}
				final Elements issuePanelContainer = activityContent.getElementsByClass("issuePanelContainer");
				if ((issuePanelContainer == null) || (issuePanelContainer.isEmpty())) {
					if (Logger.logError()) {
						Logger.error("Could not find to <div class=\"issuePanelContainer\"> reportId=" + this.reportId);
					}
					return false;
				}
				final Elements historyDataBlocks = issuePanelContainer.get(0).getElementsByClass("issue-data-block");
				if ((historyDataBlocks == null) || (historyDataBlocks.isEmpty())) {
					return true;
				}
				for (final Element historyDataBlock : historyDataBlocks) {
					final Element child = historyDataBlock.child(0);
					if (child == null) {
						continue;
					}
					final Element header = child.child(0);
					final Element body = child.child(1);
					
					Person who = null;
					// get the author and the timestamp
					for (final Element aTag : header.getElementsByTag("a")) {
						if ((aTag.attr("id") != null) && (aTag.attr("id").startsWith("changehistoryauthor"))) {
							final String href = aTag.attr("href");
							String username = null;
							if (href != null) {
								final int index = href.indexOf("name=");
								if (index > -1) {
									username = href.substring(index + 5);
								}
							}
							final String fullname = aTag.text();
							who = new Person(username, fullname, null);
						}
					}
					DateTime when = null;
					final Elements dateElems = header.getElementsByTag("time");
					if (!dateElems.isEmpty()) {
						when = DateTimeUtils.parseDate(dateElems.get(0).attr("datetime"),
						                               new Regex(
						                                         "({yyyy}\\d{4})[-:/_]({MM}[0-2]\\d)[-:/_]({dd}[0-3]\\d)T({HH}[0-2]\\d)[-:/_]({mm}[0-5]\\d)([-:/_]({ss}[0-5]\\d))({Z}[+-]\\d{4})?"));
					}
					if ((who != null) && (when != null)) {
						final HistoryElement historyElement = new HistoryElement(this.reportId, who, when);
						
						final Elements tbodys = body.getElementsByTag("tbody");
						if (tbodys.isEmpty()) {
							if (Logger.logError()) {
								Logger.error("Could not find tbody tag in JIRA history");
							}
							continue;
						}
						final Elements trs = tbodys.first().getElementsByTag("tr");
						if (trs.isEmpty()) {
							continue;
						}
						for (final Element tr : trs) {
							if (tr.childNodes().size() < 3) {
								if (Logger.logError()) {
									Logger.error("Cannot handle history table row with less than three columns: reportId="
									        + this.reportId);
								}
								continue;
							}
							final String fieldString = tr.child(0).text().replaceAll("\"", "").trim().toLowerCase();
							final String oldValue = tr.child(1).text().replaceAll("\"", "").trim().toLowerCase();
							final String newValue = tr.child(2).text().replaceAll("\"", "").trim().toLowerCase();
							if (fieldString.equals("type")) {
								historyElement.addChangedValue("type", JiraParser.resolveType(oldValue),
								                               JiraParser.resolveType(newValue));
							} else if (fieldString.equals("priority")) {
								historyElement.addChangedValue("severity", JiraParser.resolveSeverity(oldValue),
								                               JiraParser.resolveSeverity(newValue));
							} else if (fieldString.startsWith("affected version")) {
								historyElement.addChangedValue("version", oldValue, newValue);
							} else if (fieldString.startsWith("component")) {
								historyElement.addChangedValue("component", oldValue, newValue);
							} else if (fieldString.equals("labels")) {
								historyElement.addChangedValue("keywords", oldValue, newValue);
							} else if (fieldString.equals("status")) {
								historyElement.addChangedValue("status", JiraParser.resolveStatus(oldValue),
								                               JiraParser.resolveStatus(newValue));
							} else if (fieldString.equals("resolution")) {
								final Resolution newResolution = JiraParser.resolveResolution(newValue);
								historyElement.addChangedValue("resolution", JiraParser.resolveResolution(oldValue),
								                               newResolution);
								if (newResolution.equals(Resolution.RESOLVED)) {
									this.resolver = who;
								}
							}
						}
						if (!historyElement.isEmpty()) {
							this.history.add(historyElement);
						}
					}
				}
			} catch (final UnsupportedProtocolException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			} catch (final FetchException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			}
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
}

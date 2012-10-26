/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mozkito.issues.tracker.bugzilla;

import java.net.URI;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.jdom.Namespace;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.elements.Resolution;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.persistence.model.Person;


/**
 * The Class BugzillaHistoryParser.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class BugzillaHistoryParser_4_0_4 implements BugzillaHistoryParser {
	
	/** The namespace. */
	protected static Namespace              namespace = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
	
	/** The history uri. */
	private final URI                       historyUri;
	
	/** The report id. */
	private final String                    reportId;
	
	/** The resolver. */
	private Person                          resolver;
	
	/** The resolution timestamp. */
	private DateTime                        resolutionTimestamp;
	
	/** The history. */
	private final SortedSet<HistoryElement> history   = new TreeSet<HistoryElement>();
	
	/** The parsed. */
	private boolean                         parsed    = false;
	
	/** The skip regex. */
	private static Regex                    skipRegex = new Regex("No changes have been made to this bug yet.");
	
	/**
	 * Instantiates a new bugzilla history parser.
	 * 
	 * @param historyUri
	 *            the history uri
	 * @param reportId
	 *            the report id
	 */
	public BugzillaHistoryParser_4_0_4(final URI historyUri, final String reportId) {
		this.historyUri = historyUri;
		this.reportId = reportId;
		
	}
	
	/**
	 * Gets the history.
	 * 
	 * @return the history
	 */
	@Override
	public SortedSet<HistoryElement> getHistory() {
		return this.history;
	}
	
	/**
	 * Gets the resolution timestamp.
	 * 
	 * @return the resolution timestamp
	 */
	@Override
	public DateTime getResolutionTimestamp() {
		return this.resolutionTimestamp;
	}
	
	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	@Override
	public Person getResolver() {
		return this.resolver;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.bugzilla.BugzillaHistoryParser#hasParsed()
	 */
	@Override
	public boolean hasParsed() {
		return this.parsed;
	}
	
	/**
	 * Parses the.
	 * 
	 * @return true, if successful
	 */
	@Override
	@NoneNull
	public boolean parse() {
		
		if (this.parsed) {
			return true;
		}
		
		final String errorHeader = "Could not parse bugzilla report history for report " + this.reportId + ": ";
		
		try {
			final RawContent rawContent = IOUtils.fetch(this.historyUri);
			
			final MultiMatch multiMatch = skipRegex.findAll(rawContent.getContent());
			if (multiMatch != null) {
				if (Logger.logDebug()) {
					Logger.debug("Skipping history for bug report " + this.reportId
					        + ". No changes have been made to this bug yet.");
				}
				return true;
			}
			
			final Document document = Jsoup.parse(rawContent.getContent());
			final Element bugzillaBody = document.getElementById("bugzilla-body");
			if (bugzillaBody == null) {
				if (Logger.logError()) {
					Logger.error(errorHeader + "Could not find bugzilla-body.");
				}
				return false;
			}
			final Elements tables = bugzillaBody.getElementsByTag("table");
			if (tables.isEmpty()) {
				if (Logger.logError()) {
					Logger.error(errorHeader + "Could not find bugzill-body table.");
				}
				return false;
			}
			final Element historyTable = tables.first();
			final Elements trs = historyTable.getElementsByTag("tr");
			int rowspan = 1;
			HistoryElement hElement = null;
			for (int i = 1; i < trs.size(); ++i) {
				final Element tr = trs.get(i);
				final Elements tds = tr.getElementsByTag("td");
				if (tds.size() < 3) {
					if (Logger.logError()) {
						Logger.error(errorHeader
						        + "at least 3 columns in a mozilla body table are expected in every row.");
					}
					return false;
				}
				
				final int whatIndex = rowspan < 2
				                                 ? 2
				                                 : 0;
				
				if (rowspan < 2) {
					if (tds.size() < 5) {
						if (Logger.logError()) {
							Logger.error(errorHeader
							        + "at least 5 columns in a mozilla body table are expected in new history element rows.");
						}
						return false;
					}
					final String rowspanString = tds.get(0).attr("rowspan");
					if (!rowspanString.equals("")) {
						rowspan = Integer.valueOf(rowspanString);
						if (rowspan > 1) {
							++rowspan;
						}
					} else {
						rowspan = 1;
					}
					
					// get who
					final String whoString = tds.get(0).text().trim();
					Person who = new Person(whoString, null, null);
					if (whoString.equals("")) {
						who = Tracker.unknownPerson;
					}
					
					// get when
					final DateTime when = DateTimeUtils.parseDate(tds.get(1).text().trim());
					hElement = new HistoryElement(this.reportId, who, when);
					this.history.add(hElement);
				}
				if (hElement == null) {
					if (Logger.logError()) {
						Logger.error(errorHeader + "current history element must not be null at this point.");
					}
					return false;
				}
				
				// get what
				
				final String what = tds.get(whatIndex).text().trim().toLowerCase();
				
				// get removed
				final String removed = tds.get(whatIndex + 1).text().trim();
				
				// get added
				final String added = tds.get(whatIndex + 2).text().trim();
				
				String field = null;
				if (what.equals("priority")) {
					field = "priority";
					hElement.addChangedValue(field, BugzillaParser.getPriority(removed),
					                         BugzillaParser.getPriority(added));
				} else if (what.equals("summary")) {
					field = ("summary");
					hElement.addChangedValue(field, removed, added);
				} else if (what.equals("resolution")) {
					field = ("resolution");
					hElement.addChangedValue(field, BugzillaParser.getResolution(removed),
					                         BugzillaParser.getResolution(added));
					// set report resolution date and resolver
					if (BugzillaParser.getResolution(added).equals(Resolution.RESOLVED)) {
						this.resolver = hElement.getAuthor();
						this.resolutionTimestamp = hElement.getTimestamp();
					}
				} else if (what.equals("assignee")) {
					field = ("assignedTo");
					final Person oldValue = new Person(removed, null, null);
					final Person newValue = new Person(added, null, null);
					hElement.addChangedValue(field, oldValue, newValue);
				} else if (what.equals("component")) {
					field = ("component");
					hElement.addChangedValue(field, removed, added);
				} else if (what.equals("version")) {
					field = ("version");
					hElement.addChangedValue(field, removed, added);
				} else if (what.equals("summary")) {
					field = ("summary");
					hElement.addChangedValue(field, removed, added);
				} else if (what.equals("severity")) {
					field = ("severity");
					hElement.addChangedValue(field, BugzillaParser.getSeverity(removed),
					                         BugzillaParser.getSeverity(added));
				} else if (what.equals("status")) {
					field = ("status");
					hElement.addChangedValue(field, BugzillaParser.getStatus(removed), BugzillaParser.getStatus(added));
				} else if (what.equals("product")) {
					field = ("product");
					hElement.addChangedValue(field, removed, added);
				} else if (what.equals("category")) {
					field = ("category");
					hElement.addChangedValue(field, removed, added);
				}
				--rowspan;
			}
			
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e, "Could not parse bugzilla report history.");
			}
		}
		this.parsed = true;
		return true;
	}
}

/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.jira;

import java.net.URI;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.model.History;
import org.mozkito.issues.model.HistoryElement;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.datastructures.RawContent;
import org.mozkito.utilities.datastructures.Tuple;
import org.mozkito.utilities.datetime.DateTimeUtils;
import org.mozkito.utilities.io.IOUtils;
import org.mozkito.utilities.io.exceptions.FetchException;
import org.mozkito.utilities.io.exceptions.UnsupportedProtocolException;

/**
 * The Class JiraHistoryParser.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class JiraHistoryParser {
	
	/** The Constant MIN_CHILD_NODE_SIZE. */
	private static final int    MIN_CHILD_NODE_SIZE       = 3;
	
	/** The Constant NAME_TAG_LENGTH. */
	private static final int    NAME_TAG_LENGTH           = 5;
	
	/** The skip regex. */
	private static Regex        skipRegex                 = new Regex("The issue you are trying to view does not exist");
	
	/** The history. */
	private boolean             parsed                    = false;
	
	/** The uri. */
	private final URI           uri;
	
	/** The resolver. */
	private Person              resolver                  = null;
	
	/** The person factory. */
	private final PersonFactory personFactory;
	
	/** The history date time pattern. */
	public static String        HISTORY_DATE_TIME_PATTERN = "({yyyy}\\d{4})[-:/_]({MM}[0-2]\\d)[-:/_]({dd}[0-3]\\d)T({HH}[0-2]\\d)[-:/_]({mm}[0-5]\\d)([-:/_]({ss}[0-5]\\d))?({Z}[+-]\\d{4})?";
	
	/**
	 * Instantiates a new jira history parser.
	 * 
	 * @param uri
	 *            the uri
	 * @param personFactory
	 *            the person factory
	 */
	public JiraHistoryParser(final URI uri, final PersonFactory personFactory) {
		this.personFactory = personFactory;
		this.uri = uri;
	}
	
	/**
	 * Gets the person factory.
	 * 
	 * @return the personFactory
	 */
	public final PersonFactory getPersonFactory() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.personFactory;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.personFactory,
				                  "Field '%s' in '%s'.", "personFactory", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	public Person getResolver() {
		return this.resolver;
	}
	
	/**
	 * Parses the.
	 * 
	 * @param history
	 *            the history
	 * @return true, if successful
	 */
	public boolean parse(final History history) {
		// PRECONDITIONS
		if (this.parsed) {
			return true;
		}
		try {
			try {
				final RawContent rawContent = IOUtils.fetch(this.uri);
				final MultiMatch findAll = JiraHistoryParser.skipRegex.findAll(rawContent.getContent());
				if (findAll != null) {
					return true;
				}
				
				final Document document = Jsoup.parse(rawContent.getContent());
				
				final Element activityModuleHeading = document.getElementById("activitymodule_heading");
				if (activityModuleHeading == null) {
					if (Logger.logError()) {
						Logger.error("Could not find <div id=\"activitymodule_heading\"> in JIRA history tab. uri="
						        + this.uri.toString());
					}
					return false;
				}
				final Element activityContent = activityModuleHeading.nextElementSibling();
				if (activityContent == null) {
					if (Logger.logError()) {
						Logger.error("Could not find to <div id=\"activitymodule_heading\">: uri="
						        + this.uri.toString());
					}
					return false;
				}
				final Elements issuePanelContainer = activityContent.getElementsByClass("issuePanelContainer");
				if ((issuePanelContainer == null) || (issuePanelContainer.isEmpty())) {
					if (Logger.logError()) {
						Logger.error("Could not find to <div class=\"issuePanelContainer\"> uri=" + this.uri.toString());
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
					if (child.children().size() < 2) {
						if (Logger.logError()) {
							Logger.error("Could not find the body section of the issue-data-block. Skipping ...");
						}
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
									username = href.substring(index + JiraHistoryParser.NAME_TAG_LENGTH);
								}
							}
							final String fullname = aTag.text();
							who = getPersonFactory().get(username, fullname, null);
						}
					}
					DateTime when = null;
					final Elements dateElems = header.getElementsByTag("time");
					if (!dateElems.isEmpty()) {
						when = DateTimeUtils.parseDate(dateElems.get(0).attr("datetime"),
						                               new Regex(JiraHistoryParser.HISTORY_DATE_TIME_PATTERN));
					}
					if ((who != null) && (when != null)) {
						final HistoryElement historyElement = new HistoryElement(history, who, when);
						
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
							if (tr.childNodes().size() < JiraHistoryParser.MIN_CHILD_NODE_SIZE) {
								if (Logger.logError()) {
									Logger.error("Cannot handle history table row with less than three columns: uri="
									        + this.uri.toString());
								}
								continue;
							}
							final String fieldString = tr.child(0).text().replaceAll("\"", "").trim().toLowerCase();
							final String oldValue = tr.child(1).text().replaceAll("\"", "")
							                          .replaceAll("\\[[^\\]]+\\]", "").trim().toLowerCase();
							final String newValue = tr.child(2).text().replaceAll("\"", "")
							                          .replaceAll("\\[[^\\]]+\\]", "").trim().toLowerCase();
							if ("type".equals(fieldString)) {
								historyElement.addChangedValue("type", JiraParser.resolveType(oldValue),
								                               JiraParser.resolveType(newValue));
							} else if ("priority".equals(fieldString)) {
								historyElement.addChangedValue("severity", JiraParser.resolveSeverity(oldValue),
								                               JiraParser.resolveSeverity(newValue));
							} else if (fieldString.startsWith("affects version")) {
								if (!historyElement.contains("version")) {
									historyElement.addChangedValue("version", oldValue, newValue);
								} else {
									@SuppressWarnings ("unchecked")
									final Tuple<String, String> tuple = (Tuple<String, String>) historyElement.get("version");
									if (((tuple.getFirst() == null) || (tuple.getFirst().isEmpty()))
									        && (oldValue != null) && (!oldValue.isEmpty())) {
										tuple.setFirst(oldValue);
									}
									if (((tuple.getSecond() == null) || (tuple.getSecond().isEmpty()))
									        && (newValue != null) && (!newValue.isEmpty())) {
										tuple.setSecond(newValue);
									}
									historyElement.addChangedValue("version", tuple.getFirst(), tuple.getSecond());
								}
							} else if (fieldString.startsWith("fix version")) {
								if (!historyElement.contains("fix version")) {
									historyElement.addChangedValue("fix version", oldValue, newValue);
								} else {
									@SuppressWarnings ("unchecked")
									final Tuple<String, String> tuple = (Tuple<String, String>) historyElement.get("fix version");
									if (((tuple.getFirst() == null) || (tuple.getFirst().isEmpty()))
									        && (oldValue != null) && (!oldValue.isEmpty())) {
										tuple.setFirst(oldValue);
									}
									if (((tuple.getSecond() == null) || (tuple.getSecond().isEmpty()))
									        && (newValue != null) && (!newValue.isEmpty())) {
										tuple.setSecond(newValue);
									}
									historyElement.addChangedValue("fix version", tuple.getFirst(), tuple.getSecond());
								}
							} else if (fieldString.startsWith("component")) {
								historyElement.addChangedValue("component", oldValue, newValue);
							} else if ("labels".equals(fieldString)) {
								historyElement.addChangedValue("keywords", oldValue, newValue);
							} else if ("status".equals(fieldString)) {
								historyElement.addChangedValue("status", JiraParser.resolveStatus(oldValue),
								                               JiraParser.resolveStatus(newValue));
							} else if ("resolution".equals(fieldString)) {
								final Resolution newResolution = JiraParser.resolveResolution(newValue);
								historyElement.addChangedValue("resolution", JiraParser.resolveResolution(oldValue),
								                               newResolution);
								if (newResolution.equals(Resolution.RESOLVED)) {
									this.resolver = who;
								}
							}
						}
					}
				}
			} catch (final UnsupportedProtocolException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				return false;
			} catch (final FetchException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				return false;
			}
			this.parsed = true;
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
}

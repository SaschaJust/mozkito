/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.joda.time.DateTime;

import com.google.gdata.client.projecthosting.IssuesQuery;
import com.google.gdata.client.projecthosting.ProjectHostingService;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.IssuesFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.OverviewParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Class GoogleTracker.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GoogleTracker extends Tracker {
	
	protected static String       fetchRegexPattern = "((https?://code.google.com/feeds/issues/p/({=project}\\S+)/issues/full)|(https?://code.google.com/p/({=project}\\S+)/issues/list))";
	private String                projectName;
	private ProjectHostingService service;
	
	private final static Person   unknownPerson     = new Person("<unknown>", null, null);
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkRAW(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public boolean checkRAW(final RawReport rawReport) {
		if ((rawReport == null) || (!(rawReport instanceof GoogleRawContent))) {
			if (Logger.logDebug()) {
				Logger.debug("GOOGLE TRACKER: raw check for issue #" + rawReport.getId() + "failed!");
			}
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#checkXML(de.unisaarland
	 * .cs.st.reposuite.bugs.tracker.XmlReport)
	 */
	@Override
	public boolean checkXML(final XmlReport xmlReport) {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#createDocument(de
	 * .unisaarland.cs.st.reposuite.bugs.tracker.RawReport)
	 */
	@Override
	public XmlReport createDocument(final RawReport rawReport) {
		Condition.check(rawReport instanceof GoogleRawContent,
		                "The rawReport has to be an instance of GoogleRawContent.");
		return (GoogleRawContent) rawReport;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#fetchSource(java. net.URI)
	 */
	@Override
	public RawReport fetchSource(final URI uri) throws FetchException, UnsupportedProtocolException {
		try {
			final Long bugId = Long.valueOf(uri.toString());
			final IssuesQuery iQuery = new IssuesQuery(getUri().toURL());
			iQuery.setId(bugId.intValue());
			
			if (Logger.logDebug()) {
				Logger.debug("Fetching RawReport form url: " + iQuery.getFeedUrl().toString()
				        + iQuery.getQueryUri().toString());
			}
			
			final IssuesFeed resultFeed = this.service.query(iQuery, IssuesFeed.class);
			final List<IssuesEntry> entries = resultFeed.getEntries();
			
			CollectionCondition.minSize(entries, 1, "There has to be at least one entry in the issue list.");
			
			final IssuesEntry issuesEntry = entries.get(0);
			
			if (issuesEntry == null) {
				if (Logger.logWarn()) {
					Logger.warn("Skipping report #" + bugId + ". Feed returned no entries!");;
				}
				return null;
			}
			
			final byte[] digest = MessageDigest.getInstance("MD5").digest(issuesEntry.toString().getBytes());
			return new GoogleRawContent(bugId, new DateTime(), issuesEntry, digest);
		} catch (final NumberFormatException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new UnrecoverableError("Got wrongly encoded URL.");
		} catch (final MalformedURLException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final ServiceException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final NoSuchAlgorithmException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getLinkFromId(java .lang.Long)
	 */
	@Override
	public URI getLinkFromId(final Long bugId) {
		try {
			return new URI(bugId.toString());
		} catch (final URISyntaxException e) {
			throw new UnrecoverableError("Could not convert long to URI");
		}
	}
	
	@Override
	public OverviewParser getOverviewParser(final RawContent overviewContent) {
		// PRECONDITIONS
		
		try {
			if (Logger.logError()) {
				Logger.error("Overview parsing not supported yet.");
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	// /*
	// * (non-Javadoc)
	// * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#parse(de.unisaarland
	// * .cs.st.reposuite.bugs.tracker.XmlReport)
	// */
	// @Override
	// public Report parse(final XmlReport xmlReport) {
	// Condition.check(xmlReport instanceof GoogleRawContent,
	// "The xmlReport has to be an instance of GoogleRawContent.");
	//
	// GoogleRawContent issue = (GoogleRawContent) xmlReport;
	// Report report = new Report(issue.getId());
	//
	// if (Logger.logDebug()) {
	// Logger.debug("GOOGLE TRACKER: parsing issue #" + issue.getId());
	// }
	//
	// if ((issue.getOwner() != null) && (issue.getOwner().toPerson() != null)) {
	// report.setAssignedTo(issue.getOwner().toPerson());
	// }
	//
	// report.setCategory(issue.getCategory());
	//
	// report.setCreationTimestamp(issue.getCreationDate());
	// report.setLastFetch(xmlReport.getFetchTime());
	// if (issue.getUpdateDate() != null) {
	// report.setLastUpdateTimestamp(issue.getUpdateDate());
	// }
	//
	// if (issue.getPriority() != null) {
	// String googlePriority = issue.getPriority().toLowerCase();
	// if (googlePriority.equals("critical")) {
	// report.setPriority(Priority.VERY_HIGH);
	// } else if (googlePriority.equals("high")) {
	// report.setPriority(Priority.HIGH);
	// } else if (googlePriority.equals("medium")) {
	// report.setPriority(Priority.NORMAL);
	// } else if (googlePriority.equals("low")) {
	// report.setPriority(Priority.LOW);
	// } else {
	// report.setPriority(Priority.UNKNOWN);
	// if (Logger.logWarn()) {
	// Logger.warn("Unknown priority `" + googlePriority + "` seen in issue `" + report.getId()
	// + "`. Setting prioroty to UNKNOWN.");
	// }
	// }
	// }
	//
	// if (issue.getStatus() != null) {
	// String status = issue.getStatus().toLowerCase();
	// // Started, Accepted, FixedNotReleased, NeedsInfo, New,
	// // PatchesWelcome,
	// // ReviewPending, AssumedStale, Duplicate, Fixed, Invalid,
	// // KnownQuirk,
	// // NotPlanned
	// if (status.equals("started")) {
	// report.setStatus(Status.IN_PROGRESS);
	// } else if (status.equals("accepted")) {
	// report.setStatus(Status.ASSIGNED);
	// } else if (status.equals("fixednotreleased")) {
	// report.setStatus(Status.IN_PROGRESS);
	// } else if (status.equals("needsinfo")) {
	// report.setStatus(Status.FEEDBACK);
	// } else if (status.equals("new")) {
	// report.setStatus(Status.NEW);
	// } else if (status.equals("patcheswelcome")) {
	// report.setStatus(Status.UNKNOWN);
	// } else if (status.equals("reviewpending")) {
	// report.setStatus(Status.REVIEWPENDING);
	// } else if (status.equals("assumedstale")) {
	// report.setStatus(Status.UNKNOWN);
	// } else if (status.equals("duplicate")) {
	// report.setResolution(Resolution.DUPLICATE);
	// report.setStatus(Status.CLOSED);
	// } else if (status.equals("fixed")) {
	// report.setResolution(Resolution.RESOLVED);
	// report.setStatus(Status.CLOSED);
	// } else if (status.equals("invalid")) {
	// report.setResolution(Resolution.INVALID);
	// report.setStatus(Status.CLOSED);
	// } else if (status.equals("knownquirk")) {
	// report.setResolution(Resolution.INVALID);
	// report.setStatus(Status.CLOSED);
	// } else if (status.equals("notplanned")) {
	// report.setResolution(Resolution.INVALID);
	// report.setStatus(Status.CLOSED);
	// }
	// if (issue.getState().toLowerCase().equals("closed")) {
	// report.setStatus(Status.CLOSED);
	// }
	// }
	//
	// if (issue.getCloseDate() != null) {
	// report.setResolutionTimestamp(issue.getCloseDate());
	// }
	//
	// if (issue.getAuthors().size() > 0) {
	// report.setSubmitter(issue.getAuthors().get(0).toPerson());
	// }
	//
	// SortedSet<Long> siblings = new TreeSet<Long>();
	// for (Integer i : issue.getBlockedOn()) {
	// siblings.add(i.longValue());
	//
	// }
	// for (Integer i : issue.getBlocking()) {
	// siblings.add(i.longValue());
	//
	// }
	// report.setSiblings(siblings);
	//
	// report.setSubject(issue.getTitle());
	// report.setSummary(issue.getSummary());
	// report.setDescription(issue.getDescription());
	//
	// if (issue.getType() == null) {
	// report.setType(Type.UNKNOWN);
	// } else {
	// String type = issue.getType().toLowerCase();
	// if (type.equals("defect")) {
	// report.setType(Type.BUG);
	// } else if (type.equals("enhancement")) {
	// report.setType(Type.RFE);
	// } else if (type.equals("task")) {
	// report.setType(Type.TASK);
	// } else if (type.equals("docs")) {
	// report.setType(Type.OTHER);
	// } else if (type.equals("----")) {
	// report.setType(Type.UNKNOWN);
	// } else if (type.equals("feature")) {
	// report.setType(Type.RFE);
	// } else if (type.equals("optimization")) {
	// report.setType(Type.OTHER);
	// } else {
	// report.setType(Type.UNKNOWN);
	// if (Logger.logWarn()) {
	// Logger.warn("Detected an unknown type `" + type + "` in issue `" + report.getId()
	// + "`. Setting type to UNKNOWN.");
	// }
	// }
	// }
	//
	// report.setVersion(issue.getVersion());
	//
	// parseComments(report);
	//
	// return report;
	// }
	
	// private void parseComments(final Report report) {
	// SortedSet<Comment> comments = new TreeSet<Comment>();
	// Person fixer = null;
	// Person resolver = null;
	// int counter = 0;
	// int max_result = 25;
	//
	// URL baseFeedUrl = null;
	// try {
	// baseFeedUrl = new URL("https://code.google.com/feeds/issues/p/" + projectName + "/issues/" + report.getId()
	// + "/comments/full?max-result=" + max_result);
	//
	// } catch (MalformedURLException e) {
	// if (Logger.logWarn()) {
	// Logger.warn("Could not create URL!", e);
	// }
	// return;
	// }
	// ProjectHostingService service = new ProjectHostingService("unisaarland-reposuite-0.1");
	//
	// while (true) {
	//
	// URL feedUrl = baseFeedUrl;
	// if (counter != 0) {
	// int startIndex = (counter * max_result) + 1;
	// try {
	// feedUrl = new URL(baseFeedUrl.toString() + "&start-index=" + startIndex);
	// } catch (MalformedURLException e) {
	// if (Logger.logWarn()) {
	// Logger.warn("Error while building feed url. Abort.", e);
	// }
	// break;
	// }
	// }
	// ++counter;
	//
	// IssueCommentsFeed resultFeed = null;
	// try {
	// resultFeed = service.getFeed(feedUrl, IssueCommentsFeed.class);
	// } catch (ServiceException e) {
	// if (Logger.logWarn()) {
	// Logger.warn(e.getMessage(), e);
	// }
	// break;
	// } catch (IOException e) {
	// if (Logger.logWarn()) {
	// Logger.warn(e.getMessage(), e);
	// }
	// break;
	// }
	//
	// if (resultFeed.getEntries().size() < 1) {
	// break;
	// }
	//
	// for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	// IssueCommentsEntry commentEntry = resultFeed.getEntries().get(i);
	//
	// // commentEntry.getContent().
	//
	// TextContent textContent = (TextContent) commentEntry.getContent();
	// String message = "";
	// if ((textContent != null) && (textContent.getContent() != null)) {
	// HtmlTextConstruct htmlConstruct = (HtmlTextConstruct) textContent.getContent();
	// message = htmlConstruct.getHtml();
	// }
	// com.google.gdata.data.DateTime published = commentEntry.getPublished();
	// DateTime createDate = new DateTime(published.getValue(),
	// DateTimeZone.forOffsetHours(published.getTzShift()));
	//
	// Person author = unknownPerson;
	// List<com.google.gdata.data.Person> authors = commentEntry.getAuthors();
	// if (authors.size() > 0) {
	// com.google.gdata.data.Person person = authors.get(0);
	// author = new Person(person.getName(), person.getNameLang(), person.getEmail());
	// }
	//
	// if (commentEntry.hasUpdates()) {
	// Updates updates = commentEntry.getUpdates();
	// updates.getBlockedOnUpdates();
	//
	// HistoryElement hElem = new HistoryElement(report.getId(), author, createDate);
	//
	// if (updates.getCcUpdates() != null) {
	// // CCs are not supported by report
	// }
	//
	// Map<String, Tuple<String, String>> changes = new HashMap<String, Tuple<String, String>>();
	//
	// for (Label l : updates.getLabels()) {
	// String label = l.getValue();
	// String compValue = l.getValue().toLowerCase();
	// if (compValue.startsWith("type-")) {
	// String newValue = label.substring(5).trim();
	// if (changes.containsKey("type")) {
	// changes.get("type").setSecond(newValue);
	// } else {
	// changes.put("type", new Tuple<String, String>("<UNKNOWN>", newValue));
	// }
	// } else if (compValue.startsWith("-type-")) {
	// String oldValue = label.substring(6).trim();
	// if (changes.containsKey("type")) {
	// changes.get("type").setFirst(oldValue);
	// } else {
	// changes.put("type", new Tuple<String, String>(oldValue, "<UNKNOWN>"));
	// }
	// } else if (compValue.startsWith("priority-")) {
	// String newValue = label.substring(9).trim();
	// if (changes.containsKey("priority")) {
	// changes.get("priority").setSecond(newValue);
	// } else {
	// changes.put("priority", new Tuple<String, String>("<UNKNOWN>", newValue));
	// }
	// } else if (compValue.startsWith("-priority-")) {
	// String oldValue = label.substring(10).trim();
	// if (changes.containsKey("priority")) {
	// changes.get("priority").setFirst(oldValue);
	// } else {
	// changes.put("priority", new Tuple<String, String>(oldValue, "<UNKNOWN>"));
	// }
	// } else if (compValue.startsWith("category-")) {
	// String newValue = label.substring(9).trim();
	// if (changes.containsKey("category")) {
	// changes.get("category").setSecond(newValue);
	// } else {
	// changes.put("category", new Tuple<String, String>("<UNKNOWN>", newValue));
	// }
	// } else if (compValue.startsWith("-category-")) {
	// String oldValue = label.substring(10).trim();
	// if (changes.containsKey("category")) {
	// changes.get("category").setFirst(oldValue);
	// } else {
	// changes.put("category", new Tuple<String, String>(oldValue, "<UNKNOWN>"));
	// }
	// } else if (compValue.startsWith("milestone-")) {
	// String newValue = label.substring(10).trim();
	// if (changes.containsKey("milestone")) {
	// changes.get("milestone").setSecond(newValue);
	// } else {
	// changes.put("milestone", new Tuple<String, String>("<UNKNOWN>", newValue));
	// }
	// } else if (compValue.startsWith("-milestone-")) {
	// String oldValue = label.substring(11).trim();
	// if (changes.containsKey("milestone")) {
	// changes.get("milestone").setFirst(oldValue);
	// } else {
	// changes.put("milestone", new Tuple<String, String>(oldValue, "<UNKNOWN>"));
	// }
	// }
	// }
	//
	// if (updates.getOwnerUpdate() != null) {
	// hElem.addChangedValue("assignedTo", unknownPerson, new Person(updates.getOwnerUpdate()
	// .getValue(), null, null));
	// }
	//
	// if (updates.getStatus() != null) {
	// String status = updates.getStatus().getValue().toLowerCase();
	// if (status.equals("started")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.IN_PROGRESS);
	// } else if (status.equals("accepted")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.ASSIGNED);
	// } else if (status.equals("fixednotreleased")) {
	// fixer = author;
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.IN_PROGRESS);
	// } else if (status.equals("needsinfo")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.FEEDBACK);
	// } else if (status.equals("new")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.NEW);
	// } else if (status.equals("patcheswelcome")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.UNKNOWN);
	// } else if (status.equals("reviewpending")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.REVIEWPENDING);
	// } else if (status.equals("assumedstale")) {
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.UNKNOWN);
	// } else if (status.equals("duplicate")) {
	// hElem.addChangedValue("resolution", Status.UNKNOWN, Resolution.DUPLICATE);
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.CLOSED);
	// } else if (status.equals("fixed")) {
	// resolver = author;
	// hElem.addChangedValue("resolution", Status.UNKNOWN, Resolution.RESOLVED);
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.CLOSED);
	// } else if (status.equals("invalid")) {
	// hElem.addChangedValue("resolution", Status.UNKNOWN, Resolution.INVALID);
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.CLOSED);
	// } else if (status.equals("knownquirk")) {
	// hElem.addChangedValue("resolution", Status.UNKNOWN, Resolution.INVALID);
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.CLOSED);
	// } else if (status.equals("notplanned")) {
	// hElem.addChangedValue("resolution", Status.UNKNOWN, Resolution.INVALID);
	// hElem.addChangedValue("status", Status.UNKNOWN, Status.CLOSED);
	// }
	// }
	//
	// if (updates.getSummary() != null) {
	// hElem.addChangedValue("summary", "<unknown>", updates.getSummary().getValue());
	// }
	//
	// report.addHistoryElement(hElem);
	// }
	// String googleCommentId = commentEntry.getId();
	// int index = googleCommentId.lastIndexOf("/");
	// int commentId = comments.size() + 1;
	// try {
	// commentId = new Integer(googleCommentId.substring(index + 1));
	// } catch (NumberFormatException e) {
	// if (Logger.logWarn()) {
	// Logger.warn("Could not determine google comment id. Trying to guess id.");
	// }
	// }
	// Comment comment = new Comment(commentId, author, createDate, message);
	// comments.add(comment);
	// report.addComment(comment);
	// }
	// }
	// report.setComments(comments);
	//
	// if (fixer != null) {
	// report.setResolver(fixer);
	// } else if (resolver != null) {
	// report.setResolver(resolver);
	// }
	// }
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#getParser()
	 */
	@Override
	public Parser getParser(final XmlReport xmlReport) {
		// PRECONDITIONS
		
		try {
			return new GoogleParser();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the project name.
	 * 
	 * @return the project name
	 */
	public String getProjectName() {
		return this.projectName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Tracker#setup(java.net.URI, java.net.URI, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	@Override
	public void setup(URI fetchURI,
	                  final URI overviewURI,
	                  final String pattern,
	                  final String username,
	                  final String password,
	                  final Long startAt,
	                  final Long stopAt,
	                  final String cacheDirPath) throws InvalidParameterException {
		
		final Regex fetchRegex = new Regex(fetchRegexPattern);
		final List<RegexGroup> groups = fetchRegex.find(fetchURI.toString());
		if ((groups == null) || (groups.size() < 2) || (fetchRegex.getGroup("project") == null)) {
			throw new UnrecoverableError("The specified fetchUri cannot be parser (is invalid). Abort.");
		}
		
		this.projectName = fetchRegex.getGroup("project");
		
		if (!fetchURI.toString().contains("feeds/issues")) {
			try {
				fetchURI = new URI("https://code.google.com/feeds/issues/p/" + this.projectName + "/issues/full");
			} catch (final URISyntaxException e) {
				throw new UnrecoverableError(e.getMessage(), e);
			}
		}
		super.setup(fetchURI, overviewURI, pattern, username, password, startAt, stopAt, cacheDirPath);
		
		try {
			this.service = new ProjectHostingService("unisaarland-reposuite-0.1");
			if ((username != null) && (password != null) && (!username.trim().equals(""))) {
				this.service.setUserCredentials(username, password);
			}
			
			int startIndex = 1;
			final int maxResults = 100;
			
			IssuesFeed resultFeed = this.service.getFeed(new URL(fetchURI.toString() + "?start-index=" + startIndex
			        + "&max-results=" + maxResults), IssuesFeed.class);
			if (Logger.logDebug()) {
				Logger.debug(fetchURI.toString() + "?start-index=" + startIndex + "&amp;max-results=" + maxResults);
			}
			List<IssuesEntry> feedEntries = resultFeed.getEntries();
			while (feedEntries.size() > 0) {
				for (int i = 0; i < feedEntries.size(); i++) {
					final IssuesEntry entry = feedEntries.get(i);
					final long bugId = entry.getIssueId().getValue().longValue();
					if ((bugId >= startAt) && (bugId <= stopAt)) {
						addBugId(bugId);
						if (Logger.logDebug()) {
							Logger.debug("GOOGLE TRACKER: adding issue #" + bugId + " to process list.");
						}
					}
				}
				startIndex += maxResults;
				resultFeed = this.service.getFeed(new URL(fetchURI.toString() + "?start-index=" + startIndex
				        + "&max-results=" + maxResults), IssuesFeed.class);
				if (Logger.logDebug()) {
					Logger.debug(fetchURI.toString() + "?start-index=" + startIndex + "&amp;max-results=" + maxResults);
				}
				feedEntries = resultFeed.getEntries();
			}
			
		} catch (final AuthenticationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final ServiceException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
}

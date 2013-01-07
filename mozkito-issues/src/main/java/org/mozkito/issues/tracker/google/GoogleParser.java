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
package org.mozkito.issues.tracker.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gdata.client.projecthosting.IssuesQuery;
import com.google.gdata.client.projecthosting.ProjectHostingService;
import com.google.gdata.data.HtmlTextConstruct;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.projecthosting.BlockedOn;
import com.google.gdata.data.projecthosting.Blocking;
import com.google.gdata.data.projecthosting.IssueCommentsEntry;
import com.google.gdata.data.projecthosting.IssueCommentsFeed;
import com.google.gdata.data.projecthosting.IssuesEntry;
import com.google.gdata.data.projecthosting.IssuesFeed;
import com.google.gdata.data.projecthosting.Label;
import com.google.gdata.data.projecthosting.Owner;
import com.google.gdata.data.projecthosting.Updates;
import com.google.gdata.util.ServiceException;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.mozkito.issues.tracker.Parser;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.elements.Priority;
import org.mozkito.issues.tracker.elements.Resolution;
import org.mozkito.issues.tracker.elements.Severity;
import org.mozkito.issues.tracker.elements.Status;
import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.issues.tracker.model.AttachmentEntry;
import org.mozkito.issues.tracker.model.Comment;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.persistence.model.Person;

/**
 * The Class GoogleParser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GoogleParser implements Parser {
	
	/**
	 * Resolve priority.
	 * 
	 * @param priority
	 *            the priority
	 * @return the priority
	 */
	public static Priority resolvePriority(final String priority) {
		switch (priority) {
			case "critical":
				return Priority.VERY_HIGH;
			case "high":
				return Priority.HIGH;
			case "medium":
				return Priority.NORMAL;
			case "low":
				return Priority.LOW;
			default:
				return Priority.UNKNOWN;
		}
	}
	
	/**
	 * Resolve resolution.
	 * 
	 * @param resolution
	 *            the status
	 * @return the resolution
	 */
	private static Resolution resolveResolution(final String resolution) {
		// PRECONDITIONS
		
		try {
			
			switch (resolution) {
				case "duplicate":
					return Resolution.DUPLICATE;
				case "fixed":
					return Resolution.RESOLVED;
				case "invalid":
					return Resolution.INVALID;
				case "knownquirk":
					return Resolution.INVALID;
				case "notplanned":
					return Resolution.INVALID;
				default:
					return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Resolve status.
	 * 
	 * @param status
	 *            the status
	 * @return the status
	 */
	private static Status resolveStatus(final String status) {
		// PRECONDITIONS
		
		try {
			switch (status) {
			
				case "started":
					return Status.IN_PROGRESS;
				case "accepted":
					return Status.ASSIGNED;
				case "fixednotreleased":
					return Status.IN_PROGRESS;
				case "needsinfo":
					return Status.FEEDBACK;
				case "new":
					return Status.NEW;
				case "reviewpending":
					return Status.REVIEWPENDING;
				case "fixed":
				case "duplicate":
				case "invalid":
				case "knownquirk":
				case "notplanned":
					return Status.CLOSED;
				default:
					return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Resolve type.
	 * 
	 * @param type
	 *            the type
	 * @return the type
	 */
	public static Type resolveType(final String type) {
		switch (type) {
			case "defect":
				return Type.BUG;
			case "enhancement":
				return Type.RFE;
			case "task":
				return Type.TASK;
			case "docs":
				return Type.OTHER;
			case "----":
				return Type.UNKNOWN;
			case "feature":
				return Type.RFE;
			case "optimization":
				return Type.OTHER;
			default:
				return Type.UNKNOWN;
		}
	}
	
	/** The tracker. */
	private final GoogleTracker             tracker;
	
	/** The service. */
	private final ProjectHostingService     service;
	
	/** The issues entry. */
	private IssuesEntry                     issuesEntry;
	
	/** The fetch time. */
	private DateTime                        fetchTime;
	
	/** The resolver. */
	private Person                          resolver;
	
	/** The history. */
	private final SortedSet<HistoryElement> history  = new TreeSet<HistoryElement>();
	
	/** The comments. */
	private SortedSet<Comment>              comments = null;
	
	/** The resolution timestamp. */
	private DateTime                        resolutionTimestamp;
	
	/** The md5. */
	private byte[]                          md5;
	
	/**
	 * Instantiates a new google parser.
	 * 
	 * @param tracker
	 *            the tracker
	 * @param service
	 *            the service
	 */
	public GoogleParser(final GoogleTracker tracker, final ProjectHostingService service) {
		this.tracker = tracker;
		this.service = service;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			final Owner owner = this.issuesEntry.getOwner();
			if (owner != null) {
				return new Person(owner.getUsername().getValue(), null, null);
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getAttachmentEntries()
	 */
	@Override
	public List<AttachmentEntry> getAttachmentEntries() {
		// PRECONDITIONS
		
		try {
			// not supported by google API
			return new ArrayList<AttachmentEntry>(0);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComponent()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("category-")) {
					return label.substring("category-".length()).trim();
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		try {
			if (this.comments != null) {
				return this.comments;
			}
			this.comments = new TreeSet<Comment>();
			
			final String fetchUri = this.tracker.getIssuesFeedUri();
			
			final LinkedList<Tuple<Status, HistoryElement>> statusHistory = new LinkedList<Tuple<Status, HistoryElement>>();
			final LinkedList<Tuple<Resolution, HistoryElement>> resolutionHistory = new LinkedList<Tuple<Resolution, HistoryElement>>();
			final LinkedList<Tuple<String, HistoryElement>> summaryHistory = new LinkedList<Tuple<String, HistoryElement>>();
			
			if (!fetchUri.endsWith("/full")) {
				if (Logger.logError()) {
					Logger.error("Could not create feed for comments. getUri() returned unrecognized URI.");
				}
			} else {
				final String baseUri = fetchUri.substring(0, fetchUri.length() - "full".length());
				final String commentUri = baseUri + getId() + "/comments/full";
				try {
					
					int startIndex = 1;
					final int maxResults = 25;
					
					IssueCommentsFeed commentsFeed = this.service.getFeed(new URL(commentUri + "?start-index="
					        + startIndex + "&max-results=" + maxResults), IssueCommentsFeed.class);
					
					List<IssueCommentsEntry> commentEntries = commentsFeed.getEntries();
					
					while (commentEntries.size() > 0) {
						for (int i = 0; i < commentEntries.size(); i++) {
							
							final IssueCommentsEntry entry = commentEntries.get(i);
							Person author = Tracker.UNKNOWN_PERSON;
							if (!entry.getAuthors().isEmpty()) {
								final com.google.gdata.data.Person person = entry.getAuthors().get(0);
								
								if ((person.getName().contains("@"))
								        && ((person.getEmail() == null) || person.getEmail().isEmpty())) {
									author = new Person(null, null, person.getName());
								} else {
									author = new Person(null, person.getName(), person.getEmail());
								}
							}
							final com.google.gdata.data.DateTime published = entry.getPublished();
							final DateTime timestamp = new DateTime(published.getValue(),
							                                        DateTimeZone.forOffsetHours(published.getTzShift()));
							
							String commentText = "";
							final TextContent textContent = (TextContent) entry.getContent();
							if ((textContent != null) && (textContent.getContent() != null)) {
								final HtmlTextConstruct textConstruct = (HtmlTextConstruct) textContent.getContent();
								commentText = textConstruct.getPlainText();
							}
							this.comments.add(new Comment(i + startIndex, author, timestamp, commentText));
							
							if (entry.hasUpdates()) {
								final Updates updates = entry.getUpdates();
								updates.getBlockedOnUpdates();
								
								final HistoryElement hElem = new HistoryElement(getId(), author, timestamp);
								
								if (updates.getCcUpdates() != null) {
									// CCs are not supported by report
								}
								
								Tuple<Type, Type> typeUpdate = null;
								Tuple<Priority, Priority> priorityUpdate = null;
								Tuple<String, String> categoryUpdate = null;
								Tuple<String, String> milestoneUpdate = null;
								for (final Label l : updates.getLabels()) {
									final String label = l.getValue();
									final String compValue = l.getValue().toLowerCase();
									if (compValue.startsWith("type-")) {
										final String newValue = label.substring("type-".length()).trim();
										final Type newType = resolveType(newValue);
										if (typeUpdate == null) {
											typeUpdate = new Tuple<Type, Type>(Type.UNKNOWN, Type.UNKNOWN);
										}
										typeUpdate.setSecond(newType);
									} else if (compValue.startsWith("-type-")) {
										final String oldValue = label.substring("-type-".length()).trim();
										if (typeUpdate == null) {
											typeUpdate = new Tuple<Type, Type>(Type.UNKNOWN, Type.UNKNOWN);
										}
										typeUpdate.setFirst(resolveType(oldValue));
									} else if (compValue.startsWith("priority-")) {
										final String newValue = label.substring("priority-".length()).trim();
										if (priorityUpdate == null) {
											priorityUpdate = new Tuple<Priority, Priority>(Priority.UNKNOWN,
											                                               Priority.UNKNOWN);
										}
										priorityUpdate.setSecond(resolvePriority(newValue));
									} else if (compValue.startsWith("-priority-")) {
										final String oldValue = label.substring("-priority-".length()).trim();
										if (priorityUpdate == null) {
											priorityUpdate = new Tuple<Priority, Priority>(Priority.UNKNOWN,
											                                               Priority.UNKNOWN);
										}
										priorityUpdate.setFirst(resolvePriority(oldValue));
									} else if (compValue.startsWith("category-")) {
										final String newValue = label.substring("category-".length()).trim();
										if (categoryUpdate == null) {
											categoryUpdate = new Tuple<String, String>("", "");
										}
										categoryUpdate.setSecond(newValue);
									} else if (compValue.startsWith("-category-")) {
										final String oldValue = label.substring("-category-".length()).trim();
										if (categoryUpdate == null) {
											categoryUpdate = new Tuple<String, String>("", "");
										}
										categoryUpdate.setSecond(oldValue);
									} else if (compValue.startsWith("milestone-")) {
										final String newValue = label.substring("milestone-".length()).trim();
										if (milestoneUpdate == null) {
											milestoneUpdate = new Tuple<String, String>("", "");
										}
										milestoneUpdate.setSecond(newValue);
									} else if (compValue.startsWith("-milestone-")) {
										final String oldValue = label.substring("-milestone-".length()).trim();
										if (milestoneUpdate == null) {
											milestoneUpdate = new Tuple<String, String>("", "");
										}
										milestoneUpdate.setFirst(oldValue);
									} else {
										String keyword = label.trim();
										if (keyword.startsWith("-")) {
											keyword = keyword.substring(1);
											hElem.addChangedValue("keywords", keyword, null);
										} else {
											hElem.addChangedValue("keywords", null, keyword);
										}
									}
								}
								
								if (typeUpdate != null) {
									hElem.addChangedValue("type", typeUpdate.getFirst(), typeUpdate.getSecond());
								}
								if (priorityUpdate != null) {
									hElem.addChangedValue("priority", priorityUpdate.getFirst(),
									                      priorityUpdate.getSecond());
								}
								if (categoryUpdate != null) {
									hElem.addChangedValue("category", categoryUpdate.getFirst(),
									                      categoryUpdate.getSecond());
								}
								if (milestoneUpdate != null) {
									hElem.addChangedValue("version", milestoneUpdate.getFirst(),
									                      milestoneUpdate.getSecond());
								}
								
								if (updates.getOwnerUpdate() != null) {
									hElem.addChangedValue("assignedTo", Tracker.UNKNOWN_PERSON,
									                      new Person(updates.getOwnerUpdate().getValue(), null, null));
								}
								
								if (updates.getStatus() != null) {
									final String status = updates.getStatus().getValue().toLowerCase();
									Status newStatus = null;
									Resolution newResolution = null;
									switch (status) {
										case "started":
											newStatus = Status.IN_PROGRESS;
											break;
										case "accepted":
											newStatus = Status.ASSIGNED;
											break;
										case "fixednotreleased":
											this.resolver = author;
											newStatus = Status.IN_PROGRESS;
											break;
										case "needsinfo":
											newStatus = Status.FEEDBACK;
											break;
										case "new":
											newStatus = Status.NEW;
											break;
										case "reviewpending":
											newStatus = Status.REVIEWPENDING;
											break;
										case "duplicate":
											newResolution = Resolution.DUPLICATE;
											newStatus = Status.CLOSED;
											break;
										case "fixed":
											if (this.resolver == null) {
												this.resolver = author;
											}
											if (this.resolutionTimestamp == null) {
												this.resolutionTimestamp = hElem.getTimestamp();
											}
											
											newResolution = Resolution.RESOLVED;
											newStatus = Status.CLOSED;
											break;
										case "invalid":
											newResolution = Resolution.INVALID;
											newStatus = Status.CLOSED;
											break;
										case "knownquirk":
											newResolution = Resolution.INVALID;
											newStatus = Status.CLOSED;
											break;
										case "notplanned":
											newResolution = Resolution.INVALID;
											newStatus = Status.CLOSED;
											break;
										default:
											break;
									}
									if (newStatus != null) {
										statusHistory.addFirst(new Tuple<Status, HistoryElement>(newStatus, hElem));
									}
									if (newResolution != null) {
										resolutionHistory.addFirst(new Tuple<Resolution, HistoryElement>(newResolution,
										                                                                 hElem));
									}
								}
								
								if (updates.getSummary() != null) {
									final String newSummary = updates.getSummary().getValue();
									summaryHistory.addFirst(new Tuple<String, HistoryElement>(newSummary, hElem));
								}
								if (!hElem.isEmpty()) {
									this.history.add(hElem);
								}
							}
						}
						startIndex += maxResults;
						commentsFeed = this.service.getFeed(new URL(commentUri + "?start-index=" + startIndex
						        + "&max-results=" + maxResults), IssueCommentsFeed.class);
						commentEntries = commentsFeed.getEntries();
					}
					
					if (Logger.logTrace()) {
						Logger.trace("Rebuilding status history");
					}
					if (!statusHistory.isEmpty()) {
						Tuple<Status, HistoryElement> last = statusHistory.getFirst();
						last.getSecond().addChangedValue("status", Status.NEW, last.getFirst());
						for (int i = 1; i < statusHistory.size(); ++i) {
							final Tuple<Status, HistoryElement> next = statusHistory.get(i);
							next.getSecond().addChangedValue("status", last.getFirst(), next.getFirst());
							last = next;
						}
					}
					
					if (Logger.logTrace()) {
						Logger.trace("Rebuilding resolution history");
					}
					if (!resolutionHistory.isEmpty()) {
						Tuple<Resolution, HistoryElement> last = resolutionHistory.getFirst();
						last.getSecond().addChangedValue("resolution", Resolution.UNKNOWN, last.getFirst());
						for (int i = 1; i < resolutionHistory.size(); ++i) {
							final Tuple<Resolution, HistoryElement> next = resolutionHistory.get(i);
							next.getSecond().addChangedValue("resultion", last.getFirst(), next.getFirst());
							last = next;
						}
					}
					
					if (Logger.logTrace()) {
						Logger.trace("Rebuilding summary history");
					}
					if (!summaryHistory.isEmpty()) {
						Tuple<String, HistoryElement> last = summaryHistory.getFirst();
						last.getSecond().addChangedValue("summary", "<unknown>", last.getFirst());
						for (int i = 1; i < summaryHistory.size(); ++i) {
							final Tuple<String, HistoryElement> next = summaryHistory.get(i);
							next.getSecond().addChangedValue("summary", last.getFirst(), next.getFirst());
							last = next;
						}
					}
					
				} catch (final MalformedURLException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final ServiceException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
			return this.comments;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getDescription()
	 */
	
	@Override
	public String getComponent() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getHistoryElement(int)
	 */
	
	@Override
	public DateTime getCreationTimestamp() {
		// PRECONDITIONS
		
		try {
			final com.google.gdata.data.DateTime published = this.issuesEntry.getPublished();
			return new DateTime(published.getValue(), DateTimeZone.forOffsetHours(published.getTzShift()));
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getId()
	 */
	
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			if (this.issuesEntry.getContent() != null) {
				final TextContent content = (TextContent) this.issuesEntry.getContent();
				final TextConstruct lang = content.getContent();
				if (lang != null) {
					final String plainText = lang.getPlainText();
					if (plainText != null) {
						return plainText;
					}
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getNumberOfComments()
	 */
	
	@Override
	public DateTime getFetchTime() {
		// PRECONDITIONS
		
		try {
			return this.fetchTime;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getPriority()
	 */
	
	@Override
	public SortedSet<HistoryElement> getHistoryElements() {
		// PRECONDITIONS
		
		try {
			getComments();
			return this.history;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getProduct()
	 */
	
	@Override
	public String getId() {
		// PRECONDITIONS
		
		try {
			return this.issuesEntry.getIssueId().getValue().toString();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolution()
	 */
	
	@Override
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			final Set<String> result = new HashSet<String>();
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("type-")) {
					continue;
				} else if (compValue.startsWith("priority-")) {
					continue;
				} else if (compValue.startsWith("category-")) {
					continue;
				} else if (compValue.startsWith("milestone-")) {
					continue;
				} else {
					result.add(label);
				}
			}
			return result;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getLastUpdateTimestamp()
	 */
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			final com.google.gdata.data.DateTime edited = this.issuesEntry.getEdited();
			if (edited != null) {
				return new DateTime(edited.getValue(), DateTimeZone.forOffsetHours(edited.getTzShift()));
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getMd5()
	 */
	@Override
	public final byte[] getMd5() {
		return this.md5;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubject()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("priority-")) {
					final String priority = label.substring("priority-".length()).trim().toLowerCase();
					return resolvePriority(priority);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
	 */
	
	@Override
	public String getProduct() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getResolution()
	 */
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			if ((this.issuesEntry.getStatus() != null) && (this.issuesEntry.getStatus().getValue() != null)) {
				final String status = this.issuesEntry.getStatus().getValue().toLowerCase();
				return resolveResolution(status);
			}
			return Resolution.UNKNOWN;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			getComments();
			return this.resolutionTimestamp;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	
	@Override
	public Person getResolver() {
		// PRECONDITIONS
		
		try {
			getComments();
			return this.resolver;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
	 */
	
	@Override
	public String getScmFixVersion() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	
	@Override
	public Severity getSeverity() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getSiblings()
	 */
	@Override
	public Set<String> getSiblings() {
		// PRECONDITIONS
		
		try {
			final Set<String> siblings = new HashSet<String>();
			for (final BlockedOn b : this.issuesEntry.getBlockedOns()) {
				if (b.hasId()) {
					siblings.add(b.getId().getValue().toString());
				}
			}
			for (final Blocking b : this.issuesEntry.getBlockings()) {
				if (b.hasId()) {
					siblings.add(b.getId().getValue().toString());
				}
			}
			return siblings;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setXMLReport(org.mozkito.bugs.tracker.XmlReport )
	 */
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getStatus()
	 */
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			if ((this.issuesEntry.getStatus() != null) && (this.issuesEntry.getStatus().getValue() != null)) {
				final String status = this.issuesEntry.getStatus().getValue().toLowerCase();
				return resolveStatus(status);
			}
			return Status.UNKNOWN;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.issues.tracker.Parser#getSubject()
	 */
	@Override
	public String getSubject() {
		// PRECONDITIONS
		
		try {
			return this.issuesEntry.getTitle().getPlainText();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSubmitter()
	 */
	@Override
	public Person getSubmitter() {
		// PRECONDITIONS
		
		try {
			final List<com.google.gdata.data.Person> authors = this.issuesEntry.getAuthors();
			if (!authors.isEmpty()) {
				final com.google.gdata.data.Person person = authors.get(0);
				return new Person(null, person.getName(), person.getEmail());
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getSummary()
	 */
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			return getDescription();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getType()
	 */
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("type-")) {
					final String type = label.substring("type-".length()).trim();
					return resolveType(type.toLowerCase());
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#getVersion()
	 */
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("milestone-")) {
					return label.substring("milestone-".length()).trim();
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setTracker(org.mozkito.bugs.tracker.Tracker)
	 */
	@Override
	public void setTracker(final Tracker tracker) {
		// PRECONDITIONS
		
		try {
			return;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.Parser#setURI(java.net.URI)
	 */
	@Override
	public boolean setURI(final ReportLink reportLink) {
		// PRECONDITIONS
		
		try {
			try {
				final String bugId = reportLink.getBugId();
				
				final IssuesQuery iQuery = new IssuesQuery(new URL(this.tracker.getIssuesFeedUri()));
				iQuery.setId(Integer.valueOf(bugId));
				
				if (Logger.logDebug()) {
					Logger.debug("Fetching RawReport form url: " + iQuery.getFeedUrl().toString()
					        + iQuery.getQueryUri().toString());
				}
				
				final IssuesFeed resultFeed = this.service.query(iQuery, IssuesFeed.class);
				final List<IssuesEntry> entries = resultFeed.getEntries();
				
				CollectionCondition.minSize(entries, 1, "There has to be at least one entry in the issue list.");
				
				this.issuesEntry = entries.get(0);
				this.fetchTime = new DateTime();
				this.md5 = String.valueOf(this.issuesEntry.hashCode()).getBytes();
				if (this.issuesEntry == null) {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report #" + bugId + ". Feed returned no entries!");;
					}
					return false;
				}
				
				return true;
			} catch (final NumberFormatException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new UnrecoverableError("Got wrongly encoded URL.");
			} catch (final MalformedURLException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				return false;
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				return false;
			} catch (final ServiceException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				return false;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
}

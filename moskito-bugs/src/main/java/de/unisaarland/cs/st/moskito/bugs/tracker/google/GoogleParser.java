/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gdata.client.projecthosting.IssuesQuery;
import com.google.gdata.client.projecthosting.ProjectHostingService;
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

import de.unisaarland.cs.st.moskito.bugs.tracker.Parser;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Class GoogleParser.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class GoogleParser implements Parser {
	
	public static Priority resolvePriority(final String priority) {
		if (priority.equals("critical")) {
			return Priority.VERY_HIGH;
		} else if (priority.equals("high")) {
			return Priority.HIGH;
		} else if (priority.equals("medium")) {
			return Priority.NORMAL;
		} else if (priority.equals("low")) {
			return Priority.LOW;
		}
		return Priority.UNKNOWN;
	}
	
	private static Resolution resolveResolution(final String status) {
		// PRECONDITIONS
		
		try {
			if (status.equals("duplicate")) {
				return Resolution.DUPLICATE;
			} else if (status.equals("fixed")) {
				return Resolution.RESOLVED;
			} else if (status.equals("invalid")) {
				return Resolution.INVALID;
			} else if (status.equals("knownquirk")) {
				return Resolution.INVALID;
			} else if (status.equals("notplanned")) {
				return Resolution.INVALID;
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	private static Status resolveStatus(final String status) {
		// PRECONDITIONS
		
		try {
			if (status.equals("started")) {
				return Status.IN_PROGRESS;
			} else if (status.equals("accepted")) {
				return Status.ASSIGNED;
			} else if (status.equals("fixednotreleased")) {
				return Status.IN_PROGRESS;
			} else if (status.equals("needsinfo")) {
				return Status.FEEDBACK;
			} else if (status.equals("new")) {
				return Status.NEW;
			} else if (status.equals("reviewpending")) {
				return Status.REVIEWPENDING;
			} else if (status.equals("fixed") || status.equals("duplicate") || status.equals("invalid")
			        || status.equals("knownquirk") || status.equals("notplanned")) {
				return Status.CLOSED;
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public static Type resolveType(final String type) {
		if (type.equals("defect")) {
			return Type.BUG;
		} else if (type.equals("enhancement")) {
			return Type.RFE;
		} else if (type.equals("task")) {
			return Type.TASK;
		} else if (type.equals("docs")) {
			return Type.OTHER;
		} else if (type.equals("----")) {
			return Type.UNKNOWN;
		} else if (type.equals("feature")) {
			return Type.RFE;
		} else if (type.equals("optimization")) {
			return Type.OTHER;
		}
		return Type.UNKNOWN;
		
	}
	
	/** The tracker. */
	private final GoogleTracker             tracker;
	
	/** The service. */
	private final ProjectHostingService     service;
	
	/** The issues entry. */
	private IssuesEntry                     issuesEntry;
	
	private DateTime                        fetchTime;
	
	private Person                          resolver;
	
	private final SortedSet<HistoryElement> history  = new TreeSet<HistoryElement>();
	
	private SortedSet<Comment>              comments = null;
	
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAssignedTo()
	 */
	@Override
	public Person getAssignedTo() {
		// PRECONDITIONS
		
		try {
			final Owner owner = this.issuesEntry.getOwner();
			return new Person(owner.getUsername().getValue(), null, null);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getAttachmentEntries()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComponent()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCategory()
	 */
	@Override
	public String getCategory() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("category-")) {
					return label.substring(9).trim();
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getCreationTimestamp()
	 */
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getComments()
	 */
	@Override
	public SortedSet<Comment> getComments() {
		// PRECONDITIONS
		
		try {
			if (this.comments != null) {
				return this.comments;
			}
			this.comments = new TreeSet<Comment>();
			final String fetchUri = this.tracker.getUri().toASCIIString();
			if (!fetchUri.endsWith("/full")) {
				if (Logger.logError()) {
					Logger.error("Could not create feed for comments. getUri() returned unrecognized URI.");
				}
			} else {
				final String baseUri = fetchUri.substring(0, fetchUri.length() - 4);
				final String commentUri = baseUri + getId() + "/comments/full";
				try {
					final IssueCommentsFeed commentsFeed = this.service.getFeed(new URL(commentUri),
					                                                            IssueCommentsFeed.class);
					
					for (int i = 0; i < commentsFeed.getEntries().size(); i++) {
						
						final IssueCommentsEntry entry = commentsFeed.getEntries().get(i);
						Person author = Tracker.unknownPerson;
						if (!entry.getAuthors().isEmpty()) {
							final com.google.gdata.data.Person person = entry.getAuthors().get(0);
							author = new Person(null, person.getName(), person.getEmail());
						}
						final com.google.gdata.data.DateTime published = entry.getPublished();
						final DateTime timestamp = new DateTime(published.getValue(),
						                                        DateTimeZone.forOffsetHours(published.getTzShift()));
						
						this.comments.add(new Comment(i, author, timestamp, entry.getPlainTextContent()));
						
						Status lastStatus = null;
						Resolution lastResolution = null;
						String lastSummary = null;
						
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
									final String newValue = label.substring(5).trim();
									final Type newType = resolveType(newValue);
									if (typeUpdate == null) {
										typeUpdate = new Tuple<Type, Type>(Type.UNKNOWN, Type.UNKNOWN);
									}
									typeUpdate.setSecond(newType);
								} else if (compValue.startsWith("-type-")) {
									final String oldValue = label.substring(6).trim();
									if (typeUpdate == null) {
										typeUpdate = new Tuple<Type, Type>(Type.UNKNOWN, Type.UNKNOWN);
									}
									typeUpdate.setFirst(resolveType(oldValue));
								} else if (compValue.startsWith("priority-")) {
									final String newValue = label.substring(9).trim();
									if (priorityUpdate == null) {
										priorityUpdate = new Tuple<Priority, Priority>(Priority.UNKNOWN,
										                                               Priority.UNKNOWN);
									}
									priorityUpdate.setSecond(resolvePriority(newValue));
								} else if (compValue.startsWith("-priority-")) {
									final String oldValue = label.substring(10).trim();
									if (priorityUpdate == null) {
										priorityUpdate = new Tuple<Priority, Priority>(Priority.UNKNOWN,
										                                               Priority.UNKNOWN);
									}
									priorityUpdate.setFirst(resolvePriority(oldValue));
								} else if (compValue.startsWith("category-")) {
									final String newValue = label.substring(9).trim();
									if (categoryUpdate == null) {
										categoryUpdate = new Tuple<String, String>("", "");
									}
									categoryUpdate.setSecond(newValue);
								} else if (compValue.startsWith("-category-")) {
									final String oldValue = label.substring(10).trim();
									if (categoryUpdate == null) {
										categoryUpdate = new Tuple<String, String>("", "");
									}
									categoryUpdate.setSecond(oldValue);
								} else if (compValue.startsWith("milestone-")) {
									final String newValue = label.substring(10).trim();
									if (milestoneUpdate == null) {
										milestoneUpdate = new Tuple<String, String>("", "");
									}
									milestoneUpdate.setSecond(newValue);
								} else if (compValue.startsWith("-milestone-")) {
									final String oldValue = label.substring(11).trim();
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
								hElem.addChangedValue("priority", priorityUpdate.getFirst(), priorityUpdate.getSecond());
							}
							if (categoryUpdate != null) {
								hElem.addChangedValue("category", categoryUpdate.getFirst(), categoryUpdate.getSecond());
							}
							if (milestoneUpdate != null) {
								hElem.addChangedValue("version", milestoneUpdate.getFirst(),
								                      milestoneUpdate.getSecond());
							}
							
							if (updates.getOwnerUpdate() != null) {
								hElem.addChangedValue("assignedTo", Tracker.unknownPerson,
								                      new Person(updates.getOwnerUpdate().getValue(), null, null));
							}
							
							if (updates.getStatus() != null) {
								final String status = updates.getStatus().getValue().toLowerCase();
								Status newStatus = null;
								Resolution newResolution = null;
								if (status.equals("started")) {
									newStatus = Status.IN_PROGRESS;
								} else if (status.equals("accepted")) {
									newStatus = Status.ASSIGNED;
								} else if (status.equals("fixednotreleased")) {
									this.resolver = author;
									newStatus = Status.IN_PROGRESS;
								} else if (status.equals("needsinfo")) {
									newStatus = Status.FEEDBACK;
								} else if (status.equals("new")) {
									newStatus = Status.NEW;
								} else if (status.equals("reviewpending")) {
									newStatus = Status.REVIEWPENDING;
								} else if (status.equals("duplicate")) {
									newResolution = Resolution.DUPLICATE;
									newStatus = Status.CLOSED;
								} else if (status.equals("fixed")) {
									this.resolver = author;
									newResolution = Resolution.RESOLVED;
									newStatus = Status.CLOSED;
								} else if (status.equals("invalid")) {
									newResolution = Resolution.INVALID;
									newStatus = Status.CLOSED;
								} else if (status.equals("knownquirk")) {
									newResolution = Resolution.INVALID;
									newStatus = Status.CLOSED;
								} else if (status.equals("notplanned")) {
									newResolution = Resolution.INVALID;
									newStatus = Status.CLOSED;
								}
								if (newStatus != null) {
									hElem.addChangedValue("status", lastStatus, newStatus);
									lastStatus = newStatus;
								}
								if (newResolution != null) {
									hElem.addChangedValue("resolution", lastResolution, newResolution);
									lastResolution = newResolution;
								}
							}
							
							if (updates.getSummary() != null) {
								final String newSummary = updates.getSummary().getValue();
								hElem.addChangedValue("summary", lastSummary, newSummary);
								lastSummary = newSummary;
							}
							this.history.add(hElem);
						}
					}
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
				}
			}
			return this.comments;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getDescription()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getHistoryElement(int)
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getId()
	 */
	
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return this.issuesEntry.getPlainTextContent();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getNumberOfComments()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getPriority()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getProduct()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolution()
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
	
	@Override
	public DateTime getLastUpdateTimestamp() {
		// PRECONDITIONS
		
		try {
			final com.google.gdata.data.DateTime edited = this.issuesEntry.getEdited();
			return new DateTime(edited.getValue(), DateTimeZone.forOffsetHours(edited.getTzShift()));
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getResolutionTimestamp()
	 */
	@Override
	public Priority getPriority() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("priority-")) {
					final String priority = label.substring(9).trim().toLowerCase();
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubject()
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
	 */
	
	@Override
	public Resolution getResolution() {
		// PRECONDITIONS
		
		try {
			final String status = this.issuesEntry.getStatus().getValue().toLowerCase();
			return resolveResolution(status);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public DateTime getResolutionTimestamp() {
		// PRECONDITIONS
		
		try {
			if (this.issuesEntry.getClosedDate() != null) {
				final com.google.gdata.data.DateTime closedDate = this.issuesEntry.getClosedDate().getValue();
				if (closedDate != null) {
					return new DateTime(closedDate.getValue(), DateTimeZone.forOffsetHours(closedDate.getTzShift()));
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
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
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
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
	
	@Override
	public Status getStatus() {
		// PRECONDITIONS
		
		try {
			final String status = this.issuesEntry.getStatus().getValue().toLowerCase();
			return resolveStatus(status);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setXMLReport(de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport
	 * )
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSubmitter()
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getSummary()
	 */
	@Override
	public String getSummary() {
		// PRECONDITIONS
		
		try {
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getType()
	 */
	@Override
	public Type getType() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("type-")) {
					final String type = label.substring(5).trim();
					return resolveType(type);
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#getVersion()
	 */
	@Override
	public String getVersion() {
		// PRECONDITIONS
		
		try {
			for (final Label l : this.issuesEntry.getLabels()) {
				final String label = l.getValue();
				final String compValue = l.getValue().toLowerCase();
				if (compValue.startsWith("milestone-")) {
					return label.substring(10).trim();
				}
			}
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setTracker(de.unisaarland.cs.st.moskito.bugs.tracker.Tracker)
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
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.Parser#setURI(java.net.URI)
	 */
	@Override
	public boolean setURI(final ReportLink reportLink) {
		// PRECONDITIONS
		
		try {
			try {
				final String bugId = reportLink.getBugId();
				final IssuesQuery iQuery = new IssuesQuery(this.tracker.getUri().toURL());
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
				
				if (this.issuesEntry == null) {
					if (Logger.logWarn()) {
						Logger.warn("Skipping report #" + bugId + ". Feed returned no entries!");;
					}
					return false;
				}
				
				return true;
			} catch (final NumberFormatException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError("Got wrongly encoded URL.");
			} catch (final MalformedURLException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			} catch (final ServiceException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				return false;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

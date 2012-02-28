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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.comparators.CommentComparator;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "report")
public class Report implements Annotated, Comparable<Report> {
	
	private static final long     serialVersionUID  = 3241584366125944268L;
	private List<AttachmentEntry> attachmentEntries = new LinkedList<AttachmentEntry>();
	private String                category;
	private SortedSet<Comment>    comments          = new TreeSet<Comment>(new CommentComparator());
	private String                component;
	private DateTime              creationTimestamp;
	private String                description;
	private byte[]                hash              = new byte[33];
	private History               history;
	private long                  id                = -1l;
	private DateTime              lastFetch;
	private DateTime              lastUpdateTimestamp;
	// assignedTo
	// submitter
	// resolver
	private PersonContainer       personContainer   = new PersonContainer();
	private Priority              priority          = Priority.UNKNOWN;
	private String                product;
	private Resolution            resolution        = Resolution.UNKNOWN;
	private DateTime              resolutionTimestamp;
	private Severity              severity          = Severity.UNKNOWN;
	private SortedSet<Long>       siblings          = new TreeSet<Long>();
	private Status                status            = Status.UNKNOWN;
	private String                subject;
	private String                summary;
	private Type                  type              = Type.UNKNOWN;
	private String                version;
	private String                scmFixVersion;
	
	private Set<String>           keywords          = new HashSet<String>();
	
	private Report() {
		super();
	}
	
	/**
	 * @param id
	 */
	public Report(final long id) {
		this();
		setId(id);
		setHistory(new History(getId()));
	}
	
	/**
	 * @param entry
	 */
	@Transient
	public void addAttachmentEntry(final AttachmentEntry entry) {
		getAttachmentEntries().add(entry);
		setAttachmentEntries(getAttachmentEntries());
	}
	
	/**
	 * @param comment
	 * @return
	 */
	@Transient
	public boolean addComment(@NotNull final Comment comment) {
		Condition.notNull(getComments(),
		                  "The container holding the comments must be initialized before adding a comment to the report.");
		CollectionCondition.notContains(getComments(), comment,
		                                "The comment with id %d is already contained in the report.", comment.getId());
		
		final SortedSet<Comment> comments = getComments();
		final boolean ret = comments.add(comment);
		setComments(comments);
		comment.setBugReport(this);
		Condition.check(ret, "Could not add comment with id %s (already existing).", comment.getId());
		return ret;
	}
	
	/**
	 * @param historyElement
	 * @return
	 */
	@Transient
	public boolean addHistoryElement(@NotNull final HistoryElement historyElement) {
		Condition.notNull(getHistory(), "The history handler must not be null when adding a history element.");
		final History history = getHistory();
		final boolean ret = history.add(historyElement);
		setHistory(history);
		historyElement.setBugId(getId());
		return ret;
	}
	
	@Transient
	public boolean addKeyword(final String keyword) {
		return this.keywords.add(keyword);
	}
	
	/**
	 * @param sibling
	 * @return
	 */
	@Transient
	public boolean addSibling(@NotNull final Long sibling) {
		Condition.notNull(getSiblings(), "The sibling handler must not be null when adding a sibling.");
		final SortedSet<Long> siblings = getSiblings();
		final boolean ret = siblings.add(sibling);
		setSiblings(siblings);
		return ret;
	}
	
	@Override
	protected Report clone() throws CloneNotSupportedException {
		final Report report = new Report();
		report.setCategory(getCategory());
		report.setComments(getComments());
		report.setDescription(getDescription());
		report.setSeverity(getSeverity());
		report.setPriority(getPriority());
		report.setResolution(getResolution());
		report.setSubject(getSubject());
		report.setSiblings(getSiblings());
		report.setHistory(getHistory());
		report.setStatus(getStatus());
		report.setType(getType());
		report.setCreationTimestamp(getCreationTimestamp());
		report.setResolutionTimestamp(getResolutionTimestamp());
		report.setLastUpdateTimestamp(getLastUpdateTimestamp());
		report.setLastFetch(getLastFetch());
		report.setVersion(getVersion());
		report.setSummary(getSummary());
		report.setComponent(getComponent());
		report.setProduct(getProduct());
		report.setAssignedTo(getAssignedTo());
		report.setResolver(getResolver());
		report.setSubmitter(getSubmitter());
		return report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Report o) {
		if (getId() > o.getId()) {
			return 1;
		} else if (getId() < o.getId()) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * @return the assignedTo
	 */
	@Transient
	public Person getAssignedTo() {
		return getPersonContainer().get("assignedTo");
	}
	
	/**
	 * @return the attachmentEntries
	 */
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public List<AttachmentEntry> getAttachmentEntries() {
		return this.attachmentEntries;
	}
	
	/**
	 * @return the category
	 */
	@Basic
	public String getCategory() {
		return this.category;
	}
	
	/**
	 * @return the comments
	 */
	@OrderBy
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public SortedSet<Comment> getComments() {
		return this.comments;
	}
	
	/**
	 * @return
	 */
	@Basic
	public String getComponent() {
		return this.component;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "creationTimestamp")
	private Date getCreationJavaTimestamp() {
		return getCreationTimestamp() != null
		                                     ? getCreationTimestamp().toDate()
		                                     : null;
	}
	
	/**
	 * @return the creationTimestamp
	 */
	@Transient
	public DateTime getCreationTimestamp() {
		return this.creationTimestamp;
	}
	
	/**
	 * @return the description
	 */
	@Basic
	@Lob
	@Column (columnDefinition = "TEXT")
	public String getDescription() {
		return this.description;
	}
	
	public Object getField(final String lowerFieldName) {
		final Method[] methods = this.getClass().getDeclaredMethods();
		final String getter = "get" + lowerFieldName;
		
		for (final Method method : methods) {
			if (method.getName().equalsIgnoreCase(getter)) {
				try {
					return method.invoke(this);
				} catch (final IllegalArgumentException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (final IllegalAccessException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (final InvocationTargetException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
			}
		}
		
		if (Logger.logWarn()) {
			Logger.warn("Did not find a matching field for: " + lowerFieldName);
		}
		
		return null;
	}
	
	/**
	 * @return the hash
	 */
	@Basic
	public byte[] getHash() {
		return this.hash;
	}
	
	/**
	 * @return the history
	 */
	@OneToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public History getHistory() {
		return this.history;
	}
	
	/**
	 * @return the id
	 */
	@Id
	public long getId() {
		return this.id;
	}
	
	@ElementCollection
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			System.err.println("getKeywords: " + this.keywords);
			return this.keywords;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @return the lastFetch
	 */
	@Transient
	public DateTime getLastFetch() {
		return this.lastFetch;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastFetch")
	private Date getLastFetchJava() {
		return getLastFetch() != null
		                             ? getLastFetch().toDate()
		                             : null;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastUpdateTimestamp")
	private Date getLastUpdateJavaTimestamp() {
		return getLastUpdateTimestamp() != null
		                                       ? getLastUpdateTimestamp().toDate()
		                                       : null;
	}
	
	/**
	 * @return
	 */
	@Transient
	public DateTime getLastUpdateTimestamp() {
		return this.lastUpdateTimestamp;
	}
	
	/**
	 * @return the personContainer
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/**
	 * @return the priority
	 */
	@Enumerated (EnumType.ORDINAL)
	public Priority getPriority() {
		return this.priority;
	}
	
	/**
	 * @return
	 */
	@Basic
	public String getProduct() {
		return this.product;
	}
	
	/**
	 * @return the resolution
	 */
	@Enumerated (EnumType.ORDINAL)
	public Resolution getResolution() {
		return this.resolution;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "resolutionTimestamp")
	private Date getResolutionJavaTimestamp() {
		return getResolutionTimestamp() != null
		                                       ? getResolutionTimestamp().toDate()
		                                       : null;
	}
	
	/**
	 * @return
	 */
	@Transient
	public DateTime getResolutionTimestamp() {
		return this.resolutionTimestamp;
	}
	
	/**
	 * @return the resolver
	 */
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Transient
	public Person getResolver() {
		return getPersonContainer().get("resolver");
	}
	
	public String getScmFixVersion() {
		// PRECONDITIONS
		
		try {
			System.err.println("getScmFixRevision: " + this.scmFixVersion);
			return this.scmFixVersion;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @return the severity
	 */
	@Enumerated (EnumType.ORDINAL)
	public Severity getSeverity() {
		return this.severity;
	}
	
	/**
	 * @return the siblings
	 */
	@ElementCollection
	@OrderBy
	public SortedSet<Long> getSiblings() {
		return this.siblings;
	}
	
	/**
	 * @return the status
	 */
	@Enumerated (EnumType.ORDINAL)
	public Status getStatus() {
		return this.status;
	}
	
	/**
	 * @return the subject
	 */
	@Basic
	@Lob
	@Column (columnDefinition = "TEXT")
	public String getSubject() {
		return this.subject;
	}
	
	/**
	 * @return the submitter
	 */
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Transient
	public Person getSubmitter() {
		return getPersonContainer().get("submitter");
	}
	
	/**
	 * @return the summary
	 */
	@Basic
	@Lob
	@Column (columnDefinition = "TEXT")
	public String getSummary() {
		return this.summary;
	}
	
	/**
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		return this.type;
	}
	
	/**
	 * @return the version
	 */
	@Basic
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param assignedTo
	 *            the assignedTo to set
	 */
	public void setAssignedTo(final Person assignedTo) {
		getPersonContainer().add("assignedTo", assignedTo);
	}
	
	/**
	 * @param attachmentEntries
	 *            the attachmentEntries to set
	 */
	public void setAttachmentEntries(final List<AttachmentEntry> attachmentEntries) {
		this.attachmentEntries = attachmentEntries;
	}
	
	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(final String category) {
		this.category = category;
	}
	
	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(final SortedSet<Comment> comments) {
		this.comments = comments;
	}
	
	/**
	 * @param component
	 */
	public void setComponent(final String component) {
		this.component = component;
	}
	
	/**
	 * @param creationTimestamp
	 */
	@SuppressWarnings ("unused")
	private void setCreationJavaTimestamp(final Date creationTimestamp) {
		setCreationTimestamp(creationTimestamp != null
		                                              ? new DateTime(creationTimestamp)
		                                              : null);
	}
	
	/**
	 * @param creationTimestamp
	 *            the creationTimestamp to set
	 */
	public void setCreationTimestamp(final DateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * @param fieldName
	 * @param fieldValue
	 */
	@Transient
	public void setField(final String fieldName,
	                     final Object fieldValue) {
		final String lowerFieldName = fieldName.toLowerCase();
		final Method[] methods = this.getClass().getDeclaredMethods();
		final String getter = "set" + lowerFieldName;
		
		for (final Method method : methods) {
			if (method.getName().equalsIgnoreCase(getter) && (method.getParameterTypes().length == 1)
			        && ((fieldValue == null) || (method.getParameterTypes()[0] == fieldValue.getClass()))) {
				try {
					method.invoke(this, fieldValue);
				} catch (final IllegalArgumentException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (final IllegalAccessException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (final InvocationTargetException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
			}
		}
		
		if (Logger.logWarn()) {
			Logger.warn("Did not find a matching field for: " + lowerFieldName);
		}
	}
	
	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(final byte[] hash) {
		this.hash = hash;
	}
	
	/**
	 * @param history
	 *            the history to set
	 */
	private void setHistory(final History history) {
		this.history = history;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	private void setId(final long id) {
		this.id = id;
	}
	
	public void setKeywords(final Set<String> keywords) {
		// PRECONDITIONS
		try {
			this.keywords = keywords;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.keywords, keywords,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/**
	 * @param lastFetch
	 *            the lastFetch to set
	 */
	public void setLastFetch(final DateTime lastFetch) {
		this.lastFetch = lastFetch;
	}
	
	/**
	 * @param lastFetch
	 */
	@SuppressWarnings ("unused")
	private void setLastFetchJava(final Date lastFetch) {
		setLastFetch(lastFetch != null
		                              ? new DateTime(lastFetch)
		                              : null);
	}
	
	/**
	 * @param date
	 */
	@SuppressWarnings ("unused")
	private void setLastUpdateJavaTimestamp(final Date date) {
		setLastUpdateTimestamp(date != null
		                                   ? new DateTime(date)
		                                   : null);
	}
	
	/**
	 * @param lastUpdateTimestamp
	 */
	public void setLastUpdateTimestamp(final DateTime lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	
	/**
	 * @param personContainer
	 *            the personContainer to set
	 */
	public void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(final Priority priority) {
		this.priority = priority;
	}
	
	/**
	 * @param product
	 */
	public void setProduct(final String product) {
		this.product = product;
	}
	
	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(final Resolution resolution) {
		this.resolution = resolution;
	}
	
	/**
	 * @param date
	 */
	@SuppressWarnings ("unused")
	private void setResolutionJavaTimestamp(final Date date) {
		setResolutionTimestamp(date != null
		                                   ? new DateTime(date)
		                                   : null);
	}
	
	/**
	 * @param resolutionTimestamp
	 */
	public void setResolutionTimestamp(final DateTime resolutionTimestamp) {
		this.resolutionTimestamp = resolutionTimestamp;
	}
	
	/**
	 * @param resolver
	 *            the resolver to set
	 */
	public void setResolver(final Person resolver) {
		getPersonContainer().add("resolver", resolver);
	}
	
	public void setScmFixVersion(final String scmFixVersion) {
		// PRECONDITIONS
		try {
			this.scmFixVersion = scmFixVersion;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(final Severity severity) {
		this.severity = severity;
	}
	
	/**
	 * @param siblings
	 *            the siblings to set
	 */
	public void setSiblings(final SortedSet<Long> siblings) {
		this.siblings = siblings;
	}
	
	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}
	
	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}
	
	/**
	 * @param submitter
	 *            the submitter to set
	 */
	public void setSubmitter(final Person submitter) {
		getPersonContainer().add("submitter", submitter);
	}
	
	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(final String summary) {
		this.summary = summary;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}
	
	/**
	 * @param timestamp
	 * @return
	 */
	public Report timewarp(final DateTime timestamp) {
		return getHistory().rollback(this, timestamp);
	}
	
	/**
	 * @param interval
	 * @return
	 */
	public Collection<Report> timewarp(final Interval interval,
	                                   final String field) {
		final LinkedList<Report> reports = new LinkedList<Report>();
		Report report = timewarp(interval.getEnd());
		
		final History history = report.getHistory().get(field);
		
		final LinkedList<HistoryElement> list = new LinkedList<HistoryElement>(history.getElements());
		final ListIterator<HistoryElement> iterator = list.listIterator(list.size());
		while (iterator.hasPrevious()) {
			final HistoryElement element = iterator.previous();
			if (interval.contains(element.getTimestamp())) {
				final History historyPatch = new History(getId());
				historyPatch.add(element);
				reports.add(report = historyPatch.rollback(report, element.getTimestamp()));
			} else {
				break;
			}
		}
		
		return reports;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	@Transient
	public String toString() {
		String hash;
		try {
			hash = JavaUtils.byteArrayToHexString(getHash());
		} catch (final UnsupportedEncodingException e) {
			hash = "encoding failed"; // this will never be executed
		}
		return "BugReport [id="
		        + getId()
		        + ", assignedTo="
		        + getAssignedTo()
		        + ", category="
		        + getCategory()
		        + ", comments="
		        + (getComments() != null
		                                ? getComments().size()
		                                : 0)
		        + ", description="
		        + getDescription().substring(0,
		                                     getDescription().length() > 10
		                                                                   ? 10
		                                                                   : Math.max(getDescription().length() - 1, 0))
		        + "... , severity=" + getSeverity() + ", priority=" + getPriority() + ", resolution=" + getResolution()
		        + ", submitter=" + getSubmitter() + ", subject="
		        + getSubject().substring(0, getSubject().length() > 10
		                                                              ? 10
		                                                              : Math.max(getSubject().length() - 1, 0))
		        + "... , resolver=" + getResolver() + ", status=" + getStatus() + ", type=" + getType()
		        + ", creationTimestamp=" + getCreationTimestamp() + ", lastFetch=" + getLastFetch() + ", hash=" + hash
		        + "]";
	}
	
}

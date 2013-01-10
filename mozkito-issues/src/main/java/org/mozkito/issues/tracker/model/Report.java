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
package org.mozkito.issues.tracker.model;

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
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.mozkito.issues.tracker.elements.Priority;
import org.mozkito.issues.tracker.elements.Resolution;
import org.mozkito.issues.tracker.elements.Severity;
import org.mozkito.issues.tracker.elements.Status;
import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.issues.tracker.model.comparators.CommentComparator;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;

/**
 * The Class Report.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "report")
public class Report implements Annotated, Comparable<Report> {
	
	private static final int      HASH_SIZE         = 33;
	
	/** The Constant serialVersionUID. */
	private static final long     serialVersionUID  = 3241584366125944268L;
	
	/** The attachment entries. */
	private List<AttachmentEntry> attachmentEntries = new LinkedList<AttachmentEntry>();
	
	/** The category. */
	private String                category;
	
	/** The comments. */
	private SortedSet<Comment>    comments          = new TreeSet<Comment>(new CommentComparator());
	
	/** The component. */
	private String                component;
	
	/** The creation timestamp. */
	private DateTime              creationTimestamp;
	
	/** The description. */
	private String                description;
	
	/** The hash. */
	private byte[]                hash              = new byte[Report.HASH_SIZE];
	
	/** The history. */
	private History               history;
	
	/** The id. */
	private String                id;
	
	/** The last fetch. */
	private DateTime              lastFetch;
	
	/** The last update timestamp. */
	private DateTime              lastUpdateTimestamp;
	// assignedTo
	// submitter
	// resolver
	/** The person container. */
	private PersonContainer       personContainer   = new PersonContainer();
	
	/** The priority. */
	private Priority              priority          = Priority.UNKNOWN;
	/** The product. */
	private String                product;
	
	/** The resolution. */
	private Resolution            resolution        = Resolution.UNKNOWN;
	
	/** The resolution timestamp. */
	private DateTime              resolutionTimestamp;
	
	/** The severity. */
	private Severity              severity          = Severity.UNKNOWN;
	
	/** The siblings. */
	private SortedSet<String>     siblings          = new TreeSet<String>();
	
	/** The status. */
	private Status                status            = Status.UNKNOWN;
	
	/** The subject. */
	private String                subject;
	
	/** The summary. */
	private String                summary;
	
	/** The type. */
	private Type                  type              = Type.UNKNOWN;
	
	/** The version. */
	private String                version;
	
	/** The scm fix version. */
	private String                scmFixVersion;
	
	/** The keywords. */
	private Set<String>           keywords          = new HashSet<String>();
	
	/**
	 * Instantiates a new report.
	 * 
	 * @deprecated Should never be used. Exists to fulfill OpenJPA requirements.
	 */
	@Deprecated
	public Report() {
		super();
	}
	
	/**
	 * Instantiates a new report.
	 * 
	 * @param id
	 *            the id
	 */
	public Report(final String id) {
		this();
		setId(id);
		setHistory(new History(getId()));
	}
	
	/**
	 * Adds the attachment entry.
	 * 
	 * @param entry
	 *            the entry
	 */
	@Transient
	public void addAttachmentEntry(final AttachmentEntry entry) {
		getAttachmentEntries().add(entry);
		setAttachmentEntries(getAttachmentEntries());
	}
	
	/**
	 * Adds the comment.
	 * 
	 * @param comment
	 *            the comment
	 * @return true, if successful
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
	 * Adds the history element.
	 * 
	 * @param historyElement
	 *            the history element
	 * @return true, if successful
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
	
	/**
	 * Adds the keyword.
	 * 
	 * @param keyword
	 *            the keyword
	 * @return true, if successful
	 */
	@Transient
	public boolean addKeyword(final String keyword) {
		return getKeywords().add(keyword);
	}
	
	/**
	 * Adds the sibling.
	 * 
	 * @param sibling
	 *            the sibling
	 * @return true, if successful
	 */
	@Transient
	public boolean addSibling(@NotNull final String sibling) {
		Condition.notNull(getSiblings(), "The sibling handler must not be null when adding a sibling."); //$NON-NLS-1$
		final SortedSet<String> siblings = getSiblings();
		final boolean ret = siblings.add(sibling);
		setSiblings(siblings);
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
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
		if (getAssignedTo() != null) {
			report.setAssignedTo(getAssignedTo());
		}
		if (getResolver() != null) {
			report.setResolver(getResolver());
		}
		if (getSubmitter() != null) {
			report.setSubmitter(getSubmitter());
		}
		return report;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Report o) {
		final int comp = Integer.valueOf(getId().length()).compareTo(Integer.valueOf(o.getId().length()));
		if (comp == 0) {
			return getId().compareTo(o.getId());
		}
		return comp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Report other = (Report) obj;
		if (getId() == null) {
			if (getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the assigned to.
	 * 
	 * @return the assignedTo
	 */
	@Transient
	public Person getAssignedTo() {
		return getPersonContainer() != null
		                                   ? getPersonContainer().get("assignedTo")
		                                   : null;
	}
	
	/**
	 * Gets the attachment entries.
	 * 
	 * @return the attachmentEntries
	 */
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public List<AttachmentEntry> getAttachmentEntries() {
		return this.attachmentEntries;
	}
	
	/**
	 * Gets the category.
	 * 
	 * @return the category
	 */
	@Basic
	public String getCategory() {
		return this.category;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(Report.class);
	}
	
	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@OrderBy
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public SortedSet<Comment> getComments() {
		return this.comments;
	}
	
	/**
	 * Gets the component.
	 * 
	 * @return the component
	 */
	@Basic
	public String getComponent() {
		return this.component;
	}
	
	/**
	 * Gets the creation java timestamp.
	 * 
	 * @return the creation java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "creationTimestamp")
	private Date getCreationJavaTimestamp() {
		return getCreationTimestamp() != null
		                                     ? getCreationTimestamp().toDate()
		                                     : null;
	}
	
	/**
	 * Gets the creation timestamp.
	 * 
	 * @return the creationTimestamp
	 */
	@Transient
	public DateTime getCreationTimestamp() {
		return this.creationTimestamp;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Basic
	@Lob
	@Column (length = 0)
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Gets the field.
	 * 
	 * @param lowerFieldName
	 *            the lower field name
	 * @return the field
	 */
	public Object getField(final String lowerFieldName) {
		final Method[] methods = this.getClass().getDeclaredMethods();
		final String getter = "get" + lowerFieldName;
		
		for (final Method method : methods) {
			if (method.getName().equalsIgnoreCase(getter)) {
				try {
					return method.invoke(this);
				} catch (final IllegalArgumentException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IllegalAccessException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final InvocationTargetException e) {
					if (Logger.logError()) {
						Logger.error(e);
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
	 * Gets the hash.
	 * 
	 * @return the hash
	 */
	@Basic
	public byte[] getHash() {
		return this.hash;
	}
	
	/**
	 * Gets the history.
	 * 
	 * @return the history
	 */
	@OneToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public History getHistory() {
		return this.history;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	public String getId() {
		return this.id;
	}
	
	/**
	 * Gets the keywords.
	 * 
	 * @return the keywords
	 */
	@ElementCollection
	public Set<String> getKeywords() {
		// PRECONDITIONS
		
		try {
			return this.keywords;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the last fetch.
	 * 
	 * @return the lastFetch
	 */
	@Transient
	public DateTime getLastFetch() {
		return this.lastFetch;
	}
	
	/**
	 * Gets the last fetch java.
	 * 
	 * @return the last fetch java
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastFetch")
	private Date getLastFetchJava() {
		return getLastFetch() != null
		                             ? getLastFetch().toDate()
		                             : null;
	}
	
	/**
	 * Gets the last update java timestamp.
	 * 
	 * @return the last update java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastUpdateTimestamp")
	private Date getLastUpdateJavaTimestamp() {
		return getLastUpdateTimestamp() != null
		                                       ? getLastUpdateTimestamp().toDate()
		                                       : null;
	}
	
	/**
	 * Gets the last update timestamp.
	 * 
	 * @return the last update timestamp
	 */
	@Transient
	public DateTime getLastUpdateTimestamp() {
		return this.lastUpdateTimestamp;
	}
	
	/**
	 * Gets the person container.
	 * 
	 * @return the personContainer
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/**
	 * Gets the priority.
	 * 
	 * @return the priority
	 */
	@Enumerated (EnumType.ORDINAL)
	public Priority getPriority() {
		return this.priority;
	}
	
	/**
	 * Gets the product.
	 * 
	 * @return the product
	 */
	@Basic
	public String getProduct() {
		return this.product;
	}
	
	/**
	 * Gets the resolution.
	 * 
	 * @return the resolution
	 */
	@Enumerated (EnumType.ORDINAL)
	public Resolution getResolution() {
		return this.resolution;
	}
	
	/**
	 * Gets the resolution java timestamp.
	 * 
	 * @return the resolution java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "resolutionTimestamp")
	private Date getResolutionJavaTimestamp() {
		return getResolutionTimestamp() != null
		                                       ? getResolutionTimestamp().toDate()
		                                       : null;
	}
	
	/**
	 * Gets the resolution timestamp.
	 * 
	 * @return the resolution timestamp
	 */
	@Transient
	public DateTime getResolutionTimestamp() {
		return this.resolutionTimestamp;
	}
	
	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Transient
	public Person getResolver() {
		return getPersonContainer() != null
		                                   ? getPersonContainer().get("resolver")
		                                   : null;
	}
	
	/**
	 * Gets the scm fix version.
	 * 
	 * @return the scm fix version
	 */
	public String getScmFixVersion() {
		// PRECONDITIONS
		
		try {
			return this.scmFixVersion;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the severity.
	 * 
	 * @return the severity
	 */
	@Enumerated (EnumType.ORDINAL)
	public Severity getSeverity() {
		return this.severity;
	}
	
	/**
	 * Gets the siblings.
	 * 
	 * @return the siblings
	 */
	@ElementCollection
	@OrderBy
	public SortedSet<String> getSiblings() {
		return this.siblings;
	}
	
	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Enumerated (EnumType.ORDINAL)
	public Status getStatus() {
		return this.status;
	}
	
	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	@Basic
	@Lob
	@Column (length = 0)
	public String getSubject() {
		return this.subject;
	}
	
	/**
	 * Gets the submitter.
	 * 
	 * @return the submitter
	 */
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Transient
	public Person getSubmitter() {
		return getPersonContainer() != null
		                                   ? getPersonContainer().get("submitter")
		                                   : null;
	}
	
	/**
	 * Gets the summary.
	 * 
	 * @return the summary
	 */
	@Basic
	@Lob
	@Column (length = 0)
	public String getSummary() {
		return this.summary;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Enumerated (EnumType.STRING)
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Gets the version.
	 * 
	 * @return the version
	 */
	@Basic
	public String getVersion() {
		return this.version;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getId() == null)
		                                              ? 0
		                                              : getId().hashCode());
		return result;
	}
	
	/**
	 * Sets the assigned to.
	 * 
	 * @param assignedTo
	 *            the assignedTo to set
	 */
	public void setAssignedTo(final Person assignedTo) {
		getPersonContainer().add("assignedTo", assignedTo); //$NON-NLS-1$
	}
	
	/**
	 * Sets the attachment entries.
	 * 
	 * @param attachmentEntries
	 *            the attachmentEntries to set
	 */
	public void setAttachmentEntries(final List<AttachmentEntry> attachmentEntries) {
		this.attachmentEntries = attachmentEntries;
	}
	
	/**
	 * Sets the category.
	 * 
	 * @param category
	 *            the category to set
	 */
	public void setCategory(final String category) {
		this.category = category;
	}
	
	/**
	 * Sets the comments.
	 * 
	 * @param comments
	 *            the comments to set
	 * 
	 * @deprecated use addComment instead
	 */
	@Deprecated
	public void setComments(final SortedSet<Comment> comments) {
		this.comments = comments;
	}
	
	/**
	 * Sets the component.
	 * 
	 * @param component
	 *            the new component
	 */
	public void setComponent(final String component) {
		this.component = component;
	}
	
	/**
	 * Sets the creation java timestamp.
	 * 
	 * @param creationTimestamp
	 *            the new creation java timestamp
	 */
	@SuppressWarnings ("unused")
	private void setCreationJavaTimestamp(final Date creationTimestamp) {
		setCreationTimestamp(creationTimestamp != null
		                                              ? new DateTime(creationTimestamp)
		                                              : null);
	}
	
	/**
	 * Sets the creation timestamp.
	 * 
	 * @param creationTimestamp
	 *            the creationTimestamp to set
	 */
	public void setCreationTimestamp(final DateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Sets the field.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param fieldValue
	 *            the field value
	 */
	@Transient
	public void setField(final String fieldName,
	                     final Object fieldValue) {
		final String lowerFieldName = fieldName.toLowerCase();
		final Method[] methods = this.getClass().getDeclaredMethods();
		final String getter = "set" + lowerFieldName; //$NON-NLS-1$
		
		for (final Method method : methods) {
			if (method.getName().equalsIgnoreCase(getter) && (method.getParameterTypes().length == 1)
			        && ((fieldValue == null) || (method.getParameterTypes()[0] == fieldValue.getClass()))) {
				try {
					method.invoke(this, fieldValue);
				} catch (final IllegalArgumentException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IllegalAccessException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final InvocationTargetException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
		}
		
		if (Logger.logWarn()) {
			Logger.warn("Did not find a matching field for: " + lowerFieldName);
		}
	}
	
	/**
	 * Sets the hash.
	 * 
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(final byte[] hash) {
		this.hash = hash;
	}
	
	/**
	 * Sets the history.
	 * 
	 * @param history
	 *            the history to set
	 */
	private void setHistory(final History history) {
		this.history = history;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	private void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * Sets the keywords.
	 * 
	 * @param keywords
	 *            the new keywords
	 */
	public void setKeywords(final Set<String> keywords) {
		// PRECONDITIONS
		try {
			this.keywords = keywords;
		} finally {
			// POSTCONDITIONS
			// CompareCondition.equals(this.keywords, keywords,
			//			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the last fetch.
	 * 
	 * @param lastFetch
	 *            the lastFetch to set
	 */
	public void setLastFetch(final DateTime lastFetch) {
		this.lastFetch = lastFetch;
	}
	
	/**
	 * Sets the last fetch java.
	 * 
	 * @param lastFetch
	 *            the new last fetch java
	 */
	@SuppressWarnings ("unused")
	private void setLastFetchJava(final Date lastFetch) {
		setLastFetch(lastFetch != null
		                              ? new DateTime(lastFetch)
		                              : null);
	}
	
	/**
	 * Sets the last update java timestamp.
	 * 
	 * @param date
	 *            the new last update java timestamp
	 */
	@SuppressWarnings ("unused")
	private void setLastUpdateJavaTimestamp(final Date date) {
		setLastUpdateTimestamp(date != null
		                                   ? new DateTime(date)
		                                   : null);
	}
	
	/**
	 * Sets the last update timestamp.
	 * 
	 * @param lastUpdateTimestamp
	 *            the new last update timestamp
	 */
	public void setLastUpdateTimestamp(final DateTime lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	
	/**
	 * Sets the person container.
	 * 
	 * @param personContainer
	 *            the personContainer to set
	 */
	public void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/**
	 * Sets the priority.
	 * 
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(final Priority priority) {
		this.priority = priority;
	}
	
	/**
	 * Sets the product.
	 * 
	 * @param product
	 *            the new product
	 */
	public void setProduct(final String product) {
		this.product = product;
	}
	
	/**
	 * Sets the resolution.
	 * 
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(final Resolution resolution) {
		this.resolution = resolution;
	}
	
	/**
	 * Sets the resolution java timestamp.
	 * 
	 * @param date
	 *            the new resolution java timestamp
	 */
	@SuppressWarnings ("unused")
	private void setResolutionJavaTimestamp(final Date date) {
		setResolutionTimestamp(date != null
		                                   ? new DateTime(date)
		                                   : null);
	}
	
	/**
	 * Sets the resolution timestamp.
	 * 
	 * @param resolutionTimestamp
	 *            the new resolution timestamp
	 */
	public void setResolutionTimestamp(final DateTime resolutionTimestamp) {
		this.resolutionTimestamp = resolutionTimestamp;
	}
	
	/**
	 * Sets the resolver.
	 * 
	 * @param resolver
	 *            the resolver to set
	 */
	public void setResolver(final Person resolver) {
		getPersonContainer().add("resolver", resolver); //$NON-NLS-1$
	}
	
	/**
	 * Sets the scm fix version.
	 * 
	 * @param scmFixVersion
	 *            the new scm fix version
	 */
	public void setScmFixVersion(final String scmFixVersion) {
		// PRECONDITIONS
		try {
			this.scmFixVersion = scmFixVersion;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the severity.
	 * 
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(final Severity severity) {
		this.severity = severity;
	}
	
	/**
	 * Sets the siblings.
	 * 
	 * @param siblings
	 *            the siblings to set
	 */
	public void setSiblings(final SortedSet<String> siblings) {
		this.siblings = siblings;
	}
	
	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}
	
	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}
	
	/**
	 * Sets the submitter.
	 * 
	 * @param submitter
	 *            the submitter to set
	 */
	public void setSubmitter(final Person submitter) {
		getPersonContainer().add("submitter", submitter); //$NON-NLS-1$
	}
	
	/**
	 * Sets the summary.
	 * 
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(final String summary) {
		this.summary = summary;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	/**
	 * Sets the version.
	 * 
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}
	
	/**
	 * Timewarp.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @return the report
	 */
	public Report timewarp(final DateTime timestamp) {
		return getHistory().rollback(this, timestamp);
	}
	
	/**
	 * Timewarp.
	 * 
	 * @param interval
	 *            the interval
	 * @param field
	 *            the field
	 * @return the collection
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
		final int short_string_length = 10;
		return "Report [id="
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
		                                     getDescription().length() > short_string_length
		                                                                                    ? short_string_length
		                                                                                    : Math.max(getDescription().length() - 1,
		                                                                                               0))
		        + "... , severity="
		        + getSeverity()
		        + ", priority="
		        + getPriority()
		        + ", resolution="
		        + getResolution()
		        + ", submitter="
		        + getSubmitter()
		        + ", subject="
		        + getSubject().substring(0,
		                                 getSubject().length() > short_string_length
		                                                                            ? short_string_length
		                                                                            : Math.max(getSubject().length() - 1,
		                                                                                       0)) + "... , resolver="
		        + getResolver() + ", status=" + getStatus() + ", type=" + getType() + ", creationTimestamp="
		        + getCreationTimestamp() + ", lastFetch=" + getLastFetch() + ", hash=" + hash + "]";
	}
	
}

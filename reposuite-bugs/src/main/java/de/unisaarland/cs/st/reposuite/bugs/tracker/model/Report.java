/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.comparators.CommentComparator;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "report")
public class Report implements Annotated, Comparable<Report> {
	
	private long               id       = -1l;
	private Person             assignedTo;
	private String             category;
	private SortedSet<Comment> comments = new TreeSet<Comment>();
	private String             description;
	private Severity           severity;
	private Priority           priority;
	private Resolution         resolution;
	private Person             submitter;
	private String             subject;
	private Set<Long>          siblings = new TreeSet<Long>();
	private Person             resolver;
	private History            history  = new History();
	private Status             status;
	private Type               type;
	private DateTime           creationTimestamp;
	private DateTime           resolutionTimestamp;
	private DateTime           lastUpdateTimestamp;
	private DateTime           lastFetch;
	private String             version;
	private String             summary;
	private String             observedBehavior;
	private String             expectedBehavior;
	private String             stepsToReproduce;
	private String             component;
	private String             product;
	private byte[]             hash     = new byte[33];
	
	public Report() {
		super();
	}
	
	@Transient
	public void addComment(final Comment comment) {
		Condition.notNull(comment);
		Condition.notNull(this.comments);
		this.comments.add(comment);
	}
	
	@Transient
	public void addHistoryElement(final HistoryElement historyElement) {
		Condition.notNull(historyElement);
		Condition.notNull(this.history);
		this.history.add(historyElement);
	}
	
	@Transient
	public void addSibling(final Long sibling) {
		Condition.notNull(sibling);
		Condition.notNull(this.siblings);
		this.siblings.add(sibling);
	}
	
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
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	public Person getAssignedTo() {
		return this.assignedTo;
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
	@Sort (type = SortType.COMPARATOR, comparator = CommentComparator.class)
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public SortedSet<Comment> getComments() {
		return this.comments;
	}
	
	public String getComponent() {
		return this.component;
	}
	
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "creationTimestamp")
	private Date getCreationJavaTimestamp() {
		return this.creationTimestamp.toDate();
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
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the expectedBehavior
	 */
	public String getExpectedBehavior() {
		return this.expectedBehavior;
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
	@OneToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
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
	
	/**
	 * @return the lastFetch
	 */
	@Transient
	public DateTime getLastFetch() {
		return this.lastFetch;
	}
	
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastFetch")
	private Date getLastFetchJava() {
		return this.lastFetch.toDate();
	}
	
	public DateTime getLastUpdateTimestamp() {
		return this.lastUpdateTimestamp;
	}
	
	/**
	 * @return the observedBehavior
	 */
	public String getObservedBehavior() {
		return this.observedBehavior;
	}
	
	/**
	 * @return the priority
	 */
	@Enumerated (EnumType.ORDINAL)
	public Priority getPriority() {
		return this.priority;
	}
	
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
	
	public DateTime getResolutionTimestamp() {
		return this.resolutionTimestamp;
	}
	
	/**
	 * @return the resolver
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Person getResolver() {
		return this.resolver;
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
	public Set<Long> getSiblings() {
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
	 * @return the stepsToReproduce
	 */
	public String getStepsToReproduce() {
		return this.stepsToReproduce;
	}
	
	/**
	 * @return the subject
	 */
	@Basic
	public String getSubject() {
		return this.subject;
	}
	
	/**
	 * @return the submitter
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	public Person getSubmitter() {
		return this.submitter;
	}
	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return this.summary;
	}
	
	/**
	 * @return the type
	 */
	@Enumerated (EnumType.ORDINAL)
	public Type getType() {
		return this.type;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#getSaveFirst()
	 */
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param assignedTo
	 *            the assignedTo to set
	 */
	public void setAssignedTo(final Person assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(final String category) {
		this.category = category;
		
		if (Logger.logDebug()) {
			Logger.debug("Setting category " + category);
		}
	}
	
	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(final SortedSet<Comment> comments) {
		this.comments = comments;
	}
	
	public void setComponent(final String component) {
		this.component = component;
	}
	
	/**
	 * @param creationTimestamp
	 */
	@SuppressWarnings ("unused")
	private void setCreationJavaTimestamp(final Date creationTimestamp) {
		this.creationTimestamp = new DateTime(creationTimestamp);
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
	 * @param expectedBehavior
	 *            the expectedBehavior to set
	 */
	public void setExpectedBehavior(final String expectedBehavior) {
		this.expectedBehavior = expectedBehavior;
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
	public void setHistory(final History history) {
		this.history = history;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @param lastFetch
	 *            the lastFetch to set
	 */
	public void setLastFetch(final DateTime lastFetch) {
		this.lastFetch = lastFetch;
	}
	
	@SuppressWarnings ("unused")
	private void setLastFetchJava(final Date lastFetch) {
		this.lastFetch = new DateTime(lastFetch);
	}
	
	public void setLastUpdateTimestamp(final DateTime lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	
	/**
	 * @param observedBehavior
	 *            the observedBehavior to set
	 */
	public void setObservedBehavior(final String observedBehavior) {
		this.observedBehavior = observedBehavior;
	}
	
	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(final Priority priority) {
		this.priority = priority;
	}
	
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
	
	public void setResolutionTimestamp(final DateTime resolutionTimestamp) {
		this.resolutionTimestamp = resolutionTimestamp;
	}
	
	/**
	 * @param resolver
	 *            the resolver to set
	 */
	public void setResolver(final Person resolver) {
		this.resolver = resolver;
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
	public void setSiblings(final TreeSet<Long> siblings) {
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
	 * @param stepsToReproduce
	 *            the stepsToReproduce to set
	 */
	public void setStepsToReproduce(final String stepsToReproduce) {
		this.stepsToReproduce = stepsToReproduce;
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
		this.submitter = submitter;
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	@Transient
	public String toString() {
		String hash;
		try {
			hash = JavaUtils.byteArrayToHexString(this.hash);
		} catch (UnsupportedEncodingException e) {
			hash = "encoding failed"; // this will never be executed
		}
		return "BugReport [id=" + this.id + ", assignedTo=" + this.assignedTo + ", category=" + this.category
		        + ", comments=" + (this.comments != null ? this.comments.size() : 0) + ", description="
		        + this.description.substring(0, this.description.length() > 10 ? 10 : this.description.length() - 1)
		        + "... , severity=" + this.severity + ", priority=" + this.priority + ", resolution=" + this.resolution
		        + ", submitter=" + this.submitter + ", subject="
		        + this.subject.substring(0, this.subject.length() > 10 ? 10 : this.subject.length() - 1)
		        + "... , resolver=" + this.resolver + ", status=" + this.status + ", type=" + this.type
		        + ", creationTimestamp=" + this.creationTimestamp + ", lastFetch=" + this.lastFetch + ", hash=" + hash
		        + "]";
	}
	
}

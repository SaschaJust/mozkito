/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class BugReport implements Annotated {
	
	private long                      id;
	private Person                    assignedTo;
	private String                    category;
	private SortedSet<Comment>        comments = new TreeSet<Comment>();
	private String                    description;
	private Severity                  severity;
	private Priority                  priority;
	private Resolution                resolution;
	private Person                    submitter;
	private String                    subject;
	private Set<BugReport>            siblings = new HashSet<BugReport>();
	private Person                    resolver;
	private SortedSet<HistoryElement> history  = new TreeSet<HistoryElement>();
	private Status                    status;
	private Type                      type;
	private DateTime                  creationTimestamp;
	private DateTime                  lastFetch;
	private String                    version;
	private String                    summary;
	private String                    observedBehavior;
	private String                    expectedBehavior;
	private String                    stepsToReproduce;
	
	private byte[]                    hash     = new byte[33];
	
	public BugReport() {
		super();
	}
	
	@Transient
	public void addComment(final Comment comment) {
		Condition.notNull(comment);
		Condition.notNull(this.comments);
		
		this.comments.add(comment);
	}
	
	/**
	 * @param element
	 */
	@Transient
	public void addElementToHistory(final HistoryElement element) {
		Condition.notNull(element);
		Condition.notNull(this.history);
		
		this.history.add(element);
	}
	
	/**
	 * @return the assignedTo
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
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
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public SortedSet<Comment> getComments() {
		return this.comments;
	}
	
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "creationTimestamp")
	private Date getCreationJavaTimetamp() {
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
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public SortedSet<HistoryElement> getHistory() {
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
	
	/**
	 * @return the resolution
	 */
	@Enumerated (EnumType.ORDINAL)
	public Resolution getResolution() {
		return this.resolution;
	}
	
	/**
	 * @return the resolver
	 */
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Person getResolver() {
		return this.resolver;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#getSaveFirst()
	 */
	@Override
	@Transient
	public Collection<Annotated> getSaveFirst() {
		// TODO Auto-generated method stub
		return null;
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
	@ManyToMany
	public Set<BugReport> getSiblings() {
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
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
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
	public void setHistory(final SortedSet<HistoryElement> history) {
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
	
	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(final Resolution resolution) {
		this.resolution = resolution;
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
	public void setSiblings(final Set<BugReport> siblings) {
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
		return "BugReport [id=" + this.id + ", assignedTo=" + this.assignedTo + ", category=" + this.category
		        + ", comments=" + (this.comments != null ? this.comments.size() : 0) + ", description="
		        + this.description.substring(0, this.description.length() > 10 ? 10 : this.description.length() - 1)
		        + "... , severity=" + this.severity + ", priority=" + this.priority + ", resolution=" + this.resolution
		        + ", submitter=" + this.submitter + ", subject="
		        + this.subject.substring(0, this.subject.length() > 10 ? 10 : this.subject.length() - 1)
		        + "... , resolver=" + this.resolver + ", status=" + this.status + ", type=" + this.type
		        + ", creationTimestamp=" + this.creationTimestamp + ", lastFetch=" + this.lastFetch + ", hash="
		        + new String(this.hash) + "]";
	}
	
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
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
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
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
	
	/**
     * 
     */
	private static final long  serialVersionUID = 3241584366125944268L;
	private long               id               = -1l;
	private String             category;
	private SortedSet<Comment> comments         = new TreeSet<Comment>();
	private String             description;
	private Severity           severity;
	private Priority           priority;
	private Resolution         resolution;
	private String             subject;
	private SortedSet<Long>    siblings         = new TreeSet<Long>();
	private History            history          = new History();
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
	private byte[]             hash             = new byte[33];
	// assignedTo
	// submitter
	// resolver
	private PersonContainer    personContainer  = new PersonContainer();
	
	public Report() {
		super();
	}
	
	/**
	 * @param comment
	 */
	@Transient
	public void addComment(final Comment comment) {
		Condition.notNull(comment);
		Condition.notNull(this.comments);
		this.comments.add(comment);
	}
	
	/**
	 * @param historyElement
	 */
	@Transient
	public void addHistoryElement(final HistoryElement historyElement) {
		Condition.notNull(historyElement);
		Condition.notNull(this.history);
		this.history.add(historyElement);
	}
	
	/**
	 * @param sibling
	 */
	@Transient
	public void addSibling(final Long sibling) {
		Condition.notNull(sibling);
		Condition.notNull(this.siblings);
		this.siblings.add(sibling);
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
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Transient
	public Person getAssignedTo() {
		return this.getPersonContainer().get("assignedTo");
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
		return getCreationTimestamp() != null ? getCreationTimestamp().toDate() : null;
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
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the expectedBehavior
	 */
	@Basic
	@Lob
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
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastFetch")
	private Date getLastFetchJava() {
		return getLastFetch() != null ? getLastFetch().toDate() : null;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "lastUpdateTimestamp")
	private Date getLastUpdateJavaTimestamp() {
		return getLastUpdateTimestamp() != null ? getLastUpdateTimestamp().toDate() : null;
	}
	
	/**
	 * @return
	 */
	@Transient
	public DateTime getLastUpdateTimestamp() {
		return this.lastUpdateTimestamp;
	}
	
	/**
	 * @return the observedBehavior
	 */
	@Basic
	@Lob
	public String getObservedBehavior() {
		return this.observedBehavior;
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
		return getResolutionTimestamp() != null ? getResolutionTimestamp().toDate() : null;
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
		return this.getPersonContainer().get("resolver");
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
	@Sort (type = SortType.NATURAL)
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
	 * @return the stepsToReproduce
	 */
	@Basic
	@Lob
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
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Transient
	public Person getSubmitter() {
		return this.getPersonContainer().get("submitter");
	}
	
	/**
	 * @return the summary
	 */
	@Basic
	@Lob
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
	@Basic
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
		Collection<Annotated> ret = new LinkedList<Annotated>();
		for (Comment comment : this.comments) {
			ret.add(comment.getPersonContainer());
		}
		for (HistoryElement historyElement : this.history.getElements()) {
			ret.add(historyElement.getPersonContainer());
			ret.add(historyElement.getNewPersons());
			ret.add(historyElement.getOldPersons());
		}
		ret.add(this.personContainer);
		return ret;
	}
	
	/**
	 * @param assignedTo
	 *            the assignedTo to set
	 */
	public void setAssignedTo(final Person assignedTo) {
		this.getPersonContainer().add("assignedTo", assignedTo);
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
		this.creationTimestamp = creationTimestamp != null ? new DateTime(creationTimestamp) : null;
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
	
	/**
	 * @param lastFetch
	 */
	@SuppressWarnings ("unused")
	private void setLastFetchJava(final Date lastFetch) {
		this.lastFetch = lastFetch != null ? new DateTime(lastFetch) : null;
	}
	
	/**
	 * @param date
	 */
	@SuppressWarnings ("unused")
	private void setLastUpdateJavaTimestamp(final Date date) {
		this.lastUpdateTimestamp = date != null ? new DateTime(date) : null;
	}
	
	/**
	 * @param lastUpdateTimestamp
	 */
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
	
	@SuppressWarnings ("unused")
	private void setResolutionJavaTimestamp(final Date date) {
		this.resolutionTimestamp = date != null ? new DateTime(date) : null;
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
		this.getPersonContainer().add("resolver", resolver);
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
		this.getPersonContainer().add("submitter", submitter);
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
		return "BugReport [id=" + this.id + ", assignedTo=" + getAssignedTo() + ", category=" + this.category
		        + ", comments=" + (this.comments != null ? this.comments.size() : 0) + ", description="
		        + this.description.substring(0, this.description.length() > 10 ? 10 : this.description.length() - 1)
		        + "... , severity=" + this.severity + ", priority=" + this.priority + ", resolution=" + this.resolution
		        + ", submitter=" + getSubmitter() + ", subject="
		        + this.subject.substring(0, this.subject.length() > 10 ? 10 : this.subject.length() - 1)
		        + "... , resolver=" + getResolver() + ", status=" + this.status + ", type=" + this.type
		        + ", creationTimestamp=" + this.creationTimestamp + ", lastFetch=" + this.lastFetch + ", hash=" + hash
		        + "]";
	}
	
}

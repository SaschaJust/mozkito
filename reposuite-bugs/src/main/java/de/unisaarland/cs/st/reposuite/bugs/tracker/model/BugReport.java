/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
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
	private DateTime                  creatingTimestamp;
	private byte[]                    hash;
	
	/**
	 * @return the assignedTo
	 */
	public Person getAssignedTo() {
		return this.assignedTo;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return this.category;
	}
	
	/**
	 * @return the comments
	 */
	public SortedSet<Comment> getComments() {
		return this.comments;
	}
	
	/**
	 * @return the creatingTimestamp
	 */
	public DateTime getCreatingTimestamp() {
		return this.creatingTimestamp;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the hash
	 */
	public byte[] getHash() {
		return this.hash;
	}
	
	/**
	 * @return the history
	 */
	public SortedSet<HistoryElement> getHistory() {
		return this.history;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * @return the priority
	 */
	public Priority getPriority() {
		return this.priority;
	}
	
	/**
	 * @return the resolution
	 */
	public Resolution getResolution() {
		return this.resolution;
	}
	
	/**
	 * @return the resolver
	 */
	public Person getResolver() {
		return this.resolver;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#getSaveFirst()
	 */
	@Override
	public Collection<Annotated> getSaveFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the severity
	 */
	public Severity getSeverity() {
		return this.severity;
	}
	
	/**
	 * @return the siblings
	 */
	public Set<BugReport> getSiblings() {
		return this.siblings;
	}
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return this.status;
	}
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}
	
	/**
	 * @return the submitter
	 */
	public Person getSubmitter() {
		return this.submitter;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return this.type;
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
	}
	
	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(final SortedSet<Comment> comments) {
		this.comments = comments;
	}
	
	/**
	 * @param creatingTimestamp
	 *            the creatingTimestamp to set
	 */
	public void setCreatingTimestamp(final DateTime creatingTimestamp) {
		this.creatingTimestamp = creatingTimestamp;
	}
	
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
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
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
}

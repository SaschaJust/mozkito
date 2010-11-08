/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	private DateTime                  lastFetch;
	private byte[]                    hash     = new byte[33];
	
	public BugReport() {
		super();
	}
	
	public BugReport(final long id, final Person assignedTo, final String category, final SortedSet<Comment> comments,
	        final String description, final Severity severity, final Priority priority, final Resolution resolution,
	        final Person submitter, final String subject, final Person resolver,
	        final SortedSet<HistoryElement> history, final Status status, final Type type,
	        final DateTime creatingTimestamp, final DateTime lastFetch, final byte[] hash) {
		super();
		Preconditions.checkArgument(id > 0, "[BugReport] `id` should be > 0, but is `%s`.", id);
		Preconditions.checkNotNull(comments, "[BugReport] `comments` should not be null.");
		Preconditions.checkNotNull(description, "[BugReport] `description` should not be null.");
		Preconditions.checkNotNull(severity, "[BugReport] `severity` should not be null.");
		Preconditions.checkNotNull(priority, "[BugReport] `priority` should not be null.");
		Preconditions.checkNotNull(resolution, "[BugReport] `resolution` should not be null.");
		Preconditions.checkNotNull(submitter, "[BugReport] `submitter` should not be null.");
		Preconditions.checkNotNull(subject, "[BugReport] `subject` should not be null.");
		Preconditions.checkNotNull(status, "[BugReport] `status` should not be null.");
		Preconditions.checkNotNull(type, "[BugReport] `type` should not be null.");
		Preconditions.checkNotNull(creatingTimestamp, "[BugReport] `creatingTimestamp` should not be null.");
		Preconditions.checkNotNull(lastFetch, "[BugReport] `lastFetch` should not be null.");
		Preconditions.checkArgument(hash.length == 33, "[BugReport] `hash.length` should be equal to `33`, but is: %s",
		        hash.length);
		
		this.id = id;
		this.assignedTo = assignedTo;
		this.category = category;
		this.comments = comments;
		this.description = description;
		this.severity = severity;
		this.priority = priority;
		this.resolution = resolution;
		this.submitter = submitter;
		this.subject = subject;
		this.resolver = resolver;
		this.history = history;
		this.status = status;
		this.type = type;
		this.creatingTimestamp = creatingTimestamp;
		this.lastFetch = lastFetch;
		this.hash = hash;
	}
	
	public void addComment(final Comment comment) {
		Preconditions.checkNotNull(comment, "[addComment] `comment` should not be null.");
		Preconditions.checkNotNull(this.comments, "[addComment] `comments` should not be null.");
		
		this.comments.add(comment);
	}
	
	/**
	 * @param element
	 */
	public void addElementToHistory(final HistoryElement element) {
		Preconditions.checkNotNull(element, "[addElementToHistory] `element` should not be null.");
		Preconditions.checkNotNull(this.history, "[addElementToHistory] `history` should not be null.");
		
		this.history.add(element);
	}
	
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
	 * @return the lastFetch
	 */
	public DateTime getLastFetch() {
		return this.lastFetch;
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
	 * @param lastFetch
	 *            the lastFetch to set
	 */
	public void setLastFetch(final DateTime lastFetch) {
		this.lastFetch = lastFetch;
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "BugReport [id=" + this.id + ", assignedTo=" + this.assignedTo + ", category=" + this.category
		        + ", comments=" + (this.comments != null ? toString(this.comments, maxLen) : null) + ", description="
		        + this.description.substring(0, this.description.length() > 10 ? 10 : this.description.length() - 1)
		        + "... , severity=" + this.severity + ", priority=" + this.priority + ", resolution=" + this.resolution
		        + ", submitter=" + this.submitter + ", subject="
		        + this.subject.substring(0, this.subject.length() > 10 ? 10 : this.subject.length() - 1)
		        + "... , resolver=" + this.resolver + ", status=" + this.status + ", type=" + this.type
		        + ", creatingTimestamp=" + this.creatingTimestamp + ", lastFetch=" + this.lastFetch + ", hash="
		        + new String(this.hash) + "]";
	}
	
	private String toString(final Collection<?> collection, final int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && (i < maxLen); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
}

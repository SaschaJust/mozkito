package de.unisaarland.cs.st.reposuite.rcs.elements;

import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class LogEntry.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class LogEntry implements Comparable<LogEntry> {
	
	protected Person      author;
	protected DateTime    commitDate;
	protected String      message;
	protected LogEntry    previous;
	protected String      revision;
	protected Set<String> mergedBranches;
	protected String      originalId;
	
	/**
	 * Instantiates a new log entry.
	 * 
	 * @param revision
	 *            the revision
	 * @param previous
	 *            the previous LogEntry, null if this is the first revision
	 * @param author
	 *            the author
	 * @param message
	 *            the message
	 * @param dateTime
	 *            the date time
	 */
	public LogEntry(@NotNull final String revision, final LogEntry previous, @NotNull final Person author,
	        @NotNull final String message, @NotNull final DateTime dateTime, final String originalId) {
		this.revision = revision;
		this.author = author;
		this.message = message;
		this.previous = previous;
		this.commitDate = dateTime;
		this.originalId = originalId;
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final LogEntry o) {
		return this.commitDate.compareTo(o.commitDate);
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * Gets the date time.
	 * 
	 * @return the date time
	 */
	public DateTime getDateTime() {
		return this.commitDate;
	}
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return the mergedBranches
	 */
	public Set<String> getMergedBranches() {
		return this.mergedBranches;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	public String getOriginalId(){
		if (this.originalId != null) {
			return this.originalId;
		}
		return "";
	}
	
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return this.revision;
	}
	
	/**
	 * @param mergedBranches
	 *            the mergedBranches to set
	 */
	public void setMergedBranches(final Set<String> mergedBranches) {
		this.mergedBranches = mergedBranches;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogEntry [revision=" + this.revision + ", author=" + this.author + ", message="
		+ StringEscapeUtils.escapeJava(this.message) + ", commitDate=" + this.commitDate + ", previous="
		+ (this.previous != null ? this.previous.revision : "(null)") + "]";
	}
	
}

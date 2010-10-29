package de.unisaarland.cs.st.reposuite.rcs.elements;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.rcs.model.Person;

/**
 * The Class LogEntry.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class LogEntry implements Comparable<LogEntry> {
	
	protected String   revision;
	protected Person   author;
	protected String   message;
	protected DateTime commitDate;
	protected LogEntry previous;
	
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
	public LogEntry(final String revision, final LogEntry previous, final Person author, final String message,
	        final DateTime dateTime) {
		this.revision = revision;
		this.author = author;
		this.message = message;
		this.previous = previous;
		this.commitDate = dateTime;
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
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return this.revision;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogEntry [revision=" + this.revision + ", author=" + this.author + ", message=" + this.message
		        + ", commitDate=" + this.commitDate + ", previous=" + this.previous + "]";
	}
	
}

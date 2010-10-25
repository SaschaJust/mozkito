package de.unisaarland.cs.st.reposuite.rcs;

import org.joda.time.DateTime;

/**
 * The Class LogEntry.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class LogEntry implements Comparable<LogEntry> {
	
	protected String   revision;
	protected String   author;
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
	public LogEntry(String revision, LogEntry previous, String author, String message, DateTime dateTime) {
		this.revision = revision;
		this.author = author;
		this.message = message;
		this.previous = previous;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LogEntry o) {
		return this.commitDate.compareTo(o.commitDate);
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public String getAuthor() {
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
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogEntry [revision=" + this.revision + ", author=" + this.author + ", message=" + this.message
		        + ", commitDate=" + this.commitDate + ", previous=" + this.previous + "]";
	}
	
}

package de.unisaarland.cs.st.reposuite.rcs;

import org.joda.time.DateTime;

/**
 * The Class LogEntry.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class LogEntry implements Comparable<LogEntry> {
	
	private String   revision;
	private String   author;
	private String   message;
	
	private DateTime dateTime;
	
	/**
	 * Instantiates a new log entry.
	 * 
	 * @param revision
	 *            the revision
	 * @param author
	 *            the author
	 * @param message
	 *            the message
	 * @param dateTime
	 *            the date time
	 */
	public LogEntry(String revision, String author, String message, DateTime dateTime) {
		this.revision = revision;
		this.author = author;
		this.message = message;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LogEntry o) {
		return dateTime.compareTo(o.dateTime);
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Gets the date time.
	 * 
	 * @return the date time
	 */
	public DateTime getDateTime() {
		return dateTime;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}
	
}

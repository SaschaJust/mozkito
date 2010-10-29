package de.unisaarland.cs.st.reposuite.rcs.elements;

import org.joda.time.DateTime;

public class AnnotationEntry {
	
	private final String   revision;
	private final String   alternativeFilePath;
	private final String   username;
	private final DateTime timestamp;
	private final String   line;
	
	public AnnotationEntry(String revision, String username, DateTime timestamp, String line) {
		this(revision, username, timestamp, line, null);
	}
	
	public AnnotationEntry(String revision, String username, DateTime timestamp, String line, String alternativeFilePath) {
		this.revision = revision;
		this.alternativeFilePath = alternativeFilePath;
		this.username = username;
		this.timestamp = timestamp;
		this.line = line;
	}
	
	public String getAlternativeFilePath() {
		return this.alternativeFilePath;
		
	}
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return the line
	 */
	public String getLine() {
		return this.line;
	}
	
	public String getRevision() {
		return this.revision;
	}
	
	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	public boolean hasAlternativePath() {
		return (this.alternativeFilePath != null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnnotationEntry [revision=" + this.revision + ", alternativeFilePath=" + this.alternativeFilePath
		        + ", username=" + this.username + ", timestamp=" + this.timestamp + ", line=" + this.line + "]";
	}
	
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 *
 * @author kim
 */
public abstract class JavaElement {
	
	private String fullQualifiedName = "<unknown>";
	private String shortName = "<unknown>";
	private String filePath = "<unknown>";
	private DateTime timestamp = null;
	private int startLine = -1;
	private int endLine = -1;
	
	@NoneNull
	public JavaElement(final String fullQualifiedName, final String filePath, final DateTime timestamp, @NonNegative final int startLine, @NonNegative final int endLine) {
		this.fullQualifiedName = fullQualifiedName;
		String[] nameParts = fullQualifiedName.split("\\.");
		shortName = nameParts[nameParts.length - 1];
		this.filePath = filePath;
		this.timestamp = timestamp;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	/**
	 * @return the endLine
	 */
	public int getEndLine() {
		return endLine;
	}
	
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * @return the fullQualifiedName
	 */
	public String getFullQualifiedName() {
		return fullQualifiedName;
	}
	
	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}
	
	/**
	 * @return the startLine
	 */
	public int getStartLine() {
		return startLine;
	}
	
	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + fullQualifiedName + ", shortName=" + shortName + ", filePath="
		        + filePath + ", timestamp=" + timestamp + ", startLine=" + startLine + ", endLine=" + endLine + "]";
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unisaarland.cs.st.reposuite.ppa.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 *
 * @author kim
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class JavaElement implements Annotated{
	
	/**
	 * 
	 */
	private static final long serialVersionUID  = -8960043672858454394L;
	
	protected String fullQualifiedName = "<unknown>";
	
	private String   shortName         = "<unknown>";
	
	private String   filePath          = "<unknown>";
	
	private DateTime timestamp         = null;
	
	private int      startLine         = -1;
	
	private int      endLine           = -1;
	
	@NoneNull
	public JavaElement(final String fullQualifiedName, final String filePath, final DateTime timestamp,
			@NonNegative final int startLine, @NonNegative final int endLine) {
		this.fullQualifiedName = fullQualifiedName;
		String[] nameParts = fullQualifiedName.split("\\.");
		shortName = nameParts[nameParts.length - 1];
		this.filePath = filePath;
		this.timestamp = timestamp;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JavaElement other = (JavaElement) obj;
		if (endLine != other.endLine) {
			return false;
		}
		if (filePath == null) {
			if (other.filePath != null) {
				return false;
			}
		} else if (!filePath.equals(other.filePath)) {
			return false;
		}
		if (fullQualifiedName == null) {
			if (other.fullQualifiedName != null) {
				return false;
			}
		} else if (!fullQualifiedName.equals(other.fullQualifiedName)) {
			return false;
		}
		if (shortName == null) {
			if (other.shortName != null) {
				return false;
			}
		} else if (!shortName.equals(other.shortName)) {
			return false;
		}
		if (startLine != other.startLine) {
			return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
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
	@Id
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endLine;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((fullQualifiedName == null) ? 0 : fullQualifiedName.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + startLine;
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}
	
	@SuppressWarnings("unused")
	private void setEndLine(final int endLine) {
		this.endLine = endLine;
	}
	
	@SuppressWarnings("unused")
	private void setFilePath(final String filePath) {
		this.filePath = filePath;
	}
	
	@SuppressWarnings("unused")
	private void setFullQualifiedName(final String fullQualifiedName) {
		this.fullQualifiedName = fullQualifiedName;
	}
	
	@SuppressWarnings("unused")
	private void setShortName(final String shortName) {
		this.shortName = shortName;
	}
	
	@SuppressWarnings("unused")
	private void setStartLine(final int startLine) {
		this.startLine = startLine;
	}
	
	@SuppressWarnings("unused")
	private void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "JavaElement [fullQualifiedName=" + fullQualifiedName + ", shortName=" + shortName + ", filePath="
		+ filePath + ", timestamp=" + timestamp + ", startLine=" + startLine + ", endLine=" + endLine + "]";
	}
	
}

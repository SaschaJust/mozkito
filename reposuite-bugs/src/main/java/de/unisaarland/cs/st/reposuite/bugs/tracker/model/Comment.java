/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Comment implements Annotated, Comparable<Comment> {
	
	private long      id;
	private DateTime  timestamp;
	private Person    author;
	private String    message;
	private BugReport bugReport;
	
	public Comment(final BugReport bugReport, final Person author, final DateTime timestamp, final String message) {
		assert (bugReport != null);
		assert (author != null);
		assert (timestamp != null);
		assert (message != null);
		
		this.bugReport = bugReport;
		this.author = author;
		this.timestamp = timestamp;
		this.message = message;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Comment o) {
		if (o == null) {
			return 1;
		} else {
			return this.timestamp.compareTo(o.timestamp);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Comment)) {
			return false;
		}
		Comment other = (Comment) obj;
		if (this.author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!this.author.equals(other.author)) {
			return false;
		}
		if (this.bugReport == null) {
			if (other.bugReport != null) {
				return false;
			}
		} else if (!this.bugReport.equals(other.bugReport)) {
			return false;
		}
		if (this.timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!this.timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the author
	 */
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * @return the bugReport
	 */
	public BugReport getBugReport() {
		return this.bugReport;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
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
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.author == null) ? 0 : this.author.hashCode());
		result = prime * result + ((this.bugReport == null) ? 0 : this.bugReport.hashCode());
		result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
		return result;
	}
	
	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final Person author) {
		this.author = author;
	}
	
	/**
	 * @param bugReport
	 *            the bugReport to set
	 */
	public void setBugReport(final BugReport bugReport) {
		this.bugReport = bugReport;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
}

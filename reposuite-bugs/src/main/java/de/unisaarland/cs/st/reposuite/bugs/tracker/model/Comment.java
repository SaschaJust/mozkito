/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.utils.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "comment")
public class Comment implements Annotated, Comparable<Comment> {
	
	/**
     * 
     */
	private static final long serialVersionUID = -2410349441783888667L;
	private DateTime          timestamp;
	private String            message;
	private Report            bugReport;
	private CommentPrimaryKey primaryKey;
	private PersonContainer   personContainer  = new PersonContainer();
	
	/**
	 * 
	 */
	@SuppressWarnings ("unused")
	private Comment() {
		
	}
	
	/**
	 * @param bugReport
	 * @param author
	 * @param timestamp
	 * @param message
	 */
	public Comment(final Report bugReport, final int id, final Person author, final DateTime timestamp,
	        final String message) {
		Condition.notNull(bugReport);
		Condition.greater(id, 0);
		Condition.notNull(author);
		Condition.notNull(timestamp);
		Condition.notNull(message);
		
		setBugReport(bugReport);
		setAuthor(author);
		setTimestamp(timestamp);
		setMessage(message);
		setPrimaryKey(new CommentPrimaryKey(bugReport.getId(), id));
		
		bugReport.addComment(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Comment object) {
		if (object == null) {
			return 1;
		} else {
			if (getPrimaryKey().getCommentId() == object.getPrimaryKey().getCommentId()) {
				return this.timestamp.compareTo(object.timestamp);
			} else {
				if (getPrimaryKey().getCommentId() > object.getPrimaryKey().getCommentId()) {
					return 1;
				} else {
					return -1;
				}
			}
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
		if (this.bugReport == null) {
			if (other.bugReport != null) {
				return false;
			}
		} else if (!this.bugReport.equals(other.bugReport)) {
			return false;
		}
		if (this.message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!this.message.equals(other.message)) {
			return false;
		}
		if (this.personContainer == null) {
			if (other.personContainer != null) {
				return false;
			}
		} else if (!this.personContainer.equals(other.personContainer)) {
			return false;
		}
		if (this.primaryKey == null) {
			if (other.primaryKey != null) {
				return false;
			}
		} else if (!this.primaryKey.equals(other.primaryKey)) {
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
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Transient
	public Person getAuthor() {
		return this.personContainer.get("author");
	}
	
	/**
	 * @return the bugReport
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Report getBugReport() {
		return this.bugReport;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return getPrimaryKey().getCommentId();
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "timestamp")
	private Date getJavaTimestamp() {
		return this.timestamp.toDate();
	}
	
	/**
	 * @return the message
	 */
	@Lob
	@Basic
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * @return the personContainer
	 */
	@OneToOne
	protected PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/**
	 * @return the primaryKey
	 */
	@EmbeddedId
	private CommentPrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}
	
	/**
	 * @return the timestamp
	 */
	@Transient
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
		result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
		result = prime * result + ((this.personContainer == null) ? 0 : this.personContainer.hashCode());
		result = prime * result + ((this.primaryKey == null) ? 0 : this.primaryKey.hashCode());
		result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
		return result;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final Person author) {
		this.personContainer.add("author", author);
	}
	
	/**
	 * @param bugReport
	 *            the bugReport to set
	 */
	public void setBugReport(final Report bugReport) {
		this.bugReport = bugReport;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		getPrimaryKey().setCommentId(id);
	}
	
	@SuppressWarnings ("unused")
	private void setJavaTimestamp(final Date timestamp) {
		this.timestamp = new DateTime(timestamp);
	}
	
	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * @param personContainer
	 *            the personContainer to set
	 */
	protected void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/**
	 * @param primaryKey
	 *            the primaryKey to set
	 */
	private void setPrimaryKey(final CommentPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Comment [id=");
		builder.append(getPrimaryKey().getCommentId());
		builder.append(", timestamp=");
		builder.append(getTimestamp());
		builder.append(", author=");
		builder.append(getAuthor());
		builder.append(", message=");
		builder.append(getMessage().length() > 10 ? getMessage().substring(0, 10) : getMessage());
		builder.append(", bugReport=");
		builder.append(getBugReport().getId());
		builder.append("]");
		return builder.toString();
	}
	
}

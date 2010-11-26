/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

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
@Table (name = "comment", uniqueConstraints = { @UniqueConstraint (columnNames = { "id", "bugreport_id" }) })
// @Table (name = "comment")
public class Comment implements Annotated, Comparable<Comment> {
	
	/**
     * 
     */
	private static final long serialVersionUID = -2410349441783888667L;
	private long              generatedId;
	
	private DateTime          timestamp;
	
	private String            message;
	
	private Report            bugReport;
	private int               id;
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
	public Comment(final int id, final Person author, final DateTime timestamp, final String message) {
		Condition.greater(id, 0);
		Condition.notNull(author);
		Condition.notNull(timestamp);
		Condition.notNull(message);
		
		setAuthor(author);
		setTimestamp(timestamp);
		setMessage(message);
		setId(id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Comment arg0) {
		if (arg0 == null) {
			return 1;
		} else if (getId() > arg0.getId()) {
			return 1;
		} else if (getId() < arg0.getId()) {
			return -1;
		} else {
			return 0;
		}
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
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the id
	 */
	@Basic
	public int getId() {
		return this.id;
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
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
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
	 * @param generatedId
	 *            the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		this.id = id;
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
		builder.append(getId());
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

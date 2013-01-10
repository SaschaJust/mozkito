/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.model;

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

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.ioda.StringUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;

import org.joda.time.DateTime;
import org.mozkito.persistence.Annotated;
import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;

/**
 * The Class Comment.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "comment", uniqueConstraints = { @UniqueConstraint (columnNames = { "id", "bugreport_id" }) })
public class Comment implements Annotated, TextElement, Comparable<Comment> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2410349441783888667L;
	
	/** The generated id. */
	private long              generatedId;
	
	/** The timestamp. */
	private DateTime          timestamp;
	
	/** The message. */
	private String            message;
	
	/** The bug report. */
	private Report            bugReport;
	
	/** The id. */
	private int               id;
	
	/** The person container. */
	private PersonContainer   personContainer  = new PersonContainer();
	
	/**
	 * Instantiates a new comment.
	 */
	@SuppressWarnings ("unused")
	private Comment() {
		
	}
	
	/**
	 * Instantiates a new comment.
	 * 
	 * @param id
	 *            the id
	 * @param author
	 *            the author
	 * @param timestamp
	 *            the timestamp
	 * @param message
	 *            the message
	 */
	@NoneNull
	public Comment(@Positive final int id, final Person author, final DateTime timestamp, final String message) {
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
	 * Gets the author.
	 * 
	 * @return the author
	 */
	@Override
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Transient
	public Person getAuthor() {
		return getPersonContainer().get("author");
	}
	
	/**
	 * Gets the bug report.
	 * 
	 * @return the bugReport
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Report getBugReport() {
		return this.bugReport;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(Comment.class);
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Basic
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "timestamp")
	private Date getJavaTimestamp() {
		return this.timestamp.toDate();
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	@Lob
	@Basic
	@Column (length = 0)
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Gets the person container.
	 * 
	 * @return the personContainer
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.model.TextElement#getText()
	 */
	@Override
	@Transient
	public String getText() {
		return getMessage();
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Override
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final Person author) {
		getPersonContainer().add("author", author);
	}
	
	/**
	 * Sets the bug report.
	 * 
	 * @param bugReport
	 *            the bugReport to set
	 */
	public void setBugReport(final Report bugReport) {
		this.bugReport = bugReport;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		this.id = id;
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @param timestamp
	 *            the new java timestamp
	 */
	@SuppressWarnings ("unused")
	private void setJavaTimestamp(final Date timestamp) {
		this.timestamp = timestamp == null
		                                  ? null
		                                  : new DateTime(timestamp);
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * Sets the person container.
	 * 
	 * @param personContainer
	 *            the personContainer to set
	 */
	protected void setPersonContainer(final PersonContainer personContainer) {
		this.personContainer = personContainer;
	}
	
	/**
	 * Sets the timestamp.
	 * 
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
		final int short_message_length = 10;
		final StringBuilder builder = new StringBuilder();
		builder.append("Comment [id=");
		builder.append(getId());
		builder.append(", timestamp=");
		builder.append(getTimestamp());
		builder.append(", author=");
		builder.append(getAuthor());
		builder.append(", message=");
		builder.append(StringUtils.truncate(getMessage(), short_message_length));
		builder.append(", bugReport=");
		builder.append(getBugReport() == null
		                                     ? null
		                                     : getBugReport().getId());
		builder.append("]");
		return builder.toString();
	}
	
}

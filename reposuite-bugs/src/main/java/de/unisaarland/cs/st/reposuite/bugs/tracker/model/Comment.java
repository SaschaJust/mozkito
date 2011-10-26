/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

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

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.Positive;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "comment", uniqueConstraints = { @UniqueConstraint (columnNames = { "id", "bugreport_id" }) })
public class Comment implements Annotated, TextElement, Comparable<Comment> {
	
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
	 * @return the author
	 */
	@Override
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Transient
	public Person getAuthor() {
		return getPersonContainer().get("author");
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
	@Column (columnDefinition = "TEXT")
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * @return the personContainer
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected PersonContainer getPersonContainer() {
		return this.personContainer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.TextElement#getText()
	 */
	@Override
	@Transient
	public String getText() {
		return getMessage();
	}
	
	/**
	 * @return the timestamp
	 */
	@Override
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final Person author) {
		getPersonContainer().add("author", author);
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
		this.timestamp = timestamp == null
		                                  ? null
		                                  : new DateTime(timestamp);
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
		builder.append(getMessage().length() > 10
		                                         ? getMessage().substring(0, 10)
		                                         : getMessage());
		builder.append(", bugReport=");
		builder.append(getBugReport().getId());
		builder.append("]");
		return builder.toString();
	}
	
}

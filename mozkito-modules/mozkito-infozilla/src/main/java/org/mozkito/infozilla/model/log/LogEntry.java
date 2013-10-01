/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.infozilla.model.log;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class LogEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class LogEntry implements Annotated, Inlineable {
	
	/**
	 * The Enum Level.
	 */
	public static enum Level {
		
		/** The trace. */
		TRACE,
		/** The debug. */
		DEBUG,
		/** The fine. */
		FINE,
		/** The info. */
		INFO,
		/** The warn. */
		WARN,
		/** The error. */
		ERROR,
		/** The fatal. */
		FATAL,
		/** The unknown. */
		UNKNOWN;
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -966097018041268855L;
	
	/** The id. */
	private int               id;
	
	/** The line. */
	private String            message;
	
	/** The source class. */
	private String            sourceClass;
	
	/** The timestamp. */
	private DateTime          timestamp;
	
	/** The level. */
	private Level             level            = Level.UNKNOWN;
	
	/** The start position. */
	private Integer           startPosition;
	
	/** The end position. */
	private Integer           endPosition;
	
	/** The posted on. */
	private DateTime          postedOn;
	
	/** The posted by. */
	private Person            postedBy;
	
	/** The origin. */
	private Attachment        origin;
	
	/**
	 * Instantiates a new log entry.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public LogEntry() {
		// stub
	}
	
	/**
	 * Instantiates a new log entry.
	 * 
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 * @param message
	 *            the message
	 * @param level
	 *            the level
	 * @param timestamp
	 *            the timestamp
	 */
	public LogEntry(final Integer startPosition, final Integer endPosition, final String message, final Level level,
	        final DateTime timestamp) {
		setStartPosition(startPosition);
		setEndPosition(endPosition);
		setTimestamp(timestamp);
		setMessage(message);
		setLevel(level);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	@Transient
	public String getClassName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return JavaUtils.getHandle(this);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the end position.
	 * 
	 * @return the endPosition
	 */
	@Basic
	public Integer getEndPosition() {
		return this.endPosition;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets the java posted on.
	 * 
	 * @return the java posted on
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "postedOn")
	public Date getJavaPostedOn() {
		return getPostedOn() != null
		                            ? getPostedOn().toDate()
		                            : null;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 * @deprecated this has to remain for JPA
	 */
	@Deprecated
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaTimestamp() {
		return getTimestamp() != null
		                             ? getTimestamp().toDate()
		                             : null;
	}
	
	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	@Enumerated (EnumType.STRING)
	public Level getLevel() {
		return this.level;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	@Basic
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	public Attachment getOrigin() {
		return this.origin;
	}
	
	/**
	 * Gets the posted by.
	 * 
	 * @return the postedBy
	 */
	@ManyToOne
	public Person getPostedBy() {
		return this.postedBy;
	}
	
	/**
	 * Gets the posted on.
	 * 
	 * @return the postedOn
	 */
	@Transient
	public DateTime getPostedOn() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.postedOn;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the source class.
	 * 
	 * @return the sourceClass
	 */
	@Basic
	public String getSourceClass() {
		return this.sourceClass;
	}
	
	/**
	 * Gets the start position.
	 * 
	 * @return the startPosition
	 */
	public Integer getStartPosition() {
		return this.startPosition;
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Sets the end position.
	 * 
	 * @param endPosition
	 *            the endPosition to set
	 */
	public void setEndPosition(final Integer endPosition) {
		this.endPosition = endPosition;
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
	 * Sets the java posted on.
	 * 
	 * @param timestamp
	 *            the new java posted on
	 */
	public void setJavaPostedOn(final Date timestamp) {
		if (timestamp != null) {
			setPostedOn(new DateTime(timestamp));
		} else {
			setPostedOn(null);
		}
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @param date
	 *            the new java timestamp
	 * @deprecated this has to remain for JPA
	 */
	@Deprecated
	public void setJavaTimestamp(final Date date) {
		setTimestamp(date != null
		                         ? new DateTime(date)
		                         : null);
	}
	
	/**
	 * Sets the level.
	 * 
	 * @param level
	 *            the level to set
	 */
	public void setLevel(final Level level) {
		this.level = level;
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
	 * Sets the origin.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(final Attachment origin) {
		this.origin = origin;
	}
	
	/**
	 * Sets the posted by.
	 * 
	 * @param postedBy
	 *            the postedBy to set
	 */
	public void setPostedBy(final Person postedBy) {
		this.postedBy = postedBy;
	}
	
	/**
	 * Sets the posted on.
	 * 
	 * @param postedOn
	 *            the postedOn to set
	 */
	public void setPostedOn(final DateTime postedOn) {
		this.postedOn = postedOn;
	}
	
	/**
	 * Sets the source class.
	 * 
	 * @param sourceClass
	 *            the sourceClass to set
	 */
	public void setSourceClass(final String sourceClass) {
		this.sourceClass = sourceClass;
	}
	
	/**
	 * Sets the start position.
	 * 
	 * @param startPosition
	 *            the startPosition to set
	 */
	public void setStartPosition(final Integer startPosition) {
		this.startPosition = startPosition;
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("LogEntry [level=");
		builder.append(getLevel());
		builder.append(", timestamp=");
		builder.append(getTimestamp());
		builder.append(", sourceClass=");
		builder.append(getSourceClass());
		builder.append(", message=");
		builder.append(getMessage());
		builder.append("]");
		
		return builder.toString();
	}
	
}

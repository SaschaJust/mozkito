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
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.persistence.Annotated;
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
	public Integer getEndPosition() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.endPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	public int getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.level;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	@Basic
	public String getMessage() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.message;
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
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.sourceClass;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the start position.
	 * 
	 * @return the startPosition
	 */
	public Integer getStartPosition() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.startPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.endPosition = endPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.id = id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @deprecated this has to remain for JPA
	 * @param date
	 *            the new java timestamp
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
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.level = level;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.message = message;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the source class.
	 * 
	 * @param sourceClass
	 *            the sourceClass to set
	 */
	public void setSourceClass(final String sourceClass) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.sourceClass = sourceClass;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the start position.
	 * 
	 * @param startPosition
	 *            the startPosition to set
	 */
	public void setStartPosition(final Integer startPosition) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.startPosition = startPosition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		builder.append(this.level);
		builder.append(", timestamp=");
		builder.append(this.timestamp);
		builder.append(", sourceClass=");
		builder.append(this.sourceClass);
		builder.append(", message=");
		builder.append(this.message);
		builder.append("]");
		return builder.toString();
	}
	
	// /**
	// * {@inheritDoc}
	// *
	// * @see java.lang.Object#toString()
	// */
	// public String toString2() {
	// final StringBuilder builder = new StringBuilder();
	//
	// builder.append("LogEntry [");
	// builder.append(this.timestamp);
	// builder.append("] [");
	// builder.append(this.level);
	// builder.append("]");
	// if (getSourceClass() != null) {
	// builder.append(" [").append(getSourceClass()).append("]");
	// }
	// builder.append(" ").append(this.message);
	//
	// return builder.toString();
	// }
	
}

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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class LogEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class LogEntry implements Annotated {
	
	/**
     * 
     */
	private static final long serialVersionUID = -966097018041268855L;
	
	/** The id. */
	private int               id;
	
	/** The line. */
	private String            line;
	
	/** The timestamp. */
	private DateTime          timestamp;
	
	/**
	 * Instantiates a new log entry.
	 * 
	 * @param line
	 *            the line
	 * @param timestamp
	 *            the timestamp
	 */
	public LogEntry(final String line, final DateTime timestamp) {
		setTimestamp(timestamp);
		setLine(line);
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
	 */
	@Deprecated
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaTimestamp() {
		return getTimestamp() != null
		                             ? getTimestamp().toDate()
		                             : null;
	}
	
	/**
	 * Gets the line.
	 * 
	 * @return the line
	 */
	@Basic
	public String getLine() {
		return this.line;
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
	 * Sets the line.
	 * 
	 * @param line
	 *            the line to set
	 */
	public void setLine(final String line) {
		this.line = line;
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
	
}

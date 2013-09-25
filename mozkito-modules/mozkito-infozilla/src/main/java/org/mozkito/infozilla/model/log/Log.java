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
package org.mozkito.infozilla.model.log;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class Log.
 */
@Entity
public class Log implements Annotated, Attachable, Inlineable, Iterable<LogEntry> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8298783974899124685L;
	
	/** The end. */
	private DateTime          end;
	
	/** The end position. */
	private Integer           endPosition;
	
	/** The entries. */
	private List<LogEntry>    entries          = new LinkedList<>();
	
	/** The id. */
	private int               id;
	
	/** The origin. */
	private Attachment        origin;
	
	/** The start. */
	private DateTime          start;
	
	/** The start position. */
	private Integer           startPosition;
	
	/**
	 * Adds the.
	 * 
	 * @param entry
	 *            the entry
	 * @return true, if successful
	 */
	@Transient
	public boolean add(final LogEntry entry) {
		return getEntries().add(entry);
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
	 * Gets the end.
	 * 
	 * @return the end
	 */
	@Transient
	public DateTime getEnd() {
		return this.end;
	}
	
	/**
	 * Gets the end position.
	 * 
	 * @return the endPosition
	 */
	@Basic
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
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<LogEntry> getEntries() {
		return this.entries;
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
	 * Gets the java end.
	 * 
	 * @return the java end
	 */
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaEnd() {
		return getEnd() != null
		                       ? getEnd().toDate()
		                       : null;
	}
	
	/**
	 * Gets the java start.
	 * 
	 * @return the java start
	 */
	@Temporal (TemporalType.TIMESTAMP)
	public Date getJavaStart() {
		return getStart() != null
		                         ? getStart().toDate()
		                         : null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.elements.Attachable#getOrigin()
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Attachment getOrigin() {
		return this.origin;
	}
	
	/**
	 * Gets the start.
	 * 
	 * @return the start
	 */
	@Transient
	public DateTime getStart() {
		return this.start;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.elements.Inlineable#getStartPosition()
	 */
	@Override
	@Basic
	public Integer getStartPosition() {
		return this.startPosition;
	}
	
	/**
	 * Checks if is inlined.
	 * 
	 * @return the inlined
	 */
	@Basic
	public boolean isInlined() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Attachment attachment = getOrigin();
			final Integer position = getEndPosition();
			
			SANITY: {
				assert ((attachment == null) && (position != null)) || ((attachment != null) && (position == null));
			}
			
			return position != null;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<LogEntry> iterator() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getEntries().iterator();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the end to set
	 */
	public void setEnd(final DateTime end) {
		this.end = end;
	}
	
	/**
	 * Sets the end position.
	 * 
	 * @param endPosition
	 *            the endPosition to set
	 */
	public void setEndPosition(final int endPosition) {
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
	 * Sets the entries.
	 * 
	 * @param entries
	 *            the entries to set
	 */
	public void setEntries(final List<LogEntry> entries) {
		this.entries = entries;
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
	 * Sets the java end.
	 * 
	 * @param date
	 *            the new java end
	 */
	public void setJavaEnd(final Date date) {
		setEnd(date != null
		                   ? new DateTime(date)
		                   : null);
	}
	
	/**
	 * Sets the java start.
	 * 
	 * @param date
	 *            the new java start
	 */
	public void setJavaStart(final Date date) {
		setStart(date != null
		                     ? new DateTime(date)
		                     : null);
	}
	
	/**
	 * Sets the origin.
	 * 
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(final Attachment origin) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.origin = origin;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the start.
	 * 
	 * @param start
	 *            the start to set
	 */
	public void setStart(final DateTime start) {
		this.start = start;
	}
	
	/**
	 * Sets the start position.
	 * 
	 * @param startPosition
	 *            the startPosition to set
	 */
	public void setStartPosition(final int startPosition) {
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
	 * Size.
	 * 
	 * @return the int
	 */
	@Transient
	public int size() {
		return getEntries().size();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Log [start=");
		builder.append(this.start);
		builder.append(", end=");
		builder.append(this.end);
		builder.append("]");
		for (final LogEntry entry : getEntries()) {
			builder.append(System.getProperty("line.separator")).append(entry);
		}
		return builder.toString();
	}
}

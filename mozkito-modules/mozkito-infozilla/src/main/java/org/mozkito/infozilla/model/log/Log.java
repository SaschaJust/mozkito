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
import javax.persistence.Column;
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
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

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
	
	/** The posted on. */
	private DateTime          postedOn;
	
	/** The posted by. */
	private Person            postedBy;
	
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
		return this.endPosition;
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
		return this.id;
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
	@Override
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
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
	@Transient
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
	public void setEndPosition(final Integer endPosition) {
		this.endPosition = endPosition;
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
		this.id = id;
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
	public void setStartPosition(final Integer startPosition) {
		this.startPosition = startPosition;
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
		builder.append(getStart());
		builder.append(", end=");
		builder.append(getEnd());
		builder.append("]");
		
		for (final LogEntry entry : getEntries()) {
			builder.append(FileUtils.lineSeparator).append(entry);
		}
		
		return builder.toString();
	}
}

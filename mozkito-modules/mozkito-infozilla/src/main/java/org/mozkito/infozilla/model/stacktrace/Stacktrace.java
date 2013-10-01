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
package org.mozkito.infozilla.model.stacktrace;

import java.util.Date;
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
import javax.persistence.OneToOne;
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

/**
 * The Class Stacktrace.
 */
@Entity
public class Stacktrace implements Annotated, Attachable, Inlineable {
	
	/** The Constant serialVersionUID. */
	private static final long     serialVersionUID = 3213193988146023930L;
	
	/** The causes. */
	private Stacktrace            cause            = null;
	
	/** The end position. */
	private Integer               endPosition;
	
	/** The exception type. */
	private String                exceptionType;
	
	/** The id. */
	private int                   id;
	
	/** The origin. */
	private Attachment            origin;
	
	/** The reason. */
	private String                reason;
	
	/** The start position. */
	private Integer               startPosition;
	
	/** The trace. */
	private List<StacktraceEntry> trace            = new LinkedList<>();
	
	/** Denotes the number of truncated (following but not shown) trace entries. */
	private Integer               more;
	
	/** The posted on. */
	private DateTime              postedOn;
	
	/** The posted by. */
	private Person                postedBy;
	
	/**
	 * Instantiates a new stacktrace.
	 * 
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public Stacktrace() {
		// stub
	}
	
	/**
	 * Instantiates a new stacktrace.
	 * 
	 * @param origin
	 *            the origin
	 * @param exceptionType
	 *            the exception type
	 * @param reason
	 *            the reason
	 * @param more
	 *            the more
	 */
	public Stacktrace(final Attachment origin, final String exceptionType, final String reason, final Integer more) {
		super();
		this.origin = origin;
		this.exceptionType = exceptionType;
		this.reason = reason;
		this.more = more;
	}
	
	/**
	 * Instantiates a new stacktrace.
	 * 
	 * @param startPosition
	 *            the start position
	 * @param endPosition
	 *            the end position
	 * @param exceptionType
	 *            the exception type
	 * @param reason
	 *            the reason
	 * @param more
	 *            the more
	 */
	public Stacktrace(final Integer startPosition, final Integer endPosition, final String exceptionType,
	        final String reason, final Integer more) {
		super();
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.exceptionType = exceptionType;
		this.reason = reason;
		this.more = more;
	}
	
	/**
	 * Adds the.
	 * 
	 * @param entry
	 *            the entry
	 * @return true, if successful
	 */
	public boolean add(final StacktraceEntry entry) {
		return getTrace().add(entry);
	}
	
	/**
	 * Gets the cause.
	 * 
	 * @return the cause
	 */
	@OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Stacktrace getCause() {
		return this.cause;
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
	 * Gets the exception type.
	 * 
	 * @return the exceptionType
	 */
	@Basic
	public String getExceptionType() {
		return this.exceptionType;
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
	 * Gets the more.
	 * 
	 * @return the more
	 */
	@Basic
	public Integer getMore() {
		return this.more;
	}
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
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
	 * Gets the reason.
	 * 
	 * @return the reason
	 */
	@Basic
	public String getReason() {
		return this.reason;
	}
	
	/**
	 * Gets the start position.
	 * 
	 * @return the startPosition
	 */
	@Basic
	public Integer getStartPosition() {
		return this.startPosition;
	}
	
	/**
	 * Gets the trace.
	 * 
	 * @return the trace
	 */
	@OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<StacktraceEntry> getTrace() {
		return this.trace;
	}
	
	/**
	 * Sets the cause.
	 * 
	 * @param cause
	 *            the cause to set
	 */
	public void setCause(final Stacktrace cause) {
		this.cause = cause;
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
	 * Sets the exception type.
	 * 
	 * @param exceptionType
	 *            the exceptionType to set
	 */
	public void setExceptionType(final String exceptionType) {
		this.exceptionType = exceptionType;
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
	 * Sets the more.
	 * 
	 * @param more
	 *            the more to set
	 */
	public void setMore(final Integer more) {
		this.more = more;
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
	 * Sets the reason.
	 * 
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(final String reason) {
		this.reason = reason;
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
	 * Sets the trace.
	 * 
	 * @param trace
	 *            the trace to set
	 */
	public void setTrace(final List<StacktraceEntry> trace) {
		this.trace = trace;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("Stacktrace [startPosition=");
		builder.append(getStartPosition());
		builder.append(", endPosition=");
		builder.append(getEndPosition());
		builder.append("]");
		builder.append('\n');
		builder.append(getExceptionType());
		builder.append(": ");
		builder.append(getReason());
		
		for (final StacktraceEntry entry : getTrace()) {
			builder.append('\n').append(entry);
		}
		
		if (getMore() != null) {
			builder.append('\n').append("... ").append(getMore()).append(" more");
		}
		
		if (getCause() != null) {
			builder.append('\n').append("Caused by: ");
			builder.append(getCause());
		}
		
		return builder.toString();
	}
	
}

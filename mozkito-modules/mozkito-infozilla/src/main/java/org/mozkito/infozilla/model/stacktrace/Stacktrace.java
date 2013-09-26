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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;
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
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.cause;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Gets the exception type.
	 * 
	 * @return the exceptionType
	 */
	@Basic
	public String getExceptionType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.exceptionType;
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
	 * Gets the more.
	 * 
	 * @return the more
	 */
	@Basic
	public Integer getMore() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.more;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the origin.
	 * 
	 * @return the origin
	 */
	@OneToMany (cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Attachment getOrigin() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.origin;
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
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.reason;
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
	@Basic
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
	 * Gets the trace.
	 * 
	 * @return the trace
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<StacktraceEntry> getTrace() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.trace;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the cause.
	 * 
	 * @param cause
	 *            the cause to set
	 */
	public void setCause(final Stacktrace cause) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.cause = cause;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Sets the exception type.
	 * 
	 * @param exceptionType
	 *            the exceptionType to set
	 */
	public void setExceptionType(final String exceptionType) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.exceptionType = exceptionType;
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
	 * Sets the more.
	 * 
	 * @param more
	 *            the more to set
	 */
	public void setMore(final Integer more) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.more = more;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Sets the reason.
	 * 
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(final String reason) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.reason = reason;
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
	 * Sets the trace.
	 * 
	 * @param trace
	 *            the trace to set
	 */
	public void setTrace(final List<StacktraceEntry> trace) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.trace = trace;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
		builder.append(this.startPosition);
		builder.append(", endPosition=");
		builder.append(this.endPosition);
		builder.append("]");
		builder.append('\n');
		builder.append(this.exceptionType);
		builder.append(": ");
		builder.append(this.reason);
		
		for (final StacktraceEntry entry : this.trace) {
			builder.append('\n').append(entry);
		}
		
		if (this.more != null) {
			builder.append('\n').append("... ").append(this.more).append(" more");
		}
		
		if (this.cause != null) {
			builder.append('\n').append("Caused by: ");
			builder.append(this.cause);
		}
		
		return builder.toString();
	}
	
}

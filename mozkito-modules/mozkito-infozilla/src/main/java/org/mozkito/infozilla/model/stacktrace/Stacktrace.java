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

import org.mozkito.infozilla.elements.Attachable;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persistence.Annotated;

/**
 * The Class Stacktrace.
 */
@Entity
public abstract class Stacktrace implements Annotated, Attachable, Inlineable {
	
	/** The Constant serialVersionUID. */
	private static final long     serialVersionUID = 3213193988146023930L;
	
	/** The causes. */
	private List<Stacktrace>      causes           = new LinkedList<>();
	
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
	
	/**
	 * Gets the causes.
	 * 
	 * @return the causes
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<Stacktrace> getCauses() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.causes;
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
	 * Sets the causes.
	 * 
	 * @param causes
	 *            the causes to set
	 */
	public void setCauses(final List<Stacktrace> causes) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.causes = causes;
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
	
}

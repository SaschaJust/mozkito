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

package org.mozkito.infozilla.model.stacktrace;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.mozkito.persistence.Annotated;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class StacktraceEntry.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class StacktraceEntry implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2798770015254567673L;
	
	/** The entry class name. */
	private String            entryClassName;
	
	/** The file name. */
	private String            fileName;
	
	/** The id. */
	private int               id;
	
	/** The line number. */
	private Integer           lineNumber;
	
	/** The method name. */
	private String            methodName;
	
	/**
	 * @param entryClassName
	 * @param fileName
	 * @param methodName
	 * @param lineNumber
	 */
	public StacktraceEntry(final String entryClassName, final String fileName, final String methodName,
	        final Integer lineNumber) {
		super();
		this.entryClassName = entryClassName;
		this.fileName = fileName;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
	@Transient
	public final String getClassName() {
		return JavaUtils.getHandle(StacktraceEntry.class);
	}
	
	/**
	 * Gets the entry class name.
	 * 
	 * @return the entryClassName
	 */
	@Basic
	public String getEntryClassName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.entryClassName;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the file name.
	 * 
	 * @return the fileName
	 */
	@Basic
	public String getFileName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.fileName;
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
	 * Gets the line number.
	 * 
	 * @return the lineNumber
	 */
	@Basic
	public Integer getLineNumber() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.lineNumber;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the method name.
	 * 
	 * @return the methodName
	 */
	@Basic
	public String getMethodName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.methodName;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the entry class name.
	 * 
	 * @param entryClassName
	 *            the entryClassName to set
	 */
	public void setEntryClassName(final String entryClassName) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.entryClassName = entryClassName;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the file name.
	 * 
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(final String fileName) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.fileName = fileName;
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
	 * Sets the line number.
	 * 
	 * @param lineNumber
	 *            the lineNumber to set
	 */
	public void setLineNumber(final Integer lineNumber) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.lineNumber = lineNumber;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the method name.
	 * 
	 * @param methodName
	 *            the methodName to set
	 */
	public void setMethodName(final String methodName) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.methodName = methodName;
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
		builder.append("  at ");
		builder.append(this.entryClassName);
		builder.append(".");
		builder.append(this.methodName);
		builder.append("(");
		builder.append(this.fileName);
		builder.append(":");
		builder.append(this.lineNumber);
		builder.append(")");
		return builder.toString();
	}
	
}

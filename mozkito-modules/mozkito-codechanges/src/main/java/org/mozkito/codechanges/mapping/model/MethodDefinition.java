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

package org.mozkito.codechanges.mapping.model;

import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;

/**
 * The Class MethodDefinition.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MethodDefinition {
	
	/** The name. */
	private final String          name;
	
	/** The revision. */
	private final Revision        revision;
	
	/** The handle. */
	private final Handle          handle;
	
	/**
	 * The class the method is defined in.
	 * 
	 * This might potentially be null.
	 */
	private final ClassDefinition classDefinition;
	
	/** The start line. */
	private final int             startLine;
	
	/** The end line. */
	private final int             endLine;
	
	/**
	 * Instantiates a new method definition.
	 * 
	 * @param name
	 *            the name
	 * @param revision
	 *            the revision
	 * @param handle
	 *            the handle
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 */
	public MethodDefinition(final String name, final Revision revision, final Handle handle, final int startLine,
	        final int endLine) {
		this(name, revision, handle, startLine, endLine, null);
	}
	
	/**
	 * Instantiates a new method definition.
	 * 
	 * @param name
	 *            the name
	 * @param revision
	 *            the revision
	 * @param handle
	 *            the handle
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param classDefinition
	 *            the class definition
	 */
	public MethodDefinition(final String name, final Revision revision, final Handle handle, final int startLine,
	        final int endLine, final ClassDefinition classDefinition) {
		PRECONDITIONS: {
			if (name == null) {
				throw new NullPointerException();
			}
			if (revision == null) {
				throw new NullPointerException();
			}
			if (handle == null) {
				throw new NullPointerException();
			}
			if (startLine <= 0) {
				throw new IllegalArgumentException();
			}
			if (endLine <= 0) {
				throw new IllegalArgumentException();
			}
			if (endLine < startLine) {
				throw new IllegalArgumentException();
			}
		}
		
		this.name = name;
		this.revision = revision;
		this.handle = handle;
		this.startLine = startLine;
		this.endLine = endLine;
		this.classDefinition = classDefinition;
	}
	
	/**
	 * @return the classDefinition
	 */
	public ClassDefinition getClassDefinition() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.classDefinition;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the endLine
	 */
	public int getEndLine() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.endLine;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the handle
	 */
	public Handle getHandle() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.handle;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.name;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the revision
	 */
	public Revision getRevision() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.revision;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the startLine
	 */
	public int getStartLine() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.startLine;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

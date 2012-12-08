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

/**
 * The Class TalkbackEntry.
 */
public class TalkbackEntry extends StacktraceEntry {
	
	// Types definitions
	/** The Constant CLASSMETHODLINE. */
	public static final int CLASSMETHODLINE = 1;
	
	/** The Constant METHODCALLLINE. */
	public static final int METHODCALLLINE  = 2;
	
	/** The Constant METHODLINE. */
	public static final int METHODLINE      = 3;
	
	/** The Constant LIBRARYLINE. */
	public static final int LIBRARYLINE     = 4;
	
	/** The Constant ADDRESSLINE. */
	public static final int ADDRESSLINE     = 5;
	
	// The name
	/** The name. */
	private String          name;
	
	// The location
	/** The location. */
	private String          location;
	
	// The type
	/** The type. */
	private int             type            = 0;
	
	/**
	 * Instantiates a new talkback entry.
	 * 
	 * @param name
	 *            the name
	 * @param location
	 *            the location
	 * @param type
	 *            the type
	 */
	public TalkbackEntry(final String name, final String location, final int type) {
		super();
		this.name = name;
		this.location = location;
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.model.stacktrace.StacktraceEntry#getClassName()
	 */
	@Override
	public String getClassName() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getClassName' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.model.stacktrace.StacktraceEntry#getFileName()
	 */
	@Override
	public String getFileName() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getFileName' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.model.stacktrace.StacktraceEntry#getLineNumber()
	 */
	@Override
	public String getLineNumber() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getLineNumber' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return this.location.trim();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.model.stacktrace.StacktraceEntry#getMethodName()
	 */
	@Override
	public String getMethodName() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getMethodName' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name.trim();
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Sets the location.
	 * 
	 * @param location
	 *            the new location
	 */
	public void setLocation(final String location) {
		this.location = location;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(final int type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (this.name.trim() + " (" + this.location.trim() + ")");
	}
}

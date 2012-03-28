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
package de.unisaarland.cs.st.moskito.infozilla.model.stacktrace;

/**
 * The Class TalkbackEntry.
 */
public class TalkbackEntry {
	
	// Types definitions
	/** The Constant CLASSMETHODLINE. */
	public final static int CLASSMETHODLINE = 1;
	
	/** The Constant METHODCALLLINE. */
	public final static int METHODCALLLINE  = 2;
	
	/** The Constant METHODLINE. */
	public final static int METHODLINE      = 3;
	
	/** The Constant LIBRARYLINE. */
	public final static int LIBRARYLINE     = 4;
	
	/** The Constant ADDRESSLINE. */
	public final static int ADDRESSLINE     = 5;
	
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
	 * @param name the name
	 * @param location the location
	 * @param type the type
	 */
	public TalkbackEntry(String name, String location, int type) {
		super();
		this.name = name;
		this.location = location;
		this.type = type;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name.trim();
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location.trim();
	}
	
	/**
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (name.trim() + " (" + location.trim() + ")");
	}
}

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

public class TalkbackEntry {
	
	// Types definitions
	public final static int CLASSMETHODLINE = 1;
	public final static int METHODCALLLINE  = 2;
	public final static int METHODLINE      = 3;
	public final static int LIBRARYLINE     = 4;
	public final static int ADDRESSLINE     = 5;
	
	// The name
	private String          name;
	
	// The location
	private String          location;
	
	// The type
	private int             type            = 0;
	
	public TalkbackEntry(String name, String location, int type) {
		super();
		this.name = name;
		this.location = location;
		this.type = type;
	}
	
	public String getName() {
		return name.trim();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location.trim();
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String toString() {
		return (name.trim() + " (" + location.trim() + ")");
	}
}

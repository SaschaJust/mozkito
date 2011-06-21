/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.infozilla.model.stacktrace;

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

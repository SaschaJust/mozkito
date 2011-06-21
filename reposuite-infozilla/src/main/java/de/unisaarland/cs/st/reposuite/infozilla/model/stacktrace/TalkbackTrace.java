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

import java.util.ArrayList;
import java.util.List;

public class TalkbackTrace {
	
	private List<TalkbackEntry> entries;
	
	public TalkbackTrace(List<TalkbackEntry> entries) {
		super();
		this.entries = entries;
	}
	
	public TalkbackTrace() {
		super();
		this.entries = new ArrayList<TalkbackEntry>();
	}
	
	public List<TalkbackEntry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<TalkbackEntry> entries) {
		this.entries = entries;
	}
	
	public String toString() {
		if (entries != null) {
			StringBuilder sb = new StringBuilder();
			for (TalkbackEntry entry : entries) {
				sb.append(entry.toString() + System.getProperty("line.separator"));
			}
			return (sb.toString());
		} else {
			return (this.getClass().getName() + " " + this.hashCode());
		}
	}
}

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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class TalkbackTrace.
 */
public class TalkbackTrace {
	
	/** The entries. */
	private List<TalkbackEntry> entries;
	
	/**
	 * Instantiates a new talkback trace.
	 */
	public TalkbackTrace() {
		super();
		this.entries = new ArrayList<TalkbackEntry>();
	}
	
	/**
	 * Instantiates a new talkback trace.
	 * 
	 * @param entries
	 *            the entries
	 */
	public TalkbackTrace(final List<TalkbackEntry> entries) {
		super();
		this.entries = entries;
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	public List<TalkbackEntry> getEntries() {
		return this.entries;
	}
	
	/**
	 * Sets the entries.
	 * 
	 * @param entries
	 *            the new entries
	 */
	public void setEntries(final List<TalkbackEntry> entries) {
		this.entries = entries;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.entries != null) {
			final StringBuilder sb = new StringBuilder();
			for (final TalkbackEntry entry : this.entries) {
				sb.append(entry.toString() + System.getProperty("line.separator"));
			}
			return (sb.toString());
		}
		return (this.getClass().getName() + " " + hashCode());
	}
}

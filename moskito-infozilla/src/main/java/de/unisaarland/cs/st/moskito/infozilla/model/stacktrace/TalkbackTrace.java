/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.infozilla.model.stacktrace;

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

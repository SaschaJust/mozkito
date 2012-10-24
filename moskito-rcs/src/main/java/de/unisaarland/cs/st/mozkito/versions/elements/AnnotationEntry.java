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
package de.unisaarland.cs.st.mozkito.versions.elements;

import org.joda.time.DateTime;

import net.ownhero.dev.kisa.Logger;

public class AnnotationEntry {
	
	private final String   alternativeFilePath;
	private final String   line;
	private final String   revision;
	private final DateTime timestamp;
	private final String   username;
	
	public AnnotationEntry(final String revision, final String username, final DateTime timestamp, final String line) {
		this(revision, username, timestamp, line, null);
	}
	
	public AnnotationEntry(final String revision, final String username, final DateTime timestamp, final String line,
	        final String alternativeFilePath) {
		this.revision = revision;
		this.alternativeFilePath = alternativeFilePath;
		this.username = username;
		this.timestamp = timestamp;
		this.line = line;
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	public String getAlternativeFilePath() {
		return this.alternativeFilePath;
		
	}
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return the line
	 */
	public String getLine() {
		return this.line;
	}
	
	public String getRevision() {
		return this.revision;
	}
	
	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	public boolean hasAlternativePath() {
		return (this.alternativeFilePath != null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AnnotationEntry [revision=" + this.revision + ", alternativeFilePath=" + this.alternativeFilePath
		        + ", username=" + this.username + ", timestamp=" + this.timestamp + ", line=" + this.line + "]";
	}
	
}

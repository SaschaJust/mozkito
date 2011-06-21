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
package de.unisaarland.cs.st.reposuite.rcs.elements;

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

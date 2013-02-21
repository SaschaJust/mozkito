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
package org.mozkito.versions.elements;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

import org.mozkito.persistence.model.Person;

/**
 * The Class LogEntry.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class LogEntry implements Comparable<LogEntry> {
	
	/** The author. */
	protected Person   author;
	
	/** The commit date. */
	protected DateTime commitDate;
	
	/** The message. */
	protected String   message;
	
	/** The previous. */
	protected LogEntry previous;
	
	/** The revision. */
	protected String   revision;
	
	/** The original id. */
	protected String   originalId;
	
	/**
	 * Instantiates a new log entry.
	 * 
	 * @param revision
	 *            the revision
	 * @param previous
	 *            the previous LogEntry, null if this is the first revision
	 * @param author
	 *            the author
	 * @param message
	 *            the message
	 * @param dateTime
	 *            the date time
	 * @param originalId
	 *            the original id
	 */
	public LogEntry(@NotNull final String revision, final LogEntry previous, @NotNull final Person author,
	        @NotNull final String message, @NotNull final DateTime dateTime, final String originalId) {
		this.revision = revision;
		this.author = author;
		this.message = message;
		if (this.message.endsWith(FileUtils.lineSeparator)) {
			this.message = this.message.substring(0, this.message.length() - 1);
		}
		
		this.previous = previous;
		this.commitDate = dateTime;
		this.originalId = originalId;
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getClassName() + ": " + this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final LogEntry o) {
		return this.commitDate.compareTo(o.commitDate);
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name of this class.
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Gets the date time.
	 * 
	 * @return the date time
	 */
	public DateTime getDateTime() {
		return this.commitDate;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Returns the ID of the log entry in the original repository if the repository was converted (e.g. from SVN to Git)
	 * 
	 * @return the original id
	 */
	public String getOriginalId() {
		if (this.originalId != null) {
			return this.originalId;
		}
		return "";
	}
	
	/**
	 * Gets the revision.
	 * 
	 * @return the revision
	 */
	public String getRevision() {
		return this.revision;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogEntry [revision=" + this.revision + ", author=" + this.author + ", message="
		        + StringEscapeUtils.escapeJava(this.message) + ", commitDate=" + this.commitDate + ", previous="
		        + (this.previous != null
		                                ? this.previous.revision
		                                : "(null)") + "]";
	}
	
}

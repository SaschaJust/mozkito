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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.infozilla.model.log;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;

/**
 * The Class LogEntry.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class LogEntry {
	
	/** The tokens. */
	Map<String, String> tokens = new HashMap<String, String>();
	
	/** The timestamp. */
	DateTime            timestamp;
	
	/** The line. */
	String              line;
	
	/**
	 * Instantiates a new log entry.
	 *
	 * @param regex the regex
	 * @param string the string
	 */
	public LogEntry(final Regex regex, final String string) {
		regex.find(string);
		for (String groupName : regex.getGroupNames()) {
			this.tokens.put(groupName, regex.getGroup(groupName));
		}
		setTimestamp(buildTimestamp(regex));
		setLine(string);
	}
	
	/**
	 * Builds the timestamp.
	 *
	 * @param regex the regex
	 * @return the date time
	 */
	private DateTime buildTimestamp(final Regex regex) {
		// TODO Auto-generated method stub
		return new DateTime();
	}
	
	/**
	 * Gets the line.
	 *
	 * @return the line
	 */
	public String getLine() {
		return this.line;
	}
	
	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Gets the tokens.
	 *
	 * @return the tokens
	 */
	public Map<String, String> getTokens() {
		return this.tokens;
	}
	
	/**
	 * Sets the line.
	 *
	 * @param line the line to set
	 */
	public void setLine(final String line) {
		this.line = line;
	}
	
	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Sets the tokens.
	 *
	 * @param tokens the tokens to set
	 */
	public void setTokens(final Map<String, String> tokens) {
		this.tokens = tokens;
	}
}

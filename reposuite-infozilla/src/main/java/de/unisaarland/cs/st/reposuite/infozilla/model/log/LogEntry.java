/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.log;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class LogEntry {
	
	Map<String, String> tokens = new HashMap<String, String>();
	
	DateTime            timestamp;
	String              line;
	
	public LogEntry(final Regex regex, final String string) {
		regex.find(string);
		for (String groupName : regex.getGroupNames()) {
			this.tokens.put(groupName, regex.getGroup(groupName));
		}
		setTimestamp(buildTimestamp(regex));
		setLine(string);
	}
	
	private DateTime buildTimestamp(final Regex regex) {
		// TODO Auto-generated method stub
		return new DateTime();
	}
	
	/**
	 * @return the line
	 */
	public String getLine() {
		return this.line;
	}
	
	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @return the tokens
	 */
	public Map<String, String> getTokens() {
		return this.tokens;
	}
	
	/**
	 * @param line the line to set
	 */
	public void setLine(final String line) {
		this.line = line;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @param tokens the tokens to set
	 */
	public void setTokens(final Map<String, String> tokens) {
		this.tokens = tokens;
	}
}

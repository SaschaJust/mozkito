package de.unisaarland.cs.st.reposuite.rcs.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * The Class GitLogParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
class GitLogParser {
	
	protected static DateTimeFormatter gitLogDateFormat = DateTimeFormat.forPattern("EEE MMM d HH:mm:ss yyyy Z");
	protected static Regex             regex            = new Regex(
	"^(({plain}[a-zA-Z]+)$|({name}[^\\s<]+)?\\s*({lastname}[^\\s<]+\\s+)?(<({email}[^>]+)>)?)");
	protected static Regex             messageRegex     = new Regex(".*$$\\s*git-svn-id:.*");
	private static final PersonManager personManager    = new PersonManager();
	
	/**
	 * Parses the.
	 * 
	 * @param logMessage
	 *            List of strings corresponding to the lines of the log message.
	 *            (not null)
	 * @return the list
	 */
	protected static List<LogEntry> parse(final List<String> logMessage) {
		assert (logMessage != null);
		List<LogEntry> result = new ArrayList<LogEntry>();
		int lineCounter = 0;
		
		String currentID = null;
		Person author = null;
		String date = null;
		boolean append = true;
		StringBuilder message = new StringBuilder();
		for (String line : logMessage) {
			++lineCounter;
			if (line.startsWith("commit")) {
				if (currentID != null) {
					DateTime dateTime = new DateTime();
					dateTime = gitLogDateFormat.parseDateTime(date);
					LogEntry previous = null;
					if (result.size() > 0) {
						previous = result.get(result.size() - 1);
					}
					result.add(new LogEntry(currentID, previous, personManager.getPerson((author != null ? author
							: null)), message.toString(), dateTime));
					currentID = null;
					author = null;
					date = null;
					append = true;
					message = new StringBuilder();
				}
				String[] commitParts = line.split(" ");
				if (commitParts.length != 2) {
					if (Logger.logError()) {
						Logger.error("Found error in git log file: line " + lineCounter + ". Abort parsing.");
					}
					return null;
				}
				currentID = commitParts[1].trim();
			} else if (line.startsWith("Author:")) {
				String[] authorParts = line.split(":");
				if (authorParts.length != 2) {
					if (Logger.logError()) {
						Logger.error("Found error in git log file: line " + lineCounter + ". Abort parsing.");
					}
					return null;
				}
				String username = null;
				String fullname = null;
				String email = null;
				regex.find(authorParts[1].trim());
				if (regex.getGroup("plain") != null) {
					username = regex.getGroup("plain");
				} else if ((regex.getGroup("name") != null) && (regex.getGroup("lastname") != null)) {
					fullname = regex.getGroup("name") + " " + regex.getGroup("lastname");
				} else if (regex.getGroup("name") != null) {
					username = regex.getGroup("name");
				}
				if (regex.getGroup("email") != null) {
					email = regex.getGroup("email");
				}
				author = new Person(username, fullname, email);
			} else if (line.startsWith("AuthorDate:")) {
				String[] authorDateParts = line.split(": ");
				if (authorDateParts.length != 2) {
					if (Logger.logError()) {
						Logger.error("Found error in git log file: line " + lineCounter + ". Abort parsing.");
					}
					return null;
				}
				date = authorDateParts[1].trim();
			} else if (line.startsWith(" ")) {
				if (line.trim().startsWith("git-svn-id")) {
					String tmpString = message.toString();
					if (tmpString.length() > 2) {
						tmpString = tmpString.substring(0, tmpString.length() - 2);
						message = new StringBuilder();
						message.append(tmpString);
					} else {
						message = new StringBuilder();
						
					}
					append = false;
				}
				if (append) {
					message.append(line.trim());
					if (message.length() > 0) {
						message.append(FileUtils.lineSeparator);
					}
				}
				
			}
		}
		if (currentID != null) {
			DateTime dateTime;
			dateTime = gitLogDateFormat.parseDateTime(date);
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(currentID, previous, personManager.getPerson((author != null ? author : null)),
					message.toString(), dateTime));
		}
		Collections.reverse(result);
		return result;
	}
}

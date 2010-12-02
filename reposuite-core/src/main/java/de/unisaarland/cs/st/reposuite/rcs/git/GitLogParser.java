package de.unisaarland.cs.st.reposuite.rcs.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * The Class GitLogParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
class GitLogParser {
	
	// protected static DateTimeFormatter gitLogDateFormat =
	// DateTimeFormat.forPattern("EEE MMM d HH:mm:ss yyyy Z");
	protected static Regex gitLogDateFormatRegex = new Regex(
	"({EEE}[A-Za-z]{3})\\s+({MMM}[A-Za-z]{3})\\s+({d}\\d{1,2})\\s+({HH}[0-2]\\d):({mm}[0-5]\\d):({ss}[0-5]\\d)\\s+({yyyy}\\d{4})(\\s+[+-]\\d{4})");
	protected static Regex regex                 = new Regex(
	"^(({plain}[a-zA-Z]+)$|({name}[^\\s<]+)?\\s*({lastname}[^\\s<]+\\s+)?(<({email}[^>]+)>)?)");
	protected static Regex messageRegex          = new Regex(".*$$\\s*git-svn-id:.*");
	
	/**
	 * Parses the list of log messages.
	 * 
	 * @param logMessages
	 *            List of strings corresponding to the lines of the log message.
	 *            (not null)
	 * @return the list of parsed log entries representing the logMessages
	 */
	protected static List<LogEntry> parse(final List<String> logMessages) {
		Condition.notNull(logMessages);
		Condition.notNull(logMessages);
		List<LogEntry> result = new ArrayList<LogEntry>();
		int lineCounter = 0;
		
		String currentID = null;
		Person author = null;
		String date = null;
		boolean append = true;
		StringBuilder message = new StringBuilder();
		for (String line : logMessages) {
			++lineCounter;
			if (line.startsWith("commit")) {
				if (currentID != null) {
					DateTime dateTime = new DateTime();
					dateTime = DateTimeUtils.parseDate(date, gitLogDateFormatRegex);
					LogEntry previous = null;
					if (result.size() > 0) {
						previous = result.get(result.size() - 1);
					}
					result.add(new LogEntry(currentID, previous, author, message.toString(), dateTime));
					currentID = null;
					author = null;
					date = null;
					append = true;
					message = new StringBuilder();
				}
				String[] commitParts = line.split(" ");
				if (commitParts.length != 2) {
					throw new UnrecoverableError("Found error in git log file: line " + lineCounter
							+ ". Abort parsing.");
				}
				currentID = commitParts[1].trim();
			} else if (line.startsWith("Author:")) {
				String[] authorParts = line.split(":");
				if (authorParts.length != 2) {
					throw new UnrecoverableError("Found error in git log file: line " + lineCounter
							+ ". Abort parsing.");
				}
				String username = null;
				String fullname = null;
				String email = null;
				regex.find(authorParts[1].trim());
				if (regex.getGroup("plain") != null) {
					username = regex.getGroup("plain").trim();
				} else if ((regex.getGroup("name") != null) && (regex.getGroup("lastname") != null)) {
					fullname = regex.getGroup("name").trim() + " " + regex.getGroup("lastname").trim();
				} else if (regex.getGroup("name") != null) {
					username = regex.getGroup("name").trim();
				}
				if (regex.getGroup("email") != null) {
					email = regex.getGroup("email").trim();
				}
				author = new Person(username, fullname, email);
			} else if (line.startsWith("AuthorDate:")) {
				String[] authorDateParts = line.split(": ");
				if (authorDateParts.length != 2) {
					throw new UnrecoverableError("Found error in git log file: line " + lineCounter
					        + ". Abort parsing.");
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
			dateTime = DateTimeUtils.parseDate(date, gitLogDateFormatRegex);
			LogEntry previous = null;
			if (result.size() > 0) {
				previous = result.get(result.size() - 1);
			}
			result.add(new LogEntry(currentID, previous, author, message.toString(), dateTime));
		}
		Collections.reverse(result);
		return result;
	}
}

package de.unisaarland.cs.st.reposuite.rcs.git;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.rcs.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class GitLogParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
class GitLogParser {
	
	protected static SimpleDateFormat gitLogDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
	
	/**
	 * Parses the.
	 * 
	 * @param logMessage
	 *            List of strings corresponding to the lines of the log message.
	 * @return the list
	 */
	protected static List<LogEntry> parse(List<String> logMessage) {
		
		List<LogEntry> result = new ArrayList<LogEntry>();
		int lineCounter = 0;
		
		String currentID = null;
		String author = null;
		String date = null;
		StringBuilder message = new StringBuilder();
		
		for (String line : logMessage) {
			++lineCounter;
			if (line.startsWith("commit")) {
				if (currentID != null) {
					DateTime dateTime = new DateTime();
					try {
						dateTime = new DateTime(gitLogDateFormat.parse(date));
					} catch (ParseException e) {
						if (RepoSuiteSettings.logError()) {
							Logger.error("Encountered error while parsing GIT commit message date `" + date
							        + "` of transaction `" + currentID + "`. Abort parsing.");
						}
						return null;
					}
					result.add(new LogEntry(currentID, result.get(result.size() - 1), author, message.toString(),
					        dateTime));
					currentID = null;
					author = null;
					date = null;
					message = new StringBuilder();
				}
				String[] commitParts = line.split(" ");
				if (commitParts.length != 2) {
					if (RepoSuiteSettings.logError()) {
						Logger.error("Found error in git log file: line " + lineCounter + ". Abort parsing.");
					}
					return null;
				}
				currentID = commitParts[1].trim();
			} else if (line.startsWith("Author:")) {
				String[] authorParts = line.split(":");
				if (authorParts.length != 2) {
					if (RepoSuiteSettings.logError()) {
						Logger.error("Found error in git log file: line " + lineCounter + ". Abort parsing.");
					}
					return null;
				}
				author = authorParts[1].trim();
			} else if (line.startsWith("AuthorDate:")) {
				String[] authorDateParts = line.split(": ");
				if (authorDateParts.length != 2) {
					if (RepoSuiteSettings.logError()) {
						Logger.error("Found error in git log file: line " + lineCounter + ". Abort parsing.");
					}
					return null;
				}
				date = authorDateParts[1].trim();
			} else if (line.startsWith(" ")) {
				message.append(line.trim());
				message.append(FileUtils.lineSeparator);
			}
		}
		if (currentID != null) {
			DateTime dateTime;
			try {
				dateTime = new DateTime(gitLogDateFormat.parse(date));
			} catch (ParseException e) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Encountered error while parsing GIT commit message date `" + date
					        + "` of transaction `" + currentID + "`. Abort parsing.");
				}
				return null;
			}
			result.add(new LogEntry(currentID, result.get(result.size() - 1), author, message.toString(), dateTime));
		}
		return result;
	}
}

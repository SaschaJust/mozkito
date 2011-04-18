package de.unisaarland.cs.st.reposuite.rcs.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

/**
 * The Class GitLogParser.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
class GitLogParser {
	
	// protected static DateTimeFormatter gitLogDateFormat =
	// DateTimeFormat.forPattern("EEE MMM d HH:mm:ss yyyy Z");
	protected static Regex gitLogDateFormatRegex = new Regex(
	                                                         "({EEE}[A-Za-z]{3})\\s+({MMM}[A-Za-z]{3})\\s+({d}\\d{1,2})\\s+({HH}[0-2]\\d):({mm}[0-5]\\d):({ss}[0-5]\\d)\\s+({yyyy}\\d{4})\\s+({Z}[+-]\\d{4})");
	protected static Regex messageRegex          = new Regex(".*$$\\s*git-svn-id:.*");
	
	protected static Regex emailBaseRegex        = new Regex("({email}(" + Regex.emailPattern
	                                                     + ")|([a-zA-Z0-9._%-+]+@([0-9a-fA-F]{4,}-?){5}))");
	
	protected static Regex usernameBaseRegex     = new Regex("(^|[\\s<]+)({username}[a-z0-9]{4,})[\\s>]");
	
	protected static Regex originalIdRegex       = new Regex(".*@({hit}\\d+)\\s+.*");
	
	protected static Person getAuthor(final String line,
	                                  final int lineCounter) {
		String[] authorParts = line.split(":");
		if (authorParts.length != 2) {
			throw new UnrecoverableError("Found error in git log file: line " + lineCounter + ". Abort parsing.");
		}
		String username = null;
		String fullname = null;
		String email = null;
		
		Regex emailRegex = new Regex(emailBaseRegex.getPattern());
		Regex usernameRegex = new Regex(usernameBaseRegex.getPattern());
		
		String authorString = authorParts[1].trim();
		authorParts = authorString.split(" ");
		if (authorParts.length < 1) {
			if (Logger.logWarn()) {
				Logger.warn("Found log entry woth empty author string.");
			}
			return new Person("<unknown>", null, null);
		} else if (authorParts.length == 1) {
			// can be email or username
			List<RegexGroup> find = emailRegex.find(authorString);
			if (find != null) {
				email = authorString.replaceAll("<", "").replaceAll(">", "").trim();
			} else {
				username = authorString.replaceAll("<", "").replaceAll(">", "").trim();
			}
		} else if (authorParts.length == 2) {
			// could be full name or username and email OR username and username
			List<RegexGroup> find = emailRegex.find(authorString);
			if (find != null) {
				emailRegex.find(authorString);
				email = emailRegex.getGroup("email");
				username = emailRegex.removeAll(authorString);
				username = username.replaceAll("<", "").replaceAll(">", "");
				username = username.trim();
			} else {
				List<List<RegexGroup>> findList = usernameRegex.findAll(authorString);
				if (findList != null) {
					if (findList.size() > 1) {
						username = findList.get(0).get(1).getMatch();
					}
				}
				authorString = usernameRegex.removeAll(authorString);
				fullname = authorString.replaceAll("<", "").replaceAll(">", "").trim();
				if (fullname.equals("")) {
					fullname = null;
				}
			}
		} else {
			// ok. let's assume length is three. Could be fullname and email or
			// fullname and username
			if (emailRegex.matches(authorString)) {
				emailRegex.find(authorString);
				email = emailRegex.getGroup("email");
			}
			authorString = emailRegex.removeAll(authorString);
			if (usernameRegex.matches(authorString)) {
				usernameRegex.find(authorString);
				username = usernameRegex.getGroup("username");
			}
			authorString = usernameRegex.removeAll(authorString);
			fullname = authorString.replaceAll("<", "").replaceAll(">", "").trim();
			if (fullname.equals("")) {
				fullname = null;
			}
		}
		//
		// regex.find(authorParts[1].trim());
		// if (regex.getGroup("plain") != null) {
		// username = regex.getGroup("plain").trim();
		// } else if ((regex.getGroup("name") != null) &&
		// (regex.getGroup("lastname") != null)) {
		// fullname = regex.getGroup("name").trim() + " " +
		// regex.getGroup("lastname").trim();
		// } else if (regex.getGroup("name") != null) {
		// username = regex.getGroup("name").trim();
		// }
		// if (regex.getGroup("email") != null) {
		// email = regex.getGroup("email").trim();
		// }
		return new Person(username, fullname, email);
	}
	
	/**
	 * Parses the list of log messages.
	 * 
	 * @param logMessages
	 *            List of strings corresponding to the lines of the log message.
	 *            (not null)
	 * @return the list of parsed log entries representing the logMessages
	 */
	protected static List<LogEntry> parse(@NotNull final List<String> logMessages) {
		List<LogEntry> result = new ArrayList<LogEntry>();
		int lineCounter = 0;
		
		String currentID = null;
		Person author = null;
		String original_id = null;
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
					result.add(new LogEntry(currentID, previous, author, message.toString(), dateTime, original_id));
					currentID = null;
					author = null;
					date = null;
					append = true;
					original_id = null;
					message = new StringBuilder();
				}
				String[] commitParts = line.split(" ");
				if (commitParts.length != 2) {
					throw new UnrecoverableError("Found error in git log file: line " + lineCounter
					        + ". Abort parsing.");
				}
				currentID = commitParts[1].trim();
			} else if (line.startsWith("Author:")) {
				author = getAuthor(line, lineCounter);
			} else if (line.startsWith("AuthorDate:")) {
				String[] authorDateParts = line.split(": ");
				if (authorDateParts.length != 2) {
					throw new UnrecoverableError("Found error in git log file: line " + lineCounter
					        + ". Abort parsing.");
				}
				date = authorDateParts[1].trim();
			} else if (line.startsWith(" ")) {
				if (line.trim().startsWith("git-svn-id")) {
					
					originalIdRegex.find(line);
					if (originalIdRegex.getGroup("hit") != null) {
						original_id = originalIdRegex.getGroup("hit").trim();
					} else {
						if (Logger.logDebug()) {
							Logger.debug("Could not extract original if from line: " + line.trim());
						}
					}
					
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
			result.add(new LogEntry(currentID, previous, author, message.toString(), dateTime, original_id));
		}
		Collections.reverse(result);
		return result;
	}
}

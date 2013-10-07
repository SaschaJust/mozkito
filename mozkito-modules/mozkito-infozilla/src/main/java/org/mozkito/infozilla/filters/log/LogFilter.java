/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.infozilla.filters.log;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.log.Log;
import org.mozkito.infozilla.model.log.LogEntry;
import org.mozkito.infozilla.model.log.LogEntry.Level;

/**
 * The Class LogFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class LogFilter extends Filter<Log> {
	
	/**
	 * The Class PatternHeuristic.
	 */
	public static class PatternHeuristic {
		
		/** The regex. */
		private final Regex              regex;
		
		/** The mapping. */
		private final Map<String, Level> mapping = new HashMap<>();
		
		/**
		 * Instantiates a new pattern heuristic.
		 * 
		 * @param pattern
		 *            the pattern
		 */
		public PatternHeuristic(final String pattern) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				this.regex = new Regex(pattern);
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Extract.
		 * 
		 * @param entry
		 *            the entry
		 * @return the level
		 */
		public boolean adjust(final LogEntry entry) {
			final Match match = this.regex.find(entry.getMessage());
			
			if (match == null) {
				return false;
			}
			
			SANITY: {
				assert match.hasNamedGroup("LEVEL");
			}
			
			final String key = match.getGroup("LEVEL").getMatch();
			Level level = null;
			
			if (this.mapping.containsKey(key)) {
				level = this.mapping.get(key);
			} else {
				try {
					level = Level.valueOf(key);
				} catch (final IllegalArgumentException ignore) {
					// ignore
				}
			}
			
			if (level == null) {
				return false;
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Removing log level from log message.");
			}
			
			final String message = prepareMessage(entry.getMessage().substring(0, match.getFullMatch().start())
			        + entry.getMessage().substring(match.getFullMatch().end()));
			entry.setMessage(message);
			entry.setLevel(level);
			
			return true;
		}
		
		/**
		 * Applies.
		 * 
		 * @param text
		 *            the text
		 * @return true, if successful
		 */
		public boolean applies(final String text) {
			return this.regex.find(text) != null;
		}
		
		/**
		 * Convert.
		 * 
		 * @param match
		 *            the match
		 * @return the level
		 */
		public Level convert(final String match) {
			return this.mapping.get(match);
		}
		
		/**
		 * Gets the regex.
		 * 
		 * @return the regex
		 */
		public Regex getRegex() {
			return this.regex;
		}
		
		/**
		 * Map.
		 * 
		 * @param match
		 *            the match
		 * @param level
		 *            the level
		 * @return the pattern heuristic
		 */
		public PatternHeuristic map(final String match,
		                            final Level level) {
			this.mapping.put(match, level);
			return this;
		}
	}
	
	/**
	 * Prepare message.
	 * 
	 * @param message
	 *            the message
	 * @return the string
	 */
	private static String prepareMessage(final String message) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			
			final Regex regex = new Regex("^\\s*[\\)\\]\\},.:]*");
			return regex.removeAll(message.trim());
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	// @formatter:on
	
	/** The timestamp regex. */
	private final String                    TIMESTAMP_PATTERN  = "(" + "({YEAR}\\d{4})" + "-)?" + "({MONTH}\\d{2})"
	                                                                   + "-" + "({DAY}\\d{2})" + "\\s"
	                                                                   + "({HOUR}[0-2]\\d)" + ":"
	                                                                   + "({MINUTE}[0-5]\\d)" + "("
	                                                                   + ":({SECOND}[0-5]\\d)"
	                                                                   + "([,.]({MILLIS}\\d{3}))?"
	                                                                   + "({OFFSET}[+-]\\d{4})?" + ")?"
	                                                                   + "\\s*?({TEXT}[^\\n\\r]*)";
	
	/** The Constant LEVEL_PATTERN_FULL. */
	private static final PatternHeuristic[] PATTERN_HEURISTICS = new PatternHeuristic[] {
	        new PatternHeuristic("(?<![a-zA-Z])({LEVEL}TRACE|DEBUG|FINE|INFO|WARN|ERROR|FATAL)(?![a-zA-Z])"),
	        new PatternHeuristic("(?<![a-zA-Z])({LEVEL}T|D|I|W|E|F)/?:\\s").map("T", Level.TRACE).map("D", Level.DEBUG)
	                                                                       .map("I", Level.INFO).map("W", Level.WARN)
	                                                                       .map("E", Level.ERROR).map("F", Level.FATAL) };
	
	/** The Constant CLASSNAME_PATTERN. */
	private static final String             CLASSNAME_PATTERN  = "\\[({CLASS}(<[^>]+?> )?([\\w<>\\$]+\\.)*?[\\w<>\\$]+?)(:({LINE}\\d+))?\\]";
	
	/**
	 * Instantiates a new log filter.
	 * 
	 */
	public LogFilter() {
		super(new Color(102, 204, 255));
	}
	
	/**
	 * Adjust levels.
	 * 
	 * @param log
	 *            the log
	 */
	private void adjustLevels(final Log log) {
		// check which heuristics to use and apply
		PatternHeuristic h = null;
		boolean valid = false;
		
		PATTERNS: for (final PatternHeuristic heuristic : LogFilter.PATTERN_HEURISTICS) {
			h = heuristic;
			for (final LogEntry entry : log) {
				if (!heuristic.applies(entry.getMessage())) {
					continue PATTERNS;
				}
			}
			valid = true;
			break PATTERNS;
		}
		
		if (valid) {
			SANITY: {
				assert h != null;
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Adjusting log levels.");
			}
			for (final LogEntry entry : log) {
				h.adjust(entry);
			}
		}
		
	}
	
	/**
	 * Adjust classes.
	 * 
	 * @param log
	 *            the log
	 */
	private void adjustSources(final Log log) {
		final Regex regex = new Regex(LogFilter.CLASSNAME_PATTERN);
		boolean valid = true;
		for (final LogEntry entry : log) {
			if (regex.find(entry.getMessage()) == null) {
				valid = false;
				break;
			}
		}
		
		if (valid) {
			Group group = null;
			Match match = null;
			for (final LogEntry entry : log) {
				match = regex.find(entry.getMessage());
				
				SANITY: {
					assert match != null;
				}
				
				assert match.hasNamedGroup("CLASS");
				
				group = match.getGroup("CLASS");
				entry.setSourceClass(group.getMatch());
				final String message = prepareMessage(entry.getMessage().substring(0, match.getFullMatch().start())
				        + entry.getMessage().substring(match.getFullMatch().end()));
				entry.setMessage(message);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#apply(java.util.List, org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	protected void apply(final List<Log> results,
	                     final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			enhancedReport.setLogs(results);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Block separate.
	 * 
	 * @param entries
	 *            the entries
	 * @param inputText
	 *            the input text
	 * @return the list
	 */
	public List<Log> blockSeparate(final List<LogEntry> entries,
	                               final String inputText) {
		PRECONDTIONS: {
			if (entries == null) {
				throw new NullPointerException();
			}
			if (inputText == null) {
				throw new NullPointerException();
			}
		}
		if (entries.isEmpty()) {
			return new ArrayList<>(0);
		}
		
		try {
			Log log = new Log();
			final List<Log> list = new LinkedList<>();
			
			final Iterator<LogEntry> iterator = entries.iterator();
			
			SANITY: {
				assert !entries.isEmpty();
				assert iterator.hasNext();
			}
			
			LogEntry previous = iterator.next();
			LogEntry current = null;
			boolean consecutive = false;
			
			while (iterator.hasNext()) {
				current = iterator.next();
				
				if (consecutive = consecutive(previous, current)) {
					if (previous.getEndPosition() < (current.getStartPosition() - 1)) {
						// adjust end position and text
						previous.setMessage(previous.getMessage()
						        + inputText.substring(previous.getEndPosition(), current.getStartPosition()));
						previous.setEndPosition(current.getStartPosition() - 1);
					}
					
					log.add(previous);
				} else {
					log.add(previous);
					list.add(log);
					log = new Log();
				}
				
				previous = current;
			}
			
			if (current != null) {
				if (consecutive) {
					log.add(current);
				} else {
					log = new Log();
					log.add(current);
				}
				
				list.add(log);
			}
			
			return list;
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Compute timestamp.
	 * 
	 * @param dayOfMonth
	 *            the day of month
	 * @param monthOfYear
	 *            the month of year
	 * @param year
	 *            the year
	 * @param hourOfDay
	 *            the hour of day
	 * @param minuteOfHour
	 *            the minute of hour
	 * @param secondOfMinute
	 *            the second of minute
	 * @param millisOfSecond
	 *            the millis of second
	 * @param postedOn
	 *            the posted on
	 * @return the date time
	 */
	private DateTime computeTimestamp(final Integer dayOfMonth,
	                                  final Integer monthOfYear,
	                                  final Integer year,
	                                  final Integer hourOfDay,
	                                  final Integer minuteOfHour,
	                                  final Integer secondOfMinute,
	                                  final Integer millisOfSecond,
	                                  final DateTime postedOn) {
		PRECONDITIONS: {
			if (postedOn == null) {
				throw new NullPointerException();
			}
			if (hourOfDay == null) {
				throw new NullPointerException();
			}
		}
		
		DateTime result = null;
		
		try {
			final int theYear = year != null
			                                ? year
			                                : postedOn.getYear();
			final int theMonth = monthOfYear != null
			                                        ? monthOfYear
			                                        : postedOn.getMonthOfYear();
			final int theDay = dayOfMonth != null
			                                     ? dayOfMonth
			                                     : postedOn.getDayOfMonth();
			final int theHour = hourOfDay;
			final int theMinute = minuteOfHour != null
			                                          ? minuteOfHour
			                                          : 0;
			final int theSecond = secondOfMinute != null
			                                            ? secondOfMinute
			                                            : 0;
			result = new DateTime(theYear, theMonth, theDay, theHour, theMinute, theSecond);
			return result;
		} finally {
			assert result != null;
		}
	}
	
	/**
	 * Consecutive.
	 * 
	 * @param previous
	 *            the previous
	 * @param current
	 *            the current
	 * @return true, if successful
	 */
	private boolean consecutive(final LogEntry previous,
	                            final LogEntry current) {
		PRECONDITIONS: {
			if (previous == null) {
				throw new NullPointerException();
			}
			
			if (current == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			final Duration duration = new Duration(current.getTimestamp(), previous.getTimestamp());
			return duration.getMillis() < (24 * 60 * 60 * 1000);
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#runFilter(java.lang.String)
	 */
	@Override
	protected List<Log> runFilter(final String inputText) {
		PRECONDITIONS: {
			if (inputText == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			final DateTime postedOn = new DateTime();
			final List<LogEntry> logEntries = new LinkedList<>();
			
			final MultiMatch multiMatch = new Regex(this.TIMESTAMP_PATTERN).findAll(inputText);
			if (multiMatch != null) {
				for (final Match match : multiMatch) {
					final Integer year = match.hasNamedGroup("YEAR")
					                                                ? Integer.parseInt(match.getGroup("YEAR")
					                                                                        .getMatch())
					                                                : null;
					final Integer month = match.hasNamedGroup("MONTH")
					                                                  ? Integer.parseInt(match.getGroup("MONTH")
					                                                                          .getMatch())
					                                                  : null;
					final Integer day = match.hasNamedGroup("DAY")
					                                              ? Integer.parseInt(match.getGroup("DAY").getMatch())
					                                              : null;
					final Integer hour = match.hasNamedGroup("HOUR")
					                                                ? Integer.parseInt(match.getGroup("HOUR")
					                                                                        .getMatch())
					                                                : null;
					final Integer minute = match.hasNamedGroup("MINUTE")
					                                                    ? Integer.parseInt(match.getGroup("MINUTE")
					                                                                            .getMatch())
					                                                    : null;
					final Integer second = match.hasNamedGroup("SECOND")
					                                                    ? Integer.parseInt(match.getGroup("SECOND")
					                                                                            .getMatch())
					                                                    : null;
					final Integer millis = match.hasNamedGroup("MILLIS")
					                                                    ? Integer.parseInt(match.getGroup("MILLIS")
					                                                                            .getMatch())
					                                                    : null;
					final DateTime timestamp = computeTimestamp(day, month, year, hour, minute, second, millis,
					                                            postedOn);
					
					final String message = prepareMessage(match.getGroup("TEXT").getMatch());
					logEntries.add(new LogEntry(match.getFullMatch().start(), match.getFullMatch().end(), message,
					                            Level.UNKNOWN, timestamp));
				}
			}
			
			if (!logEntries.isEmpty()) {
				final List<Log> list = blockSeparate(logEntries, inputText);
				final List<Log> results = new LinkedList<>();
				
				for (final Log log : list) {
					SANITY: {
						assert log.getEntries() != null;
						assert !log.getEntries().isEmpty();
					}
					final DateTime first = log.getEntries().iterator().next().getTimestamp();
					final DateTime last = log.getEntries().listIterator(logEntries.size()).previous().getTimestamp();
					
					// TODO this should be done in blockseparate
					for (final LogEntry entry : log.getEntries()) {
						if (log.getStartPosition() == null) {
							log.setStartPosition(entry.getStartPosition());
						} else {
							log.setStartPosition(Math.min(entry.getStartPosition(), log.getStartPosition()));
						}
						
						if (log.getEndPosition() == null) {
							log.setEndPosition(entry.getEndPosition());
						} else {
							log.setEndPosition(Math.max(entry.getEndPosition(), log.getEndPosition()));
						}
					}
					
					log.setEnd(first);
					log.setStart(last);
					adjustLevels(log);
					adjustSources(log);
					results.add(log);
				}
				return results;
			} else {
				return new ArrayList<>(0);
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}

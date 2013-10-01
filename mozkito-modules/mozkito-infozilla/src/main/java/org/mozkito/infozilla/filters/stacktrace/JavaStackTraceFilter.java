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

package org.mozkito.infozilla.filters.stacktrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.filters.stacktrace.java.EntryFinder;
import org.mozkito.infozilla.filters.stacktrace.java.EntryFinder.Entry;
import org.mozkito.infozilla.filters.stacktrace.java.MoreFinder;
import org.mozkito.infozilla.filters.stacktrace.java.ReasonFinder;
import org.mozkito.infozilla.filters.stacktrace.java.ReasonFinder.Reason;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.infozilla.model.stacktrace.StacktraceEntry;
import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class JavaStackTraceFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class JavaStackTraceFilter extends StackTraceFilter {



	/** The Constant lineBreaks. */
	public static final Regex  LINE_BREAKS_REGEX       = new Regex("(\\r\\n|\\r|\\n)");

	/** The Constant EXCEPTION_GROUP_NAME. */
	public static final String EXCEPTION_GROUP  = "EXCEPTION_GROUP";
	
	/** The Constant MESSAGE_GROUP_NAME. */
	public static final String MESSAGE_GROUP    = "MESSAGE_GROUP";
	
	/** The Constant CLASSNAME_GROUP. */ 
	public static final String CLASSNAME_GROUP  = "CLASSNAME_GROUP";
	
	/** The Constant METHODNAME_GROUP. */
	public static final String METHODNAME_GROUP = "METHODNAME_GROUP";
	
	/** The Constant FILENAME_GROUP. */
	public static final String FILENAME_GROUP   = "FILENAME_GROUP";
	
	/** The Constant LINENUMBER_GROUP. */
	public static final String LINENUMBER_GROUP = "LINENUMBER_GROUP";
	
	/** The Constant MORE_GROUP. */
	public static final String MORE_GROUP       = "MORE_GROUP";
	
	// @formatter:off
	/** The Constant JAVA_EXCEPTION. */
	public static final String JAVA_EXCEPTION   = "(?<!Caused by:)" // may not start with 'Caused by:'
												+ "(?<!>)" // may not start with '>' to avoid matching: Caused by: <openjpa-2.2.1.1-rexported nonfatal general error> org.apache.openjpa.persistence.PersistenceException: null
												+ "\\s+?" // this is somewhat shady since this requires at least one space before the actual exception. TODO check if this appears to not always happen in the trackers
												+ "({" + JavaStackTraceFilter.EXCEPTION_GROUP + "}(<[^>]+?> )?([\\w<>\\$]+\\.)*?[\\w<>\\$]+?(Error|Exception))" // the actual exception 
												+ "(\\s|$|:\\s" // we can require to either be at the end of the string here, or have the Exception/Error keyword being followed by a whitespace. Or we have a message, then the keyword is followed by a ':\s' and the message string.
														+ "({" 
														+ JavaStackTraceFilter.MESSAGE_GROUP + "}[^\\n\\s]+" 
														+ ")"
												+")";
	
	// @formatter:off	
	/**
	 * The Constant JAVA_REASON.
	 * 
	 * This holds the regular expression for the &quot;at ...&quot lines in a trace.
	 * 
	 * Please refer to the implementation of {@link StackTraceElement#toString()} to see how this is constructed.
	 */
	public static final String JAVA_REASON      = "at\\s+" 
                                    			+ "({" + JavaStackTraceFilter.CLASSNAME_GROUP + "}([\\w<>\\$_\\s]+\\.)*?[\\w<>\\$_\\s]+?)" 
                                    			+ "\\." 
                                    			+ "({" + JavaStackTraceFilter.METHODNAME_GROUP + "}[\\w<>\\$_\\s]+?)" 
                                    			+ "\\s*\\(" 
                                    			+"({" + JavaStackTraceFilter.FILENAME_GROUP + "}[^.]+\\.java|Unknown Source|Native Method)"  
                                    			+ ":?" 
                                    			+ "({" + JavaStackTraceFilter.LINENUMBER_GROUP + "}\\d+)?" 
                                    			+ "\\)";
	// @formatter:on
	
	// @formatter:on
	
	/** The Constant JAVA_MORE. */
	public static final String JAVA_MORE         = "...\\s+({" + JavaStackTraceFilter.MORE_GROUP + "}\\d+)\\s+more";
	
	// @formatter:off
	/** The Constant JAVA_CAUSE. */
	public static final String JAVA_CAUSE       = "Caused by:\\s+"
                                                    + "({" + JavaStackTraceFilter.EXCEPTION_GROUP + "}(<[^>]+?> )?([\\w<>\\$]+\\.)*?[\\w<>\\$]+?(Error|Exception))"
                                                    +"(\\s|$|:\\s" 
                                                    	+ "({"
                                                    	+ JavaStackTraceFilter.MESSAGE_GROUP + "}[^\\n\\s]+)"
                                                    +")";
	// @formatter:on
	
	/**
	 * Instantiates a new java stack trace filter.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	public JavaStackTraceFilter(final EnhancedReport enhancedReport) {
		super(enhancedReport);
		
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#apply(java.util.List, org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	protected void apply(final List<Stacktrace> results,
	                     final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			enhancedReport.setStacktraces(results);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Creates the stacktrace.
	 * 
	 * @param stacktraceEntry
	 *            the stacktrace entry
	 * @param reasons
	 *            the reasons
	 * @param moreEntry
	 *            the more entry
	 * @param offset
	 *            the offset
	 * @return the stacktrace
	 */
	private Stacktrace createStacktrace(final EntryFinder.Entry stacktraceEntry,
	                                    final List<Reason> reasons,
	                                    final MoreFinder.Entry moreEntry,
	                                    final int offset) {
		PRECONDITIONS: {
			if (stacktraceEntry == null) {
				throw new NullPointerException();
			}
			if (reasons == null) {
				throw new NullPointerException();
			}
			if (offset < 0) {
				throw new IllegalArgumentException();
			}
		}
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Using following data to create a stacktrace:");
				Logger.debug("Entry point: " + stacktraceEntry);
				
				for (final Reason reason : reasons) {
					Logger.debug("Reason entry: " + reason);
				}
				
				Logger.debug("More: " + moreEntry);
				
			}
			
			final int startPosition = stacktraceEntry.getStart();
			int endPosition = 0;
			Integer moreValue = null;
			
			if (moreEntry != null) {
				endPosition = moreEntry.getEnd();
				moreValue = moreEntry.getNumber();
			} else if (!reasons.isEmpty()) {
				endPosition = reasons.listIterator(reasons.size()).previous().getEnd();
			} else {
				endPosition = stacktraceEntry.getEnd();
			}
			
			final String exceptionType = stacktraceEntry.getThrowable();
			final String message = stacktraceEntry.getMessage();
			endPosition += offset;
			
			final Stacktrace stacktrace = new Stacktrace(
			                                             startPosition,
			                                             endPosition,
			                                             JavaStackTraceFilter.LINE_BREAKS_REGEX.removeAll(exceptionType),
			                                             JavaStackTraceFilter.LINE_BREAKS_REGEX.removeAll(message),
			                                             moreValue);
			
			for (final Reason reason : reasons) {
				stacktrace.add(new StacktraceEntry(
				                                   JavaStackTraceFilter.LINE_BREAKS_REGEX.removeAll(reason.getElement()
				                                                                                          .getClassName()),
				                                   reason.getElement().getFileName() != null
				                                                                            ? JavaStackTraceFilter.LINE_BREAKS_REGEX.removeAll(reason.getElement()
				                                                                                                                                     .getFileName())
				                                                                            : null,
				                                   JavaStackTraceFilter.LINE_BREAKS_REGEX.removeAll(reason.getElement()
				                                                                                          .getMethodName()),
				                                   reason.getElement().getLineNumber()));
			}
			
			return stacktrace;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the entry windows.
	 * 
	 * @param stacktraceEntries
	 *            the stacktrace entries
	 * @param end
	 *            the end
	 * @return the entry windows
	 */
	private List<Tuple<Integer, Integer>> getEntryWindows(final List<EntryFinder.Entry> stacktraceEntries,
	                                                      final int end) {
		final List<Tuple<Integer, Integer>> list = new ArrayList<>(stacktraceEntries.size());
		
		final Iterator<EntryFinder.Entry> iterator = stacktraceEntries.iterator();
		assert iterator.hasNext();
		EntryFinder.Entry current = iterator.next();
		
		while (iterator.hasNext()) {
			final EntryFinder.Entry next = iterator.next();
			list.add(new Tuple<Integer, Integer>(current.getEnd(), next.getStart()));
			current = next;
		}
		if (current.getEnd() != end) {
			list.add(new Tuple<Integer, Integer>(current.getEnd(), end));
		}
		
		return list;
	}
	
	/**
	 * Parses the exception.
	 * 
	 * @param subString
	 *            the sub string
	 * @param offset
	 *            the offset
	 * @param stacktraceEntry
	 *            the stacktrace entry
	 * @return the filter result
	 */
	private Stacktrace parseException(final String subString,
	                                  final int offset,
	                                  final EntryFinder.Entry stacktraceEntry) {
		// determine end point of actual exception
		// whatever comes first, MORE, next CAUSED BY, end of string
		int exceptionEnd = subString.length();
		
		// determine end point of whole trace, i.e. the last reason entry or following MORE
		
		final EntryFinder causeFinder = new EntryFinder(subString);
		final MoreFinder moreFinder = new MoreFinder(subString);
		final ReasonFinder reasonFinder = new ReasonFinder(subString);
		
		final boolean hasCauses = causeFinder.findCause();
		final boolean hasMores = moreFinder.find();
		final boolean hasReasons = reasonFinder.find();
		
		MoreFinder.Entry validMore = null;
		EntryFinder.Entry currentCause = null;
		
		if (hasCauses) {
			// find first cause
			assert causeFinder.iterator().hasNext();
			
			final Entry causeEntry = causeFinder.getEntries().iterator().next();
			currentCause = causeEntry;
			final int causeStart = causeEntry.getStart();
			exceptionEnd = causeStart - 1;
		}
		
		if (hasMores) {
			final MoreFinder.Entry moreEntry = moreFinder.iterator().next();
			if (moreEntry.getStart() < exceptionEnd) {
				exceptionEnd = moreEntry.getStart() - 1;
				validMore = moreEntry;
			}
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Setting exception (absolute) end at position " + (exceptionEnd + offset));
		}
		
		if (hasReasons) {
			final List<ReasonFinder.Reason> validReasons = new LinkedList<>();
			
			for (final ReasonFinder.Reason reason : reasonFinder) {
				if ((reason.getEnd() <= exceptionEnd)) {
					validReasons.add(reason);
				}
			}
			
			// require at least one reason
			if (validReasons.isEmpty()) {
				return null;
			} else {
				assert !validReasons.isEmpty();
				assert (validMore == null)
				        || (validMore.getStart() >= validReasons.listIterator(validReasons.size()).previous().getEnd());
				
				// fix exception message incase there were line breaks
				// i.e. if the start of the first reason is less than 255 chars away, consider this as message.
				// otherwise we probably should consider this a valid trace anyways
				final Reason firstReason = validReasons.iterator().next();
				if (firstReason.getStart() > 255) {
					if (Logger.logDebug()) {
						Logger.debug("Reason offset to big compared to exception entry. Skipping.");
					}
					return null;
				} else if (firstReason.getStart() > 0) {
					final String newMessage = stacktraceEntry.getMessage()
					        + subString.substring(0, firstReason.getStart() - 1);
					final String newTrimmedMessage = newMessage.trim();
					// the + 1 is the space before the at in the reason regex
					final int shift = (firstReason.getStart() - (newMessage.length() - newTrimmedMessage.length())) + 1;
					final int newEntryEnd = (stacktraceEntry.getEnd() + shift);
					if (Logger.logDebug()) {
						Logger.debug("Shifting stacktrace entry end by " + shift);
					}
					stacktraceEntry.setEnd(newEntryEnd);
					stacktraceEntry.setMessage(newTrimmedMessage);
				}
				
				final Stacktrace stacktrace = createStacktrace(stacktraceEntry, validReasons, validMore, offset);
				if ((currentCause != null) && (exceptionEnd < subString.length())) {
					final int localOffset = currentCause.getEnd();
					final int newOffset = offset + localOffset;
					
					final String newSubstring = subString.substring(localOffset);
					final Stacktrace causeResult = parseException(newSubstring, newOffset, currentCause);
					if (causeResult != null) {
						stacktrace.setCause(causeResult);
						stacktrace.setEndPosition(causeResult.getEndPosition());
					}
				}
				
				return stacktrace;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#runFilter(java.lang.String)
	 */
	@Override
	protected List<Stacktrace> runFilter(final String inputText) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final EntryFinder eFinder = new EntryFinder(inputText);
			final List<Stacktrace> filterResults = new LinkedList<>();
			
			if (eFinder.findException()) {
				
				final List<EntryFinder.Entry> stacktraceEntries = eFinder.getEntries();
				final List<Tuple<Integer, Integer>> entryWindows = getEntryWindows(stacktraceEntries,
				                                                                   inputText.length());
				
				for (final Tuple<Integer, Integer> window : entryWindows) {
					
					final EntryFinder.Entry stacktraceEntry = stacktraceEntries.remove(0);
					final int offset = window.getFirst();
					final int length = window.getSecond() - offset;
					
					if (length == 0) {
						continue;
					}
					
					if (Logger.logDebug()) {
						Logger.debug("[LENGTH:" + inputText.length() + "] Analyzing substring: " + offset + " +"
						        + length);
					}
					
					SANITY: {
						assert offset > 0;
						assert length > 0;
						assert offset < inputText.length();
						assert (offset + length) <= inputText.length();
					}
					
					final String substring = inputText.substring(offset, offset + length);
					
					if (Logger.logDebug()) {
						Logger.debug("Substring: " + substring);
					}
					
					final Stacktrace filterResult = parseException(substring, offset, stacktraceEntry);
					
					if (filterResult != null) {
						filterResults.add(filterResult);
					} else {
						if (Logger.logWarn()) {
							Logger.warn("Couldn't find valid trace.");
						}
					}
				}
				
			}
			
			return filterResults;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

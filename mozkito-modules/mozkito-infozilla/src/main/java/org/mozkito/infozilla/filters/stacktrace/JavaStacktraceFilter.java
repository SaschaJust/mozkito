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
package org.mozkito.infozilla.filters.stacktrace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.infozilla.filters.FilterTextRemover;
import org.mozkito.infozilla.model.stacktrace.JavaStacktrace;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;

/**
 * The Class JavaStacktraceFilter.
 */
public class JavaStacktraceFilter extends StackTraceFilter {
	
	/** The text remover. */
	private FilterTextRemover   textRemover;
	
	// Define a reusable Regular Expression for finding Java Stack Traces
	/** The Constant JAVA_EXCEPTION. */
	@SuppressWarnings ("unused")
	private static final String JAVA_EXCEPTION = "^(([\\w<>\\$_]+\\.)+[\\w<>\\$_]+(Error|Exception){1}(\\s|:))";
	
	/** The Constant JAVA_REASON. */
	@SuppressWarnings ("unused")
	private static final String JAVA_REASON    = "(:?.*?)(at\\s+([\\w<>\\$_]+\\.)+[\\w<>\\$_]+\\s*\\(.+?\\.java(:)?(\\d+)?\\)";
	
	/** The Constant JAVA_TRACE. */
	@SuppressWarnings ("unused")
	private static final String JAVA_TRACE     = "(\\s*?at\\s+([\\w<>\\$_\\s]+\\.)+[\\w<>\\$_\\s]+\\s*\\(.+?\\.java(:)?(\\d+)?\\))*)";
	
	// private static Pattern pattern_stacktrace_java =
	// Pattern.compile(JAVA_STACKTRACE, Pattern.DOTALL
	// | Pattern.MULTILINE);
	// private static Pattern pattern_cause_java = Pattern.compile(JAVA_CAUSE,
	// Pattern.DOTALL | Pattern.MULTILINE);
	
	/**
	 * This method is used to create an {@link Stacktrace} object given its String representation.
	 * 
	 * @param stackTraceMatchGroup
	 *            the String representing a StrackTrace Cause. This usually comes from a RegExHelper matches' group()
	 *            operation!
	 * @return a Stacktrace as represented by the given String
	 */
	private JavaStacktrace createCause(final String stackTraceMatchGroup) {
		// String exception = "";
		// String reason = "";
		// List<String> foundFrames = new ArrayList<String>();
		//
		// // This Pattern has: GROUP 1 GROUP 2 GROUP 3
		// String causeException =
		// "(Caused by:)(.*?(Error|Exception){1})(.*?)(at\\s+([\\w<>\\$_\n\r]+\\.)+[\\w<>\\$_\n\r]+\\s*\\(.+?\\.java(:)?(\\d+)?\\)(\\s*?at\\s+([\\w<>\\$_\\s]+\\.)+[\\w<>\\$_\\s]+\\s*\\(.+?\\.java(:)?(\\d+)?\\))*)";
		// Pattern causeEPattern = Pattern.compile(causeException,
		// Pattern.DOTALL | Pattern.MULTILINE);
		//
		// // Find the Exception of this cause (which is group 2 of
		// causeEPattern)
		// Matcher exceptionMatcher =
		// causeEPattern.matcher(stackTraceMatchGroup);
		// if (exceptionMatcher.find()) {
		// MatchResult matchResult = exceptionMatcher.toMatchResult();
		//
		// exception = matchResult.group(2).trim();
		// reason = matchResult.group(4).trim();
		//
		// // look at the frames
		// String regexFrames =
		// "(^\\s*?at\\s+(([\\w<>\\$_\n\r]+\\.)+[\\w<>\\$_\n\r]+\\s*\\(.*?\\)$))";
		// Pattern patternFrames = Pattern.compile(regexFrames, Pattern.DOTALL |
		// Pattern.MULTILINE);
		//
		// // Find all frames (without the preceeding "at" )
		// for (MatchResult framesMatch : RegExHelper.findMatches(patternFrames,
		// matchResult.group(5))) {
		// foundFrames.add(framesMatch.group(2).replaceAll("[\n\r]", ""));
		// }
		// }
		// // create a Stacktrace
		// JavaStacktrace trace = new JavaStacktrace(exception, reason,
		// foundFrames);
		// trace.setCause(true);
		//
		// return trace;
		return null;
	}
	
	/**
	 * This method is used to create an Stacktrace object given its String representation.
	 * 
	 * @param stackTraceMatchGroup
	 *            the String representing a StrackTrace. This usually comes from a RegExHelper matches' group()
	 *            operation!
	 * @return a Stacktrace as represented by the given String
	 */
	private JavaStacktrace createTrace(final String stackTraceMatchGroup) {
		// String exception = "";
		// String reason = "";
		// List<String> foundFrames = new ArrayList<String>();
		//
		// // This Pattern has: GROUP 1 = exception GROUP 4 = reason GROUP 5 =
		// // frames
		// String traceException =
		// "(([\\w<>\\$_]+\\.)+[\\w<>\\$_]+(Error|Exception){1})(.*?)(at\\s+([\\w<>\\$_\n\r]+\\.)+[\\w<>\\$_\n\r]+\\s*\\(.+?\\.java(:)?(\\d+)?\\)(\\s*?at\\s+([\\w<>\\$_\\s]+\\.)+[\\w<>\\$_\\s]+\\s*\\(.+?\\.java(:)?(\\d+)?\\))*)";
		// Pattern tracePattern = Pattern.compile(traceException, Pattern.DOTALL
		// | Pattern.MULTILINE);
		//
		// // Find the Exception of this cause (which is group 2 of
		// causeEPattern)
		// Matcher exceptionMatcher =
		// tracePattern.matcher(stackTraceMatchGroup);
		// if (exceptionMatcher.find()) {
		// MatchResult matchResult = exceptionMatcher.toMatchResult();
		//
		// exception = matchResult.group(1).trim();
		// reason = matchResult.group(4).trim();
		//
		// // look at the frames
		// String regexFrames =
		// "(^\\s*?at\\s+(([\\w<>\\$_\\s]+\\.)+[\\w<>\\$_\\s]+\\s*\\(.*?\\)$))";
		// Pattern patternFrames = Pattern.compile(regexFrames, Pattern.DOTALL |
		// Pattern.MULTILINE);
		//
		// // Find all frames (without the preceeding "at" )
		// for (MatchResult framesMatch : RegExHelper.findMatches(patternFrames,
		// matchResult.group(5))) {
		// foundFrames.add(framesMatch.group(2).replaceAll("[\n\r]", ""));
		// }
		// }
		// // create a Stacktrace
		// JavaStacktrace trace = new JavaStacktrace(exception, reason,
		// foundFrames);
		// trace.setCause(false);
		//
		// return (trace);
		return null;
	}
	
	/**
	 * Find a list of starting points of x.y.zException or x.y.zError
	 * 
	 * @param s
	 *            The CharSequence to look inside for such starting points
	 * @return an array of possible starting points
	 */
	private int[] findExceptions(final CharSequence s) {
		// List<Integer> exceptionList = new ArrayList<Integer>();
		//
		// // We match against our well known JAVA_EXCEPTION Pattern that
		// denotes a
		// // start of an exception or error
		// Pattern exceptionPattern = Pattern.compile(JAVA_EXCEPTION,
		// Pattern.DOTALL | Pattern.MULTILINE);
		//
		// // For every match we want to add the start of that match to the list
		// of
		// // possible starting points
		// for (MatchResult r : RegExHelper.findMatches(exceptionPattern, s)) {
		//
		// // If there have previously been some starting points
		// if (exceptionList.size() > 0) {
		// // See if this new starting points is at least 20 lines away
		// // from the old one
		// // Sometimes the reason contains another Exception in the first
		// // 5 lines
		// // In this case we would otherwise omit the root exception but
		// // take the exception stated in Reason as root!
		// String newRegion =
		// s.subSequence(exceptionList.get(exceptionList.size() - 1),
		// r.start()).toString();
		// if (newRegion.split("[\n\r]").length >= 20) {
		// exceptionList.add(new Integer(r.start()));
		// }
		// } else {
		// // If there had been no starting points before just add one to
		// // start with
		// exceptionList.add(new Integer(r.start()));
		// }
		// }
		//
		// // If no region is found then go and try the whole text for
		// exhaustive
		// // search
		// if (exceptionList.size() == 0) {
		// exceptionList.add(new Integer(0));
		// }
		//
		// // Convert the List<Integer> to an array
		// int[] results = new int[exceptionList.size()];
		// for (int i = 0; i < exceptionList.size(); i++) {
		// results[i] = exceptionList.get(i).intValue();
		// }
		//
		// return results;
		return null;
	}
	
	/**
	 * Find Stacktraces or Causes that match against our exhaustive patterns.
	 * 
	 * @param s
	 *            The CharSequence to look in for Stack Traces or Causes
	 * @return A list of Matches
	 */
	private List<MatchResult> findStacktraces(final CharSequence s) {
		// List<MatchResult> stacktraces = new ArrayList<MatchResult>();
		//
		// for (MatchResult r : RegExHelper.findMatches(pattern_stacktrace_java,
		// s)) {
		// stacktraces.add(r);
		// }
		//
		// for (MatchResult r : RegExHelper.findMatches(pattern_cause_java, s))
		// {
		// stacktraces.add(r);
		// }
		// return stacktraces;
		return null;
	}
	
	// Auto-generated Message from InfozillaFilter interface
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.filters.stacktrace.StackTraceFilter#getOutputText()
	 */
	@Override
	public String getOutputText() {
		return this.textRemover.doDelete();
	}
	
	/**
	 * Get a List of Stacktraces that are inside the Text s.
	 * 
	 * @param inputSequence
	 *            A CharSequence containing the Text to look for Stack Traces in
	 * @return A List of Stacktraces
	 */
	private List<Stacktrace> getStacktraces(final CharSequence inputSequence) {
		final List<Stacktrace> stackTraces = new ArrayList<Stacktrace>();
		
		// Split the text sequence first by possible exception start otherwise
		// multiline patterns will run FOREVER!
		
		final int[] possibleStart = findExceptions(inputSequence);
		
		for (int i = 0; i < (possibleStart.length - 1); i++) {
			
			final CharSequence region = inputSequence.subSequence(possibleStart[i], possibleStart[i + 1] - 1);
			final List<MatchResult> matches = findStacktraces(region);
			
			for (final MatchResult match : matches) {
				final String matchText = match.group();
				
				// Mark this Stack Trace match for deletion
				final int traceStart = inputSequence.toString().indexOf(matchText);
				final int traceEnd = traceStart + matchText.length() + 1;
				this.textRemover.markForDeletion(traceStart, traceEnd);
				if ((traceStart == 0) && (traceEnd == 0)) {
					if (Logger.logError()) {
						Logger.error("Critical Error in Stacktrace InfozillaFilter! Could not find start and End!");
					}
				}
				// Check if it is a cause or not
				if (matchText.trim().startsWith("Caused by:")) {
					// Create a cause
					final JavaStacktrace cause = createCause(matchText);
					// Add it to the List of Stack Traces
					cause.setTraceStart(traceStart);
					cause.setTraceEnd(traceEnd);
					stackTraces.add(cause);
					
				} else {
					// Create a trace
					final JavaStacktrace trace = createTrace(matchText);
					// Add it to the List of Stack Traces
					trace.setTraceStart(traceStart);
					trace.setTraceEnd(traceEnd);
					stackTraces.add(trace);
				}
			}
		}
		
		// And for the last region, too !!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if (possibleStart.length > 0) {
			
			final CharSequence region = inputSequence.subSequence(possibleStart[possibleStart.length - 1],
			                                                      inputSequence.length());
			final List<MatchResult> matches = findStacktraces(region);
			
			for (final MatchResult match : matches) {
				final String matchText = match.group();
				
				// Mark this Stack Trace match for deletion
				final int traceStart = inputSequence.toString().lastIndexOf(matchText);
				final int traceEnd = traceStart + matchText.length();
				this.textRemover.markForDeletion(traceStart, traceEnd);
				if ((traceStart == 0) && (traceEnd == 0)) {
					if (Logger.logError()) {
						Logger.error("Critical Error in Stacktrace InfozillaFilter! Could not find start and End!");
					}
				}
				
				// Check if it is a cause or not
				if (matchText.trim().startsWith("Caused by:")) {
					// Create a cause
					final JavaStacktrace cause = createCause(matchText);
					// Add it to the List of Stack Traces
					cause.setTraceStart(traceStart);
					cause.setTraceEnd(traceEnd);
					stackTraces.add(cause);
					
				} else {
					// Create a trace
					final JavaStacktrace trace = createTrace(matchText);
					// Add it to the List of Stack Traces
					trace.setTraceStart(traceStart);
					trace.setTraceEnd(traceEnd);
					stackTraces.add(trace);
				}
			}
		}
		
		return stackTraces;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	// Auto-generated Message from InfozillaFilter interface
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.filters.stacktrace.StackTraceFilter#runFilter(java.lang.String)
	 */
	@Override
	public List<Stacktrace> runFilter(final String inputText) {
		// Initialize TextRemover
		this.textRemover = new FilterTextRemover(inputText);
		// Get a Bunch of Stack Traces
		final List<Stacktrace> foundStacktraces = getStacktraces(inputText);
		
		// Do the removal in the textRemover
		// ==> This is already done in getStacktraces when a MatchResult is
		// present!
		
		// And return the found Stack Traces
		return foundStacktraces;
	}
}
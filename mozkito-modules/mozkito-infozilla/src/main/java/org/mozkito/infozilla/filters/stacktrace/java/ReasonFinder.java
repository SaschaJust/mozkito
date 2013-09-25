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

package org.mozkito.infozilla.filters.stacktrace.java;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jregex.REFlags;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter2;

/**
 * The Class ReasonFinder.
 */
public class ReasonFinder implements Iterable<ReasonFinder.Reason> {
	
	/**
	 * The Class Reason.
	 */
	public static class Reason {
		
		/** The start. */
		private final int               start;
		
		/** The end. */
		private final int               end;
		
		/** The element. */
		private final StackTraceElement element;
		
		/**
		 * Instantiates a new reason.
		 * 
		 * @param start
		 *            the start
		 * @param end
		 *            the end
		 * @param element
		 *            the element
		 */
		public Reason(final int start, final int end, final StackTraceElement element) {
			super();
			this.start = start;
			this.end = end;
			this.element = element;
		}
		
		/**
		 * Gets the element.
		 * 
		 * @return the element
		 */
		public final StackTraceElement getElement() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.element;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the end.
		 * 
		 * @return the end
		 */
		public final int getEnd() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.end;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the start.
		 * 
		 * @return the start
		 */
		public final int getStart() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.start;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("Reason [start=");
			builder.append(this.start);
			builder.append(", end=");
			builder.append(this.end);
			builder.append(", element=");
			builder.append(this.element);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
	/** The text. */
	private String                          text;
	
	/** The entries. */
	private final List<ReasonFinder.Reason> entries = new LinkedList<>();
	
	/**
	 * Instantiates a new reason finder.
	 * 
	 * @param text
	 *            the text
	 */
	public ReasonFinder(final String text) {
		PRECONDITIONS: {
			if (text == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			this.text = text;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Find.
	 * 
	 * @return true, if successful
	 */
	public boolean find() {
		final Regex regex = new Regex(JavaStackTraceFilter2.JAVA_REASON, REFlags.DOTALL | REFlags.MULTILINE);
		final MultiMatch multiMatch = regex.findAll(this.text);
		if (multiMatch != null) {
			for (final Match match : multiMatch) {
				final String className = JavaStackTraceFilter2.LINE_BREAKS_REGEX.removeAll(match.getGroup(JavaStackTraceFilter2.CLASSNAME_GROUP)
				                                                                        .getMatch());
				final String methodName = JavaStackTraceFilter2.LINE_BREAKS_REGEX.removeAll(match.getGroup(JavaStackTraceFilter2.METHODNAME_GROUP)
				                                                                         .getMatch());
				String fileName = match.getGroup(JavaStackTraceFilter2.FILENAME_GROUP).getMatch();
				if (fileName != null) {
					fileName = JavaStackTraceFilter2.LINE_BREAKS_REGEX.removeAll(fileName);
				}
				
				Integer lineNumber = null;
				final Group lineGroup = match.getGroup(JavaStackTraceFilter2.LINENUMBER_GROUP);
				String lineNumberStr = lineGroup != null
				                                        ? lineGroup.getMatch()
				                                        : null;
				if (lineNumberStr != null) {
					lineNumberStr = JavaStackTraceFilter2.LINE_BREAKS_REGEX.removeAll(lineNumberStr).trim();
					lineNumber = Integer.parseInt(lineNumberStr);
				} else if ("Native Method".equals(fileName)) {
					// see StackTraceElement JavaDoc:
					// "A value of -2 indicates that the method containing the execution point is a native method"
					lineNumber = -2;
				} else if ("Unknown Source".equals(fileName)) {
					// again, see StackTraceElement JavaDoc
					// "neither the file name nor the line  number are available"
					lineNumber = -1;
					fileName = null;
				}
				
				// if (Logger.logInfo()) {
				// Logger.info("Creating new StackTraceElement with:");
				// Logger.info("classname: '" + className + "'");
				// Logger.info("methodname: '" + methodName + "'");
				// Logger.info("filename: '" + fileName + "'");
				// Logger.info("linenumber: '" + lineNumberStr + "'");
				// }
				
				SANITY: {
					assert match != null;
					assert match.getFullMatch() != null;
					assert this.entries != null;
					assert lineNumber != null;
				}
				
				this.entries.add(new ReasonFinder.Reason(match.getFullMatch().start(), match.getFullMatch().end(),
				                                         new StackTraceElement(className, methodName, fileName,
				                                                               lineNumber)));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	public final List<ReasonFinder.Reason> getEntries() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.entries;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ReasonFinder.Reason> iterator() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.entries.iterator();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}

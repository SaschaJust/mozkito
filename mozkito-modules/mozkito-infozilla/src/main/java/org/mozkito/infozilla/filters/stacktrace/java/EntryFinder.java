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
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter2;

/**
 * The Class EntryFinder.
 */
public class EntryFinder implements Iterable<EntryFinder.Entry> {
	
	/**
	 * The Class Entry.
	 */
	public static class Entry {
		
		/** The start. */
		private final int    start;
		
		/** The end. */
		private int          end;
		
		/** The throwable. */
		private final String throwable;
		
		/** The message. */
		private String       message;
		
		/**
		 * Instantiates a new entry.
		 * 
		 * @param start
		 *            the start
		 * @param end
		 *            the end
		 * @param throwable
		 *            the throwable
		 * @param message
		 *            the message
		 */
		public Entry(final int start, final int end, final String throwable, final String message) {
			super();
			this.start = start;
			this.end = end;
			this.throwable = throwable;
			this.message = message;
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
		 * Gets the message.
		 * 
		 * @return the message
		 */
		public final String getMessage() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.message;
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
		 * Gets the throwable.
		 * 
		 * @return the throwable
		 */
		public final String getThrowable() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.throwable;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * @param end
		 *            the end to set
		 */
		public final void setEnd(final int end) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				this.end = end;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * @param message
		 *            the message to set
		 */
		public final void setMessage(final String message) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				this.message = message;
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
			builder.append("Entry [start=");
			builder.append(this.start);
			builder.append(", end=");
			builder.append(this.end);
			builder.append(", throwable=");
			builder.append(this.throwable);
			builder.append(", message=");
			builder.append(this.message);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
	/** The entries. */
	private final List<EntryFinder.Entry> entries = new LinkedList<>();
	
	/** The text. */
	private String                        text;
	
	/**
	 * Instantiates a new entry finder.
	 * 
	 * @param text
	 *            the text
	 */
	public EntryFinder(final String text) {
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
	 * @param regex
	 *            the regex
	 * @return true, if successful
	 */
	private boolean find(final Regex regex) {
		final MultiMatch multiMatch = regex.findAll(this.text);
		
		if (multiMatch != null) {
			for (final Match match : multiMatch) {
				// due to the composition of the regex, we need to compute leading whitespaces and remove them
				final Regex leadingWhitespaces = new Regex("^(\\r\\n|\\r|\\n|\\s)+");
				final String entryText = match.getFullMatch().getMatch();
				final String removed = leadingWhitespaces.removeAll(entryText);
				final int leadingSpaces = entryText.length() - removed.length();
				
				if (Logger.logDebug()) {
					Logger.debug("---> " + (match.getFullMatch().start() + leadingSpaces) + " up to "
					        + match.getFullMatch().end() + " " + match.getFullMatch().getMatch());
				}
				
				this.entries.add(new Entry(
				                           match.getFullMatch().start() + leadingSpaces,
				                           match.getFullMatch().end(),
				                           match.getGroup(JavaStackTraceFilter2.EXCEPTION_GROUP).getMatch(),
				                           match.getGroup(JavaStackTraceFilter2.MESSAGE_GROUP) != null
				                                                                                     ? match.getGroup(JavaStackTraceFilter2.MESSAGE_GROUP)
				                                                                                            .getMatch()
				                                                                                     : ""));
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * Find cause.
	 * 
	 * @return true, if successful
	 */
	public boolean findCause() {
		final Regex causeRegex = new Regex(JavaStackTraceFilter2.JAVA_CAUSE, REFlags.MULTILINE | REFlags.DOTALL);
		return find(causeRegex);
	}
	
	/**
	 * Find.
	 * 
	 * @return true, if successful
	 */
	public boolean findException() {
		final Regex exceptionRegex = new Regex(JavaStackTraceFilter2.JAVA_EXCEPTION, REFlags.MULTILINE | REFlags.DOTALL);
		
		return find(exceptionRegex);
	}
	
	/**
	 * Gets the entries.
	 * 
	 * @return the entries
	 */
	public final List<EntryFinder.Entry> getEntries() {
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
	public Iterator<EntryFinder.Entry> iterator() {
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

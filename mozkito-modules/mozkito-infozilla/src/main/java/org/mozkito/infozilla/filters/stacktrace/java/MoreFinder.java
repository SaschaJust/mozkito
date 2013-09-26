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
import java.util.regex.Pattern;

import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter;

/**
 * The Class MoreFinder.
 */
public class MoreFinder implements Iterable<MoreFinder.Entry> {
	
	/**
	 * The Class Entry.
	 */
	public static class Entry {
		
		/** The start. */
		private final int start;
		
		/** The end. */
		private final int end;
		
		/** The number. */
		private final int number;
		
		/**
		 * Instantiates a new entry.
		 * 
		 * @param start
		 *            the start
		 * @param end
		 *            the end
		 * @param number
		 *            the number
		 */
		public Entry(final int start, final int end, final int number) {
			super();
			this.start = start;
			this.end = end;
			this.number = number;
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
		 * Gets the number.
		 * 
		 * @return the number
		 */
		public final int getNumber() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.number;
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
		
	}
	
	/** The text. */
	private String                       text;
	
	/** The entries. */
	private final List<MoreFinder.Entry> entries = new LinkedList<>();
	
	/**
	 * Instantiates a new more finder.
	 * 
	 * @param text
	 *            the text
	 */
	public MoreFinder(final String text) {
		PRECONDITIONS: {
			// none
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
		final Regex regex = new Regex(JavaStackTraceFilter.JAVA_MORE, Pattern.MULTILINE | Pattern.DOTALL);
		final MultiMatch multiMatch = regex.findAll(this.text);
		if (multiMatch != null) {
			for (final Match match : multiMatch) {
				this.entries.add(new Entry(match.getFullMatch().start(), match.getFullMatch().end(),
				                           Integer.parseInt(match.getGroup(JavaStackTraceFilter.MORE_GROUP).getMatch())));
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return the entries
	 */
	public final List<MoreFinder.Entry> getEntries() {
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
	public Iterator<Entry> iterator() {
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

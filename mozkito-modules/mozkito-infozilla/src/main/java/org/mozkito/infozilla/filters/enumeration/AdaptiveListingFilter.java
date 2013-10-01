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

package org.mozkito.infozilla.filters.enumeration;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.itemization.Listing;
import org.mozkito.infozilla.model.itemization.ListingEntry;

/**
 * The Class AdaptiveListingFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class AdaptiveListingFilter extends Filter<Listing> {
	
	/**
	 * The Class EntryFinder.
	 */
	private static class EntryFinder {
		
		/** The type. */
		private final Listing.Type type;
		
		/** The pattern. */
		private final String       pattern;
		
		/**
		 * Instantiates a new entry finder.
		 * 
		 * @param pattern
		 *            the pattern
		 * @param type
		 *            the type
		 */
		public EntryFinder(final String pattern, final Listing.Type type) {
			this.pattern = pattern;
			this.type = type;
		}
		
		/**
		 * Gets the entry points.
		 * 
		 * @param inputText
		 *            the input text
		 * @return the entry points
		 */
		public List<List<ListingEntry>> getEntryPoints(final String inputText) {
			List<List<ListingEntry>> list = new LinkedList<>();
			final List<ListingEntry> currentList = new LinkedList<>();
			
			final Regex regex = new Regex(this.pattern);
			final MultiMatch multiMatch = regex.findAll(inputText);
			
			ListingEntry entry = null;
			ListingEntry previous = null;
			
			if (multiMatch != null) {
				for (final Match match : multiMatch) {
					entry = new ListingEntry(match.getGroup("BULLET").getMatch(),
					                         match.hasNamedGroup("STOP")
					                                                    ? match.getGroup("STOP").getMatch()
					                                                    : null, this.type,
					                         match.getFullMatch().start(), match.getFullMatch().end(), null);
					if (previous != null) {
						if (entry.getStop() == null) {
							if (previous.getStop() == null) {
								SANITY: {
									assert Listing.Type.ITEMIZATION.equals(previous.getType());
									assert Listing.Type.ITEMIZATION.equals(entry.getType());
								}
								
								if (!previous.getType().equals(entry.getType())) {
									list.add(currentList);
									list = new LinkedList<>();
								}
							} else {
								list.add(currentList);
								list = new LinkedList<>();
							}
						} else {
							if ((previous.getStop() == null) || !entry.getStop().equals(previous.getStop())) {
								list.add(currentList);
								list = new LinkedList<>();
							}
						}
					}
					currentList.add(entry);
					previous = entry;
				}
			}
			
			list.add(currentList);
			
			return list;
		}
	}
	
	/** The Constant ALPHA_PATTERN. */
	private static final String ALPHA_PATTERN       = "(?<!\\w)({BULLET}[a-zA-Z])({STOP}(\\.?[\\)\\]]|:))\\s*";
	
	/** The Constant ROMAN_PATTERN. */
	private static final String ROMAN_PATTERN       = "(?<!\\w)({BULLET}[ivx])({STOP}(\\.?[\\)\\]]|:))\\s*";
	
	/** The Constant NUMERIC_PATTERN. */
	private static final String NUMERIC_PATTERN     = "(?<!\\w)({BULLET}[1-9][0-9]*)({STOP}(\\.?[\\)\\]]|:))\\s*";
	
	/** The Constant ITEMIZATION_PATTERN. */
	private static final String ITEMIZATION_PATTERN = "(?<!\\w)({BULLET}(-+|\\*+|\\++))\\s+";
	
	/**
	 * @param enhancedReport
	 */
	public AdaptiveListingFilter(final EnhancedReport enhancedReport) {
		super(enhancedReport);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#apply(java.util.List, org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	protected void apply(final List<Listing> results,
	                     final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			enhancedReport.setListings(results);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the entry points.
	 * 
	 * @param inputText
	 *            the input text
	 * @return the entry points
	 */
	private List<List<ListingEntry>> getEntryPoints(final String inputText) {
		final List<List<ListingEntry>> list = new LinkedList<>();
		final EntryFinder[] finders = new EntryFinder[] {
		        new EntryFinder(AdaptiveListingFilter.ALPHA_PATTERN, Listing.Type.ENUMERATION),
		        new EntryFinder(AdaptiveListingFilter.ROMAN_PATTERN, Listing.Type.ENUMERATION),
		        new EntryFinder(AdaptiveListingFilter.NUMERIC_PATTERN, Listing.Type.ENUMERATION),
		        new EntryFinder(AdaptiveListingFilter.ITEMIZATION_PATTERN, Listing.Type.ITEMIZATION) };
		
		for (final EntryFinder finder : finders) {
			final List<List<ListingEntry>> alphaEntryPoints = finder.getEntryPoints(inputText);
			
			for (final List<ListingEntry> currentList : alphaEntryPoints) {
				if (currentList.size() > 1) {
					list.add(currentList);
				} else if (!currentList.isEmpty()) {
					if (Logger.logInfo()) {
						Logger.info("Dropping entry: " + currentList.iterator().next());
					}
				}
			}
		}
		
		return list;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#runFilter(java.lang.String)
	 */
	@Override
	protected List<Listing> runFilter(final String inputText) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final List<Listing> results = new LinkedList<>();
			final List<List<ListingEntry>> entryPoints = getEntryPoints(inputText);
			
			for (final List<ListingEntry> list : entryPoints) {
				SANITY: {
					assert !list.isEmpty();
				}
				final ListingEntry first = list.iterator().next();
				final ListingEntry last = list.listIterator(list.size()).previous();
				
				final Listing listing = new Listing(first.getType(), first.getStartPosition(), last.getEndPosition());
				listing.addAll(list);
				results.add(listing);
			}
			
			return results;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

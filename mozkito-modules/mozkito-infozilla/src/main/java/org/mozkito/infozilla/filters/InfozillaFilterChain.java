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
package org.mozkito.infozilla.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import org.mozkito.infozilla.elements.FilterResult;
import org.mozkito.infozilla.filters.enumeration.AdaptiveListingFilter;
import org.mozkito.infozilla.filters.log.LogFilter;
import org.mozkito.infozilla.filters.patch.UnifiedDiffPatchFilter;
import org.mozkito.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class InfozillaFilterChain.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InfozillaFilterChain {
	
	/**
	 * The Class Region.
	 */
	public static class Region implements Comparable<Region> {
		
		/** The from. */
		private final int from;
		
		/** The to. */
		private final int to;
		
		/**
		 * Instantiates a new region.
		 * 
		 * @param from
		 *            the from
		 * @param to
		 *            the to
		 */
		public Region(final int from, final int to) {
			PRECONDITIONS: {
				
				if (from < 0) {
					throw new IllegalArgumentException();
				}
				if (to <= 0) {
					throw new IllegalArgumentException();
				}
			}
			this.from = from;
			this.to = to;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final Region o) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.from - o.from;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Region other = (Region) obj;
			if (this.from != other.from) {
				return false;
			}
			if (this.to != other.to) {
				return false;
			}
			return true;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + this.from;
			result = (prime * result) + this.to;
			return result;
		}
		
		/**
		 * Merge.
		 * 
		 * @param other
		 *            the other
		 * @return the region
		 */
		public Region merge(final Region other) {
			PRECONDITIONS: {
				if (!overlaps(other)) {
					throw new IllegalArgumentException();
				}
			}
			
			try {
				if (equals(other)) {
					return this;
				} else {
					if (compareTo(other) < 0) {
						return new Region(this.from, other.to);
					} else {
						return new Region(this.to, other.from);
					}
				}
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Overlaps.
		 * 
		 * @param other
		 *            the other
		 * @return true, if successful
		 */
		public boolean overlaps(final Region other) {
			if (compareTo(other) < 0) {
				return this.to >= other.from;
			} else {
				return this.from <= other.to;
			}
		}
		
	}
	
	/**
	 * The Class TextRemover.
	 */
	public static class TextRemover {
		
		/** The text. */
		private final String     text;
		
		/** The regions. */
		public SortedSet<Region> regions = new TreeSet<>();
		
		/**
		 * Instantiates a new text remover.
		 * 
		 * @param text
		 *            the text
		 */
		public TextRemover(final String text) {
			PRECONDITIONS: {
				if (text == null) {
					throw new NullPointerException();
				}
			}
			
			this.text = text;
		}
		
		/**
		 * Block.
		 * 
		 * @param from
		 *            the from
		 * @param to
		 *            the to
		 * @return true, if successful
		 */
		public boolean block(final int from,
		                     final int to) {
			PRECONDITIONS: {
				assert this.text != null;
				
				if (from < 0) {
					throw new IllegalArgumentException();
				}
				if (to <= 0) {
					throw new IllegalArgumentException();
				}
				if (to >= this.text.length()) {
					;
				}
			}
			
			try {
				final Region newRegion = new Region(from, to);
				Region replaceRegion = null;
				
				REGIONS: for (final Region region : this.regions) {
					if (newRegion.overlaps(region)) {
						replaceRegion = region;
						break REGIONS;
					}
				}
				
				if (replaceRegion != null) {
					this.regions.remove(replaceRegion);
				}
				
				return this.regions.add(newRegion);
			} finally {
				// none
			}
		}
		
		/**
		 * Prepare.
		 * 
		 * @return the string
		 */
		public String prepare() {
			SANITY: {
				assert this.text != null;
				assert this.regions != null;
			}
			
			final StringBuilder builder = new StringBuilder();
			int position = 0;
			Region current = null;
			
			for (final Region region : this.regions) {
				builder.append(this.text.substring(position, region.from));
				for (int i = region.from; i < (region.to - FileUtils.lineSeparator.length()); ++i) {
					builder.append(' ');
				}
				builder.append(FileUtils.lineSeparator);
				position = region.to;
				current = region;
			}
			
			if (current != null) {
				builder.append(this.text.substring(current.to));
			}
			
			return builder.toString();
		}
	}
	
	/**
	 * Strip html.
	 * 
	 * @param string
	 *            the string
	 * @return the string
	 */
	private static String stripHTML(final String string) {
		String input = Jsoup.clean(string, "", Whitelist.none().addTags("br", "p"),
		                           new OutputSettings().prettyPrint(true));;
		input = Jsoup.clean(input, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
		return StringEscapeUtils.unescapeHtml(input);
	}
	
	/** The enhanced report. */
	private EnhancedReport                  enhancedReport;
	
	/** The report. */
	private Report                          report;
	
	/** The text map. */
	private final Map<Integer, TextRemover> textMap = new HashMap<>();
	
	/**
	 * Instantiates a new infozilla filter chain.
	 * 
	 * @param report
	 *            the report
	 */
	public InfozillaFilterChain(final Report report) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.report = report;
			this.enhancedReport = new EnhancedReport(report.getId());
			
			this.textMap.put(-1, new TextRemover(stripHTML(report.getDescription())));
			for (final Comment comment : report.getComments()) {
				this.textMap.put(comment.getId(), new TextRemover(stripHTML(comment.getMessage())));
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Parses the report.
	 * 
	 * @return the enhanced report
	 */
	public EnhancedReport parse() {
		SANITY: {
			assert this.enhancedReport != null;
			assert this.report != null;
			assert this.textMap != null;
		}
		
		STACKTRACES: {
			performFiltering(new JavaStackTraceFilter());
		}
		
		PATCHES: {
			performFiltering(new UnifiedDiffPatchFilter());
		}
		
		SOURCECODE: {
			performFiltering(new JavaSourceCodeFilter());
		}
		
		LOGS: {
			performFiltering(new LogFilter());
		}
		
		LISTINGS: {
			performFiltering(new AdaptiveListingFilter());
		}
		
		// set new description / comments
		this.enhancedReport.setFilteredDescription(this.textMap.get(-1).prepare());
		final List<Comment> filteredComments = new ArrayList<>(this.report.getComments().size());
		
		for (final Comment comment : this.report.getComments()) {
			filteredComments.add(new Comment(comment.getId(), comment.getAuthor(), comment.getTimestamp(),
			                                 this.textMap.get(comment.getId()).prepare()));
		}
		this.enhancedReport.setFilteredComments(filteredComments);
		
		return this.enhancedReport;
	}
	
	/**
	 * Perform filtering.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param filter
	 *            the filter
	 */
	private <T> void performFiltering(final InfozillaFilter<T> filter) {
		String description = null;
		String message = null;
		
		final List<T> results = new LinkedList<>();
		
		description = this.textMap.get(-1).prepare();
		
		for (final FilterResult<T> result : filter.runFilter(description)) {
			results.add(result.third);
			this.textMap.get(-1).block(result.first, result.second);
		}
		
		for (final Comment comment : this.report.getComments()) {
			message = this.textMap.get(comment.getId()).prepare();
			
			for (final FilterResult<T> result : filter.runFilter(message)) {
				results.add(result.third);
				this.textMap.get(comment.getId()).block(result.first, result.second);
			}
		}
		
		filter.apply(results, this.enhancedReport);
	}
	
}

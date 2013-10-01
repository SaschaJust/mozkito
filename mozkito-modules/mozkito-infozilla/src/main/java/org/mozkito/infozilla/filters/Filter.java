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

package org.mozkito.infozilla.filters;

import java.util.List;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.FilterStatistics;
import org.mozkito.infozilla.elements.FilterStatisticsImpl;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.persons.model.Person;

/**
 * This interface describes the method interface for every infoZilla InfozillaFilter.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just
 */
public abstract class Filter<T extends Inlineable> {
	
	/** The enhanced report. */
	private EnhancedReport              enhancedReport;
	
	/** The statistics. */
	private static FilterStatisticsImpl statistics = new FilterStatisticsImpl();
	
	/**
	 * Instantiates a new infozilla filter.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	public Filter(final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.enhancedReport = enhancedReport;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Apply.
	 * 
	 * @param results
	 *            the results
	 * @param enhancedReport
	 *            the enhanced report
	 */
	protected abstract void apply(List<T> results,
	                              EnhancedReport enhancedReport);
	
	/**
	 * Filter.
	 * 
	 * @param text
	 *            the text
	 * @param author
	 *            the author
	 * @param timestamp
	 *            the timestamp
	 * @return the list
	 */
	public List<T> filter(final String text,
	                      final Person author,
	                      final DateTime timestamp) {
		
		final List<T> results = runFilter(text);
		
		for (final T entity : results) {
			entity.setPostedBy(author);
			entity.setPostedOn(timestamp);
		}
		
		if (!results.isEmpty()) {
			final EnhancedReport newEnhancedReport = EnhancedReport.empty();
			apply(results, newEnhancedReport);
			EnhancedReport.merge(this.enhancedReport, newEnhancedReport);
		}
		
		// apply some stats here
		return results;
	}
	
	/**
	 * Gets the stats.
	 * 
	 * @return the stats
	 */
	public FilterStatistics getStats() {
		return Filter.statistics;
	}
	
	/**
	 * Run filter.
	 * 
	 * @param inputText
	 *            the input text
	 * @return the list
	 */
	protected abstract List<T> runFilter(String inputText);
}

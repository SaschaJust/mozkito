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

import java.util.LinkedList;
import java.util.List;

import org.mozkito.infozilla.filters.stacktrace.JavaStacktraceFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.Report;

/**
 * The Class InfozillaFilterChain.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class InfozillaFilterChain {
	
	/**
	 * Parses the.
	 * 
	 * @param report
	 *            the report
	 * @return the enhanced report
	 */
	public static EnhancedReport parse(final Report report) {
		final EnhancedReport enhancedReport = new EnhancedReport(report.getId());
		
		final JavaStacktraceFilter filter = new JavaStacktraceFilter();
		final List<Stacktrace> stacktraces = new LinkedList<>();
		for (final Comment comment : report.getComments()) {
			stacktraces.addAll(filter.runFilter(comment.getMessage()));
		}
		enhancedReport.setStacktraces(stacktraces);
		return enhancedReport;
	}
	
	/**
	 * Adds the filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	public void addFilter(final InfozillaFilter filter) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Gets the enumerations.
	 * 
	 * @return the enumerations
	 */
	public List<?> getEnumerations() {
		return null;
	}
	
	/**
	 * Gets the input text.
	 * 
	 * @return the inputText
	 */
	public String getInputText() {
		return null;
	}
	
	/**
	 * Gets the output text.
	 * 
	 * @return the outputText
	 */
	public String getOutputText() {
		return null;
	}
	
	/**
	 * Gets the patches.
	 * 
	 * @return the patches
	 */
	public List<?> getPatches() {
		return null;
	}
	
	/**
	 * Gets the regions.
	 * 
	 * @return the regions
	 */
	public List<?> getRegions() {
		return null;
	}
	
	/**
	 * Gets the traces.
	 * 
	 * @return the traces
	 */
	public List<?> getTraces() {
		return null;
	}
	
	/**
	 * Sets the input text.
	 * 
	 * @param inputText
	 *            the inputText to set
	 */
	public void setInputText(final String inputText) {
		// stub
	}
}

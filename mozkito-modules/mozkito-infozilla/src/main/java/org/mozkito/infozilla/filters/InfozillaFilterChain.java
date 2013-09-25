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

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import org.mozkito.infozilla.elements.FilterResult;
import org.mozkito.infozilla.filters.log.LogFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.log.Log;
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
		
		// final JavaStackTraceFilter filter = new JavaStackTraceFilter();
		// final List<Stacktrace> stacktraces = new LinkedList<>();
		// final String description = stripHTML(report.getDescription());
		// for (final FilterResult<Stacktrace> result : filter.runFilter(description)) {
		// stacktraces.add(result.third);
		// }
		//
		// for (final Comment comment : report.getComments()) {
		// final String message = stripHTML(comment.getMessage());
		// for (final FilterResult<Stacktrace> result : filter.runFilter(message)) {
		// stacktraces.add(result.third);
		// }
		// }
		
		// enhancedReport.setStacktraces(stacktraces);
		// final List<Patch> patches = new LinkedList<>();
		
		// final UnifiedDiffPatchFilter patchFilter = new UnifiedDiffPatchFilter();
		
		// final String description = stripHTML(report.getDescription());
		// for (final FilterResult<Patch> result : patchFilter.runFilter(description)) {
		// patches.add(result.third);
		// }
		//
		// for (final Comment comment : report.getComments()) {
		// final String message = stripHTML(comment.getMessage());
		// for (final FilterResult<Patch> result : patchFilter.runFilter(message)) {
		// patches.add(result.third);
		// }
		// }
		
		// enhancedReport.setPatches(patches);
		
		// final List<SourceCode> codeFragments = new LinkedList<>();
		// final JavaSourceCodeFilter sourceCodeFilter = new JavaSourceCodeFilter();
		//
		// final String description = stripHTML(report.getDescription());
		// for (final FilterResult<SourceCode> result : sourceCodeFilter.runFilter(description)) {
		// codeFragments.add(result.third);
		// }
		//
		// for (final Comment comment : report.getComments()) {
		// final String message = stripHTML(comment.getMessage());
		// for (final FilterResult<SourceCode> result : sourceCodeFilter.runFilter(message)) {
		// codeFragments.add(result.third);
		// }
		// }
		//
		// for (final SourceCode code : codeFragments) {
		// Logger.always(code.toString());
		// }
		
		final List<Log> logs = new LinkedList<>();
		final LogFilter logFilter = new LogFilter();
		
		final String description = stripHTML(report.getDescription());
		for (final FilterResult<Log> result : logFilter.runFilter(description)) {
			logs.add(result.third);
		}
		
		for (final Comment comment : report.getComments()) {
			final String message = stripHTML(comment.getMessage());
			for (final FilterResult<Log> result : logFilter.runFilter(message)) {
				logs.add(result.third);
			}
		}
		
		for (final Log log : logs) {
			Logger.always(log.toString());
		}
		
		return enhancedReport;
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
	
	/**
	 * Adds the filter.
	 * 
	 * @param filter
	 *            the filter
	 */
	public void addFilter(final InfozillaFilter<?> filter) {
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

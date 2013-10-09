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

package org.mozkito.infozilla.managers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.mozkito.infozilla.SimpleEditor;
import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.filters.enumeration.AdaptiveListingFilter;
import org.mozkito.infozilla.filters.enumeration.EnumerationFilter;
import org.mozkito.infozilla.filters.link.LinkFilter;
import org.mozkito.infozilla.filters.log.LogFilter;
import org.mozkito.infozilla.filters.patch.UnifiedDiffPatchFilter;
import org.mozkito.infozilla.filters.sourcecode.JavaSourceCodeFilter;
import org.mozkito.infozilla.filters.stacktrace.JavaStackTraceFilter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Report;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class Manager implements IManager {
	
	/** The enhanced report. */
	protected EnhancedReport                               enhancedReport;
	/** The report. */
	protected Report                                       report;
	/** The editor. */
	protected SimpleEditor                                 editor;
	/** The color map. */
	protected final Map<Class<? extends Filter<?>>, Color> colorMap = new HashMap<Class<? extends Filter<?>>, Color>() {
		                                                                
		                                                                private static final long serialVersionUID = 1L;
		                                                                
		                                                                {
			                                                                put(JavaStackTraceFilter.class,
			                                                                    Color.YELLOW);
			                                                                put(JavaSourceCodeFilter.class, Color.pink);
			                                                                put(LinkFilter.class, Color.orange);
			                                                                put(LogFilter.class, Color.cyan);
			                                                                put(UnifiedDiffPatchFilter.class,
			                                                                    Color.GREEN);
			                                                                put(AdaptiveListingFilter.class, Color.red);
			                                                                put(EnumerationFilter.class, Color.red);
		                                                                }
	                                                                };
	protected CountDownLatch                               latch    = null;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.managers.IManager#getEnhancedReport()
	 */
	@Override
	public EnhancedReport getEnhancedReport() {
		return this.enhancedReport;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.managers.IManager#getReport()
	 */
	@Override
	public Report getReport() {
		return this.report;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.managers.IManager#parse()
	 */
	@Override
	public abstract EnhancedReport parse();
}

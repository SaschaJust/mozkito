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

package org.mozkito.infozilla.chain;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.ISettings;

import org.mozkito.infozilla.filters.InfozillaFilterChain;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Report;

/**
 * The Class StacktraceFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class StacktraceFilter extends Transformer<Report, EnhancedReport> {
	
	/**
	 * Instantiates a new stacktrace filter.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param filterChain
	 *            the filter chain
	 */
	public StacktraceFilter(final Group threadGroup, final ISettings settings, final InfozillaFilterChain filterChain) {
		super(threadGroup, settings, false);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			
			new ProcessHook<Report, EnhancedReport>(this) {
				
				/**
				 * {@inheritDoc}
				 * 
				 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
				 */
				@Override
				public void process() {
					PRECONDITIONS: {
						// none
					}
					
					try {
						final Report data = getInputData();
						
						EnhancedReport enhancedReport = null;
						
						if (data != null) {
							final InfozillaFilterChain chain = new InfozillaFilterChain(data);
							enhancedReport = chain.parse();
						}
						
						provideOutputData(enhancedReport);
					} finally {
						POSTCONDITIONS: {
							// none
						}
					}
				}
			};
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

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

import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Report;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ReportTransformer extends Transformer<Report, EnhancedReport> {
	
	/**
	 * Instantiates a new report transformer.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public ReportTransformer(final Group threadGroup, final ISettings settings) {
		super(threadGroup, settings, true);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			new ProcessHook<Report, EnhancedReport>(this) {
				
				@Override
				public void process() {
					PRECONDITIONS: {
						// none
					}
					
					try {
						provideOutputData(new EnhancedReport(getInputData()));
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

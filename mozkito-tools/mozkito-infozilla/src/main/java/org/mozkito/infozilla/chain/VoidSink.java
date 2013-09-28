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
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.infozilla.model.EnhancedReport;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class VoidSink extends Sink<EnhancedReport> {
	
	/**
	 * Instantiates a new void sink.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public VoidSink(final Group threadGroup, final ISettings settings) {
		super(threadGroup, settings, false);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			
			new ProcessHook<EnhancedReport, EnhancedReport>(this) {
				
				@Override
				public void process() {
					PRECONDITIONS: {
						// none
					}
					
					try {
						if (Logger.logInfo()) {
							Logger.info(getInputData().toString());
						}
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

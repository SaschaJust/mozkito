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

package org.mozkito.mappings.chains.sinks;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.utils.graph.MappingsGraph;

/**
 * The Class Graphify.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Graphify extends Sink<Mapping> {
	
	/**
	 * Instantiates a new graphify.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param mappingsGraph
	 *            the mappings graph
	 */
	public Graphify(final Group threadGroup, final ISettings settings, final MappingsGraph mappingsGraph) {
		super(threadGroup, settings, false);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			new ProcessHook<Mapping, Mapping>(this) {
				
				@Override
				public void process() {
					final Mapping mapping = getInputData();
					
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("Graphify.storing", mapping)); //$NON-NLS-1$
					}
					
					mappingsGraph.addMapping(mapping);
					
					// if ((++i % PERSIST_COUNT_THRESHOLD) == 0) {
					// ((TitanGraph) graph).commit();
					// }
				}
			};
			
			new PostExecutionHook<Mapping, Mapping>(this) {
				
				@Override
				public void postExecution() {
					PRECONDITIONS: {
						// none
					}
					
					try {
						mappingsGraph.shutdown();
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

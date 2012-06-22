/***********************************************************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.threads.Filter;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

/**
 * The Class MappingEngineProcessor.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MappingEngineProcessor extends Filter<Mapping> {
	
	/**
	 * Instantiates a new mapping engine processor.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 * @param engine
	 *            the engine
	 */
	public MappingEngineProcessor(final Group threadGroup, final Settings settings, final MappingFinder finder,
	        final MappingEngine engine) {
		super(threadGroup, settings, false);
		
		new ProcessHook<Mapping, Mapping>(this) {
			
			@Override
			public void process() {
				final Mapping candidate = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("[%s] Processing mapping for '%s' -> '%s'.", engine.getHandle(),
					             candidate.getElement1(), candidate.getElement2());
				}
				
				final Mapping score = finder.score(engine, candidate);
				
				provideOutputData(score);
				
			}
		};
	}
}

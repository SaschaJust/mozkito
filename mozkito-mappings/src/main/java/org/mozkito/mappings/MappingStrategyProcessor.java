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

package org.mozkito.mappings;

import org.mozkito.mappings.finder.MappingFinder;
import org.mozkito.mappings.model.IComposite;
import org.mozkito.mappings.strategies.MappingStrategy;

import net.ownhero.dev.andama.threads.Filter;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class MappingStrategyProcessor.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MappingStrategyProcessor extends Filter<IComposite> {
	
	/**
	 * Instantiates a new mapping strategy processor.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 * @param strategy
	 *            the strategy
	 */
	public MappingStrategyProcessor(final Group threadGroup, final Settings settings, final MappingFinder finder,
	        final MappingStrategy strategy) {
		super(threadGroup, settings, false);
		new ProcessHook<IComposite, IComposite>(this) {
			
			@Override
			public void process() {
				final IComposite inputData = getInputData();
				final IComposite mapping = finder.map(strategy, inputData);
				if (mapping != null) {
					if (Logger.logInfo()) {
						Logger.info("Providing for store operation: " + mapping);
					}
					setOutputData(mapping);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Discarding " + mapping + " due to non-positive score (" + getInputData() + ").");
					}
					skipOutputData(mapping);
				}
			}
		};
	}
	
}

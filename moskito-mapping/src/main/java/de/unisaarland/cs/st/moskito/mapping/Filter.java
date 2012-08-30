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

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.model.Composite;
import de.unisaarland.cs.st.moskito.mapping.model.IComposite;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

/**
 * The Class Filter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Filter extends Transformer<Composite, Mapping> {
	
	/**
	 * Instantiates a new filter.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 */
	public Filter(final Group threadGroup, final Settings settings, final MappingFinder finder) {
		super(threadGroup, settings, false);
		new ProcessHook<Composite, Mapping>(this) {
			
			@Override
			public void process() {
				final IComposite inputData = getInputData();
				final Mapping mapping = finder.filter(inputData);
				if (mapping != null) {
					if (Logger.logInfo()) {
						Logger.info("Providing for store operation: " + mapping);
					}
					setOutputData(mapping);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Discarding " + mapping + " due to non-positive score (" + inputData + ").");
					}
					skipOutputData(mapping);
				}
			}
		};
	}
	
}

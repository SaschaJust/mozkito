/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.FilteredMapping;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringMappingFilter extends AndamaTransformer<PersistentMapping, FilteredMapping> {
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public ScoringMappingFilter(final AndamaGroup threadGroup, final MappingSettings settings,
	        final MappingFinder finder) {
		super(threadGroup, settings, false);
		new ProcessHook<PersistentMapping, FilteredMapping>(this) {
			
			@Override
			public void process() {
				FilteredMapping mapping = finder.filter(getInputData());
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

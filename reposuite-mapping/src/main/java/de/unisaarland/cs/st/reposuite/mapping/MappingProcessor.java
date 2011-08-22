/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingProcessor extends RepoSuiteTransformerThread<MapScore, PersistentMapping> {
	
	private final MappingFinder mappingFinder;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 */
	public MappingProcessor(final RepoSuiteThreadGroup threadGroup, final MappingSettings settings,
	        final MappingFinder finder) {
		super(threadGroup, MappingProcessor.class.getSimpleName(), settings);
		this.mappingFinder = finder;
	}
	
	@Override
	public void run() {
		try {
			
			if (!checkConnections() || !checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			
			MapScore score = null;
			
			while (!isShutdown() && ((score = read()) != null)) {
				PersistentMapping mapping = this.mappingFinder.map(score);
				if (mapping != null) {
					if (Logger.logInfo()) {
						Logger.info("Providing for store operation: " + mapping);
					}
					write(mapping);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Discarding " + mapping + " due to non-positive score (" + score + ").");
					}
				}
			}
			
			finish();
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}

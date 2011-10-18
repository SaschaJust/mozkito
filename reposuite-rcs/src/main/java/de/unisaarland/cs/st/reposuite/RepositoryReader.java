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
/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * The {@link RepositoryReader} reads data from a given {@link Repository} and
 * outputs {@link LogEntry} chunks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryReader extends AndamaSource<LogEntry> {
	
	private LogIterator logIterator;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryReader(final AndamaGroup threadGroup, final RepositorySettings settings,
	        final Repository repository) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<LogEntry, LogEntry>(this) {
			
			@Override
			public void preExecution() {
				if (Logger.logInfo()) {
					Logger.info("Requesting logs from " + repository);
				}
				
				repository.getTransactionCount();
				long cacheSize = (Long) getSettings().getSetting("cache.size").getValue();
				RepositoryReader.this.logIterator = (LogIterator) repository.log(repository.getFirstRevisionId(),
				                                                                 repository.getEndRevision(),
				                                                                 (int) cacheSize);
				
				if (Logger.logInfo()) {
					Logger.info("Created iterator.");
				}
			}
		};
		
		new ProcessHook<LogEntry, LogEntry>(this) {
			
			@Override
			public void process() {
				if (RepositoryReader.this.logIterator.hasNext()) {
					if (Logger.logTrace()) {
						Logger.trace("filling queue [" + outputSize() + "]");
					}
					
					LogEntry entry = RepositoryReader.this.logIterator.next();
					
					if (Logger.logTrace()) {
						Logger.trace("with entry: " + entry);
					}
					
					provideOutputData(entry);
					setCompleted();
				} else {
					setCompleted();
				}
			}
		};
	}
	
}

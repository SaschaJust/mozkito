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
/**
 * 
 */
package org.mozkito;

import java.util.Iterator;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.exceptions.RepositoryOperationException;

/**
 * The {@link RepositoryReader} reads data from a given {@link Repository} and outputs {@link LogEntry} chunks.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class RepositoryReader extends Source<LogEntry> {
	
	/** The log iterator. */
	private Iterator<LogEntry> logIterator;
	
	/**
	 * Instantiates a new repository reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param repository
	 *            the repository
	 */
	public RepositoryReader(final Group threadGroup, final Settings settings, final Repository repository) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<LogEntry, LogEntry>(this) {
			
			@Override
			public void preExecution() {
				
				if (Logger.logInfo()) {
					Logger.info("Requesting logs from " + repository);
				}
				try {
					repository.getChangeSetCount();
					RepositoryReader.this.logIterator = repository.log(System.getProperty("repository.startAt",
					                                                                      repository.getFirstRevisionId()),
					                                                   System.getProperty("repository.stopAt",
					                                                                      repository.getEndRevision()))
					                                              .iterator();
				} catch (final RepositoryOperationException e) {
					throw new UnrecoverableError(e);
				}
				
				if (Logger.logInfo()) {
					Logger.info("Created iterator.");
				}
			}
		};
		
		new ProcessHook<LogEntry, LogEntry>(this) {
			
			@Override
			public void process() {
				if (RepositoryReader.this.logIterator.hasNext()) {
					
					final LogEntry entry = RepositoryReader.this.logIterator.next();
					
					if (Logger.logDebug()) {
						Logger.debug("Processing: " + entry);
					}
					
					if (entry == null) {
						provideOutputData(null, true);
						setCompleted();
						
						if (Logger.logDebug()) {
							Logger.debug("No more input data (through hasNext() returned true). this.input: "
							        + getInputData() + ", this.output: " + getOutputData() + ", this.status: "
							        + completed());
						}
					} else {
						providePartialOutputData(entry);
					}
				} else {
					provideOutputData(null, true);
					setCompleted();
					
				}
			}
		};
	}
}

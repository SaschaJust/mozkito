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
package de.unisaarland.cs.st.moskito;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.elements.LogEntry;
import de.unisaarland.cs.st.moskito.rcs.elements.LogIterator;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

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
				System.out.println("test test test test test ");
				try {
					PersistenceUtil util = PersistenceManager.getUtil();
					Criteria<RCSTransaction> c2003 = util.createCriteria(RCSTransaction.class)
					                                     .eq("id", "9d0145a6a6646a4a17342f2d89bab05455888794");
					RCSTransaction t2003 = util.load(c2003).get(0);
					Criteria<RCSTransaction> c2009 = util.createCriteria(RCSTransaction.class)
					                                     .eq("id", "4961e37c07a56d511700ffef36feafe1fa22a4d6");
					RCSTransaction t2009 = util.load(c2009).get(0);
					System.out.println("t2003 compared to t2009 results in: " + t2003.compareTo(t2009));
				} catch (UninitializedDatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				throw new UnrecoverableError("KABUMM!");
				//
				// if (Logger.logInfo()) {
				// Logger.info("Requesting logs from " + repository);
				// }
				//
				// repository.getTransactionCount();
				// final long cacheSize = (Long)
				// getSettings().getSetting("cache.size").getValue();
				// RepositoryReader.this.logIterator = (LogIterator)
				// repository.log(repository.getFirstRevisionId(),
				// repository.getEndRevision(),
				// (int) cacheSize);
				//
				// if (Logger.logInfo()) {
				// Logger.info("Created iterator.");
				// }
			}
		};
		
		new ProcessHook<LogEntry, LogEntry>(this) {
			
			@Override
			public void process() {
				if (RepositoryReader.this.logIterator.hasNext()) {
					
					final LogEntry entry = RepositoryReader.this.logIterator.next();
					
					if (Logger.logDebug()) {
						Logger.debug("with entry: " + entry);
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

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
package de.unisaarland.cs.st.moskito;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.elements.LogEntry;
import de.unisaarland.cs.st.moskito.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * The {@link RepositoryParser} takes {@link LogEntry}s from the input storage, parses the data and stores the produced
 * {@link RCSTransaction} in the output storage.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryParser extends AndamaTransformer<LogEntry, RCSTransaction> {
	
	/**
	 * @see RepoSuiteTransformerThread
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryParser(final AndamaGroup threadGroup, final RepositorySettings settings,
	        final Repository repository, PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		final RCSFileManager fileManager = new RCSFileManager();
		final Set<String> tids = new HashSet<String>();
		final PersistenceUtil pUtil = persistenceUtil;
		
		new ProcessHook<LogEntry, RCSTransaction>(this) {
			
			@Override
			public void process() {
				final LogEntry data = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Parsing " + data);
				}
				
				if (tids.contains(data.getRevision())) {
					throw new UnrecoverableError("Attempt to create an transaction that was created before! ("
					        + data.getRevision() + ")");
				}
				
				final RCSTransaction rcsTransaction = RCSTransaction.createTransaction(data.getRevision(),
				                                                                       data.getMessage(),
				                                                                       data.getDateTime(),
				                                                                       data.getAuthor(),
				                                                                       data.getOriginalId(), pUtil);
				tids.add(data.getRevision());
				final Map<String, ChangeType> changedPaths = repository.getChangedPaths(data.getRevision());
				for (final String fileName : changedPaths.keySet()) {
					RCSFile file;
					
					if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
						file = fileManager.getFile(repository.getFormerPathName(rcsTransaction.getId(),
						
						fileName));
						if (file == null) {
							
							if (Logger.logWarn()) {
								Logger.warn("Found renaming of unknown file. Assuming type `added` instead of `renamed`: "
								        + changedPaths.get(fileName));
							}
							file = fileManager.getFile(fileName);
							
							if (file == null) {
								file = fileManager.createFile(fileName, rcsTransaction);
							}
						} else {
							file.assignTransaction(rcsTransaction, fileName);
						}
					} else {
						file = fileManager.getFile(fileName);
						
						if (file == null) {
							file = fileManager.createFile(fileName, rcsTransaction);
						}
					}
					
					new RCSRevision(rcsTransaction, file, changedPaths.get(fileName));
				}
				
				provideOutputData(rcsTransaction);
			}
		};
	}
	
}

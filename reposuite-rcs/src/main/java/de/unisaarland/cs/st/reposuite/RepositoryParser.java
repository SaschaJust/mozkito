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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * The {@link RepositoryParser} takes {@link LogEntry}s from the input storage,
 * parses the data and stores the produced {@link RCSTransaction} in the output
 * storage.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryParser extends AndamaTransformer<LogEntry, RCSTransaction> {
	
	private final Repository  repository;
	private RCSFileManager    fileManager;
	private final Set<String> tids = new HashSet<String>();
	
	/**
	 * @see RepoSuiteTransformerThread
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryParser(final AndamaGroup threadGroup, final RepositorySettings settings,
	        final Repository repository) {
		super(threadGroup, settings, false);
		this.repository = repository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterExecution()
	 */
	@Override
	public void afterExecution() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterProcess()
	 */
	@Override
	public void afterProcess() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		this.fileManager = new RCSFileManager();
	}
	
	@Override
	public void beforeProcess() {
	}
	
	@Override
	public RCSTransaction process(final LogEntry data) throws net.ownhero.dev.andama.exceptions.UnrecoverableError,
	                                                  Shutdown {
		if (Logger.logDebug()) {
			Logger.debug("Parsing " + data);
		}
		if (this.tids.contains(data.getRevision())) {
			throw new UnrecoverableError("Attempt to create an transaction that was created before! ("
			        + data.getRevision() + ")");
		}
		
		RCSTransaction rcsTransaction = RCSTransaction.createTransaction(data.getRevision(), data.getMessage(),
		                                                                 data.getDateTime(), data.getAuthor(),
		                                                                 data.getOriginalId());
		this.tids.add(data.getRevision());
		Map<String, ChangeType> changedPaths = this.repository.getChangedPaths(data.getRevision());
		for (String fileName : changedPaths.keySet()) {
			RCSFile file;
			
			if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
				file = this.fileManager.getFile(this.repository.getFormerPathName(rcsTransaction.getId(),
				
				fileName));
				if (file == null) {
					
					if (Logger.logWarn()) {
						Logger.warn("Found renaming of unknown file. Assuming type `added` instead of `renamed`: "
						        + changedPaths.get(fileName));
					}
					file = this.fileManager.getFile(fileName);
					
					if (file == null) {
						file = this.fileManager.createFile(fileName, rcsTransaction);
					}
				} else {
					file.assignTransaction(rcsTransaction, fileName);
				}
			} else {
				file = this.fileManager.getFile(fileName);
				
				if (file == null) {
					file = this.fileManager.createFile(fileName, rcsTransaction);
				}
			}
			
			new RCSRevision(rcsTransaction, file, changedPaths.get(fileName));
		}
		
		if (Logger.logTrace()) {
			Logger.trace("filling queue [" + outputSize() + "]");
		}
		
		return rcsTransaction;
	}
}

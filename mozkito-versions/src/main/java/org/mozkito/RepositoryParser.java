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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;
import org.mozkito.versions.elements.RCSFileManager;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSRevision;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The {@link RepositoryParser} takes {@link LogEntry}s from the input storage, parses the data and stores the produced.
 * 
 * {@link RCSTransaction} in the output storage.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class RepositoryParser extends Transformer<LogEntry, RCSTransaction> {
	
	private static final RCSFileManager FILE_MANAGER    = new RCSFileManager();
	private static final Set<String>    TRANSACTION_IDS = new HashSet<String>();
	
	/**
	 * Parses the log entry assuming that it was extracted from the provided repository and returns the corresponding
	 * RCSTransaction.
	 * 
	 * @param repository
	 *            the repository
	 * @param data
	 *            the data
	 * @return the rCS transaction
	 */
	public static RCSTransaction parseLogEntry(final Repository repository,
	                                           final LogEntry data) {
		if (Logger.logDebug()) {
			Logger.debug("Parsing " + data);
		}
		
		if (TRANSACTION_IDS.contains(data.getRevision())) {
			throw new UnrecoverableError("Attempt to create an transaction that was created before! ("
			        + data.getRevision() + ")");
		}
		
		final RCSTransaction rcsTransaction = new RCSTransaction(data.getRevision(), data.getMessage(),
		                                                         data.getDateTime(), data.getAuthor(),
		                                                         data.getOriginalId());
		TRANSACTION_IDS.add(data.getRevision());
		final Map<String, ChangeType> changedPaths = repository.getChangedPaths(data.getRevision());
		for (final String fileName : changedPaths.keySet()) {
			RCSFile file;
			
			if (changedPaths.get(fileName).equals(ChangeType.Renamed)) {
				file = FILE_MANAGER.getFile(repository.getFormerPathName(rcsTransaction.getId(), fileName));
				if (file == null) {
					
					if (Logger.logWarn()) {
						Logger.warn("Found renaming of unknown file. Assuming type `added` instead of `renamed`: "
						        + changedPaths.get(fileName));
					}
					file = FILE_MANAGER.getFile(fileName);
					
					if (file == null) {
						file = FILE_MANAGER.createFile(fileName, rcsTransaction);
					}
				} else {
					file.assignTransaction(rcsTransaction, fileName);
				}
			} else {
				file = FILE_MANAGER.getFile(fileName);
				
				if (file == null) {
					file = FILE_MANAGER.createFile(fileName, rcsTransaction);
				}
			}
			
			new RCSRevision(rcsTransaction, file, changedPaths.get(fileName));
		}
		return rcsTransaction;
	}
	
	/**
	 * Instantiates a new repository parser.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param repository
	 *            the repository
	 */
	public RepositoryParser(final Group threadGroup, final Settings settings, final Repository repository) {
		super(threadGroup, settings, false);
		
		new ProcessHook<LogEntry, RCSTransaction>(this) {
			
			@Override
			public void process() {
				final LogEntry data = getInputData();
				final RCSTransaction rcsTransaction = parseLogEntry(repository, data);
				provideOutputData(rcsTransaction);
			}
		};
	}
	
}

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

import java.util.HashMap;
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
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * The {@link RepositoryParser} takes {@link LogEntry}s from the input storage, parses the data and stores the produced.
 * 
 * {@link ChangeSet} in the output storage.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class RepositoryParser extends Transformer<LogEntry, ChangeSet> {
	
	/** The Constant TRANSACTION_IDS. */
	private static final Set<String>         TRANSACTION_IDS = new HashSet<String>();
	
	/** The Constant CURRENT_FILES. */
	private static final Map<String, Handle> CURRENT_FILES   = new HashMap<String, Handle>();
	
	/**
	 * Parses the log entry assuming that it was extracted from the provided repository and returns the corresponding
	 * ChangeSet.
	 * 
	 * @param repository
	 *            the repository
	 * @param archive
	 *            the archive
	 * @param data
	 *            the data
	 * @return the rCS transaction
	 */
	public static ChangeSet parseLogEntry(final Repository repository,
	                                      final VersionArchive archive,
	                                      final LogEntry data) {
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Parsing " + data);
			}
			
			if (TRANSACTION_IDS.contains(data.getRevision())) {
				throw new UnrecoverableError("Attempt to create an transaction that was created before! ("
				        + data.getRevision() + ")");
			}
			
			final ChangeSet changeSet = new ChangeSet(data.getRevision(), data.getMessage(), data.getDateTime(),
			                                          data.getAuthor(), data.getOriginalId());
			TRANSACTION_IDS.add(data.getRevision());
			final Map<String, ChangeType> changedPaths = repository.getChangedPaths(data.getRevision());
			for (final String fileName : changedPaths.keySet()) {
				switch (changedPaths.get(fileName)) {
					case Renamed:
						Handle renamedHandle = CURRENT_FILES.get(fileName);
						if (renamedHandle == null) {
							if (Logger.logError()) {
								Logger.error("Renaming of unknown file with file name %s. Assuming 'ADD' operation instead. Data may be incosistent!",
								             fileName);
							}
							renamedHandle = new Handle(archive);
							CURRENT_FILES.put(fileName, renamedHandle);
							final Revision addRevision = new Revision(changeSet, renamedHandle,
							                                          changedPaths.get(fileName));
							renamedHandle.assignRevision(addRevision, fileName);
							break;
						}
						final Revision renameRevision = new Revision(changeSet, renamedHandle,
						                                             changedPaths.get(fileName));
						renamedHandle.assignRevision(renameRevision, fileName);
						break;
					case Added:
						final Handle addedHandle = new Handle(archive);
						CURRENT_FILES.put(fileName, addedHandle);
						final Revision revision = new Revision(changeSet, addedHandle, changedPaths.get(fileName));
						addedHandle.assignRevision(revision, fileName);
						break;
					case Deleted:
						final Handle deletedHandle = CURRENT_FILES.get(fileName);
						if (deletedHandle == null) {
							if (Logger.logError()) {
								Logger.error("Deletion of unknown file with file name %s. Ignoring operation. Data may be incosistent!",
								             fileName);
							}
							break;
						}
						new Revision(changeSet, deletedHandle, changedPaths.get(fileName));
						break;
					default:
						Handle modifiedHandle = CURRENT_FILES.get(fileName);
						if (modifiedHandle == null) {
							if (Logger.logError()) {
								Logger.error("Modification of unknown file with file name %s. Assuming 'ADD' operation instead. Data may be incosistent!",
								             fileName);
							}
							modifiedHandle = new Handle(archive);
							CURRENT_FILES.put(fileName, modifiedHandle);
							final Revision addRevision = new Revision(changeSet, modifiedHandle,
							                                          changedPaths.get(fileName));
							modifiedHandle.assignRevision(addRevision, fileName);
							break;
						}
						new Revision(changeSet, modifiedHandle, changedPaths.get(fileName));
						break;
				}
			}
			return changeSet;
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
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
	 * @param archive
	 *            the archive
	 */
	public RepositoryParser(final Group threadGroup, final Settings settings, final Repository repository,
	        final VersionArchive archive) {
		super(threadGroup, settings, false);
		
		new ProcessHook<LogEntry, ChangeSet>(this) {
			
			@Override
			public void process() {
				final LogEntry data = getInputData();
				final ChangeSet changeSet = parseLogEntry(repository, archive, data);
				provideOutputData(changeSet);
			}
		};
	}
	
}

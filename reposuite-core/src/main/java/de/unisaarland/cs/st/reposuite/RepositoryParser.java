/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteTransformerThread;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The {@link RepositoryParser} takes {@link LogEntry}s from the input storage,
 * parses the data and stores the produced {@link RCSTransaction} in the output
 * storage.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryParser extends RepoSuiteTransformerThread<LogEntry, RCSTransaction> {
	
	private final Repository repository;
	private RCSFileManager   fileManager;
	
	/**
	 * @see RepoSuiteTransformerThread
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryParser(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
			final Repository repository) {
		super(threadGroup, RepositoryParser.class.getSimpleName(), settings);
		this.repository = repository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		LogEntry entry;
		RCSTransaction previousRcsTransaction = null;
		this.fileManager = new RCSFileManager();
		try {
			while (!isShutdown() && ((entry = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Parsing " + entry);
				}
				RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
						entry.getDateTime(), entry.getAuthor(), previousRcsTransaction);
				Map<String, ChangeType> changedPaths = this.repository.getChangedPaths(entry.getRevision());
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
					
					rcsTransaction.addRevision(new RCSRevision(rcsTransaction, file, changedPaths.get(fileName),
							previousRcsTransaction));
				}
				if (Logger.logTrace()) {
					Logger.trace("filling queue [" + outputSize() + "]");
				}
				System.err.println(rcsTransaction);
				write(rcsTransaction);
				previousRcsTransaction = rcsTransaction;
			}
			finish();
		} catch (InterruptedException e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
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
	
	private final Repository                  repository;
	private RCSFileManager                    fileManager;
	private final Map<String, RevDependency>  reverseDependencies = new HashMap<String, RevDependency>();
	private final Map<String, String>         latest              = new HashMap<String, String>();
	private final Map<String, RCSTransaction> cached              = new HashMap<String, RCSTransaction>();
	
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
		
		for (RevDependencyIterator revdep = this.repository.getRevDependencyIterator(); revdep.hasNext();) {
			RevDependency rd = revdep.next();
			this.reverseDependencies.put(rd.getId(), rd);
		}
		
		this.fileManager = new RCSFileManager();
		try {
			while (!isShutdown() && ((entry = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Parsing " + entry);
				}
				
				RCSTransaction rcsTransaction = new RCSTransaction(entry.getRevision(), entry.getMessage(),
				                                                   entry.getDateTime(), entry.getAuthor());
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
					
					new RCSRevision(rcsTransaction, file, changedPaths.get(fileName), previousRcsTransaction);
				}
				
				RevDependency revdep = this.reverseDependencies.get(entry.getRevision());
				rcsTransaction.setBranch(revdep.getCommitBranch());
				rcsTransaction.setTag(revdep.getTagName());
				for (String parent : revdep.getParents()) {
					if (!this.cached.containsKey(parent)) {
						// ERROR!
						
					} else {
						RCSTransaction parentTransaction = this.cached.get(parent);
						rcsTransaction.addParent(parentTransaction);
						
						// detect closing branch
						if (revdep.getParents().size() > 1) {
							if (!parentTransaction.getBranch().getName().equals(rcsTransaction.getBranch().getName())) {
								// closed branch
								// remove parent transaction from cache
								this.cached.remove(parentTransaction.getId());
								// remove branch from cache
								this.latest.remove(parentTransaction.getBranch().getName());
								
								parentTransaction.getBranch().setEnd(parentTransaction);
							}
						}
					}
				}
				
				// detect new branch
				if (rcsTransaction.getParents().size() == 1) {
					RCSTransaction parentTransaction = rcsTransaction.getParents().iterator().next();
					if (!rcsTransaction.getBranch().equals(parentTransaction.getBranch())
					        && (parentTransaction.getChildren().size() > 1)) {
						rcsTransaction.getBranch().setBegin(rcsTransaction);
					}
				}
				
				// ++++++ Update caches ++++++
				// remove old "latest transaction" from cache
				if (this.cached.containsKey(this.latest.get(revdep.getCommitBranch().getName()))) {
					this.cached.remove(this.latest.get(revdep.getCommitBranch().getName()));
				}
				
				// add transaction to cache
				this.cached.put(rcsTransaction.getId(), rcsTransaction);
				
				// set new "latest transaction" to active branch cache
				this.latest.put(revdep.getCommitBranch().getName(), rcsTransaction.getId());
				// ------ Update caches ------
				
				if (Logger.logTrace()) {
					Logger.trace("filling queue [" + outputSize() + "]");
				}
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

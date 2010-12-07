/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.security.UnrecoverableEntryException;
import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteFilterThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryGraphBuilder extends RepoSuiteFilterThread<RCSTransaction> {
	
	private final Repository                  repository;
	private final HibernateUtil               hibernateUtil;
	private final Map<String, RevDependency>  reverseDependencies = new HashMap<String, RevDependency>();
	private final Map<String, String>         latest              = new HashMap<String, String>();
	private final Map<String, RCSTransaction> cached              = new HashMap<String, RCSTransaction>();
	
	public RepositoryGraphBuilder(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
	        final Repository repository, final HibernateUtil hibernateUtil) {
		super(threadGroup, RepositoryGraphBuilder.class.getSimpleName(), settings);
		this.repository = repository;
		this.hibernateUtil = hibernateUtil;
	}
	
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		if (Logger.logInfo()) {
			Logger.info("Fetching reverse dependencies. This could take a while...");
		}
		
		for (RevDependencyIterator revdep = this.repository.getRevDependencyIterator(); revdep.hasNext();) {
			RevDependency rd = revdep.next();
			this.reverseDependencies.put(rd.getId(), rd);
		}
		
		if (Logger.logInfo()) {
			Logger.info("Reverse dependencies ready.");
		}
		
		RCSTransaction rcsTransaction = null;
		
		try {
			while (!isShutdown() && ((rcsTransaction = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Updating graph for " + rcsTransaction);
				}
				
				RevDependency revdep = this.reverseDependencies.get(rcsTransaction.getId());
				rcsTransaction.setBranch(revdep.getCommitBranch());
				rcsTransaction.setTag(revdep.getTagName());
				for (String parent : revdep.getParents()) {
					if (!this.cached.containsKey(parent)) {
						if (Logger.logError()) {
							Logger.error("Got child of unknown parent. This should not happen.");
						}
						throw new UnrecoverableEntryException("Got child of unknown parent. This should not happen.");
					} else {
						RCSTransaction parentTransaction = this.cached.get(parent);
						rcsTransaction.addParent(parentTransaction);
					}
				}
				
				// detect new branch
				if (rcsTransaction.getParents().size() == 1) {
					RCSTransaction parentTransaction = rcsTransaction.getParents().iterator().next();
					if (!rcsTransaction.getBranch().equals(parentTransaction.getBranch())) {
						rcsTransaction.getBranch().setBegin(rcsTransaction);
					}
				} else if (rcsTransaction.getParents().isEmpty()) {
					if (rcsTransaction.getBranch().getBegin() == null) {
						rcsTransaction.getBranch().setBegin(rcsTransaction);
					}
				}
				
				// we have to store the current transaction before (!) we update
				// the parents' children
				write(rcsTransaction);
				
				// detect branch merge
				if (revdep.getParents().size() > 1) {
					for (String parent : revdep.getParents()) {
						RCSTransaction parentTransaction = this.cached.get(parent);
						
						if (!parentTransaction.getBranch().getName().equals(rcsTransaction.getBranch().getName())) {
							// closed branch
							// remove parent transaction from cache
							
							// wait for children to be persisted
							while (getOutputStorage().size() > 0) {
								Thread.sleep(1000);
							}
							
							parentTransaction.addChild(rcsTransaction);
							this.hibernateUtil.update(this.cached.remove(parentTransaction.getId()));
							// remove branch from cache
							this.latest.remove(parentTransaction.getBranch().getName());
							
							parentTransaction.getBranch().setEnd(parentTransaction);
						}
					}
				}
				
				// ++++++ Update caches ++++++
				// remove old "latest transaction" from cache
				if (this.cached.containsKey(this.latest.get(revdep.getCommitBranch().getName()))) {
					while (getOutputStorage().size() > 0) {
						Thread.sleep(1000);
					}
					this.cached.get(this.latest.get(revdep.getCommitBranch().getName())).addChild(rcsTransaction);
					this.hibernateUtil.update(this.cached.remove(this.latest.get(revdep.getCommitBranch().getName())));
				}
				
				// add transaction to cache
				this.cached.put(rcsTransaction.getId(), rcsTransaction);
				
				// set new "latest transaction" to active branch cache
				this.latest.put(revdep.getCommitBranch().getName(), rcsTransaction.getId());
				// ------ Update caches ------
				
			}
			
			// wait for children to be persisted
			while (getOutputStorage().size() > 0) {
				Thread.sleep(1000);
			}
			
			// persist all remaining cached transactions
			this.hibernateUtil.commitTransaction();
			this.hibernateUtil.beginTransaction();
			for (RCSTransaction transaction : this.cached.values()) {
				this.hibernateUtil.update(transaction);
			}
			this.hibernateUtil.commitTransaction();
			
			finish();
		} catch (Exception e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}

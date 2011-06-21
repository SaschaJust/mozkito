/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.security.UnrecoverableEntryException;
import java.util.HashMap;
import java.util.Map;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class GraphBuilder extends RepoSuiteSinkThread<RCSTransaction> {
	
	private final Repository                  repository;
	private final PersistenceUtil             persistenceUtil;
	private final Map<String, RevDependency>  reverseDependencies = new HashMap<String, RevDependency>();
	private final Map<String, String>         latest              = new HashMap<String, String>();
	private final Map<String, RCSTransaction> cached              = new HashMap<String, RCSTransaction>();
	
	public GraphBuilder(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
	        final Repository repository, final PersistenceUtil persistenceUtil) {
		super(threadGroup, GraphBuilder.class.getSimpleName(), settings);
		this.repository = repository;
		this.persistenceUtil = persistenceUtil;
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
			this.persistenceUtil.beginTransaction();
			while (!isShutdown() && ((rcsTransaction = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Updating graph for " + rcsTransaction);
				}
				
				RevDependency revdep = this.reverseDependencies.get(rcsTransaction.getId());
				RCSBranch rcsBranch = revdep.getCommitBranch();
				this.persistenceUtil.update(rcsBranch);
				rcsTransaction.setBranch(rcsBranch);
				rcsTransaction.addAllTags(revdep.getTagNames());
				for (String parent : revdep.getParents()) {
					RCSTransaction parentTransaction = null;
					if (!this.cached.containsKey(parent)) {
						try {
							parentTransaction = this.persistenceUtil.loadById(parent, RCSTransaction.class);
						} catch (ArrayIndexOutOfBoundsException e) {
							throw new UnrecoverableError(
							                             "Got child of parent that is not cached an cannot be loaded anymore.",
							                             e);
						}
						if (parentTransaction != null) {
							this.cached.put(parentTransaction.getId(), parentTransaction);
						}
					} else {
						parentTransaction = this.cached.get(parent);
					}
					if (parentTransaction != null) {
						rcsTransaction.addParent(parentTransaction);
					} else {
						if (Logger.logError()) {
							Logger.error("Got child `" + rcsTransaction.getId()
							        + "` of unknown parent. This should not happen.");
						}
						throw new UnrecoverableEntryException("Got child of unknown parent. This should not happen.");
					}
				}
				
				// detect new branch
				if (rcsTransaction.getParents().isEmpty()) {
					rcsTransaction.getBranch().setBegin(rcsTransaction);
				} else {
					RCSBranch branch = rcsTransaction.getBranch();
					boolean foundParentInBranch = false;
					for (RCSTransaction parent : rcsTransaction.getParents()) {
						if (parent.getBranch().equals(branch)) {
							foundParentInBranch = true;
							break;
						}
					}
					if (!foundParentInBranch) {
						branch.setBegin(rcsTransaction);
					}
				}
				
				// detect branch merge
				if (revdep.getParents().size() > 1) {
					for (String parent : revdep.getParents()) {
						RCSTransaction parentTransaction = this.cached.get(parent);
						
						if (!parentTransaction.getBranch().getName().equals(rcsTransaction.getBranch().getName())) {
							// closed branch
							// remove parent transaction from cache
							
							parentTransaction.addChild(rcsTransaction);
							
							// this.persistenceUtil.update(this.cached.remove(parentTransaction.getId()));
							this.cached.remove(parentTransaction.getId());
							this.persistenceUtil.commitTransaction();
							this.persistenceUtil.beginTransaction();
							// remove branch from cache
							this.latest.remove(parentTransaction.getBranch().getName());
							
							parentTransaction.getBranch().setEnd(parentTransaction);
						}
					}
				}
				
				// ++++++ Update caches ++++++
				// remove old "latest transaction" from cache
				if (this.cached.containsKey(this.latest.get(revdep.getCommitBranch().getName()))) {
					this.cached.get(this.latest.get(revdep.getCommitBranch().getName())).addChild(rcsTransaction);
					
					// this.persistenceUtil.update(this.cached.remove(this.latest.get(revdep.getCommitBranch().getName())));
					this.cached.remove(this.latest.get(revdep.getCommitBranch().getName()));
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
				
				// add transaction to cache
				this.cached.put(rcsTransaction.getId(), rcsTransaction);
				
				// set new "latest transaction" to active branch cache
				this.latest.put(revdep.getCommitBranch().getName(), rcsTransaction.getId());
				// ------ Update caches ------
				
			}
			
			// persist all remaining cached transactions
			// this.persistenceUtil.commitTransaction();
			// this.persistenceUtil.beginTransaction();
			// for (RCSTransaction transaction : this.cached.values()) {
			// this.persistenceUtil.update(transaction);
			// }
			this.persistenceUtil.commitTransaction();
			// this.persistenceUtil.flush();
			this.cached.clear();
			
			finish();
		} catch (Exception e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}

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

import java.security.UnrecoverableEntryException;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.elements.RevDependency;
import de.unisaarland.cs.st.moskito.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class GraphBuilder extends AndamaSink<RCSTransaction> {
	
	public GraphBuilder(final AndamaGroup threadGroup, final RepositorySettings settings, final Repository repository,
			final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		final Map<String, RevDependency> reverseDependencies = new HashMap<String, RevDependency>();
		final Map<String, String> latest = new HashMap<String, String>();
		final Map<String, RCSTransaction> cached = new HashMap<String, RCSTransaction>();
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				if (Logger.logInfo()) {
					Logger.info("Fetching reverse dependencies. This could take a while...");
				}
				
				for (RevDependencyIterator revdep = repository.getRevDependencyIterator(); revdep.hasNext();) {
					RevDependency rd = revdep.next();
					reverseDependencies.put(rd.getId(), rd);
				}
				
				if (Logger.logInfo()) {
					Logger.info("Reverse dependencies ready.");
				}
				
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (Logger.logDebug()) {
					Logger.debug("Updating graph for " + getInputData());
				}
				
				RCSTransaction rcsTransaction = getInputData();
				
				RevDependency revdep = reverseDependencies.get(rcsTransaction.getId());
				RCSBranch rcsBranch = revdep.getCommitBranch();
				rcsTransaction.setBranch(rcsBranch);
				rcsTransaction.addAllTags(revdep.getTagNames());
				for (String parent : revdep.getParents()) {
					RCSTransaction parentTransaction = null;
					if (!cached.containsKey(parent)) {
						try {
							parentTransaction = persistenceUtil.loadById(parent, RCSTransaction.class);
						} catch (ArrayIndexOutOfBoundsException e) {
							throw new UnrecoverableError(
									"Got child of parent that is not cached an cannot be loaded anymore.",
									e);
						}
						if (parentTransaction != null) {
							cached.put(parentTransaction.getId(), parentTransaction);
						}
					} else {
						parentTransaction = cached.get(parent);
					}
					if (parentTransaction != null) {
						rcsTransaction.addParent(parentTransaction);
					} else {
						if (Logger.logError()) {
							Logger.error("Got child `" + rcsTransaction.getId()
									+ "` of unknown parent. This should not happen.");
						}
						throw new UnrecoverableError(
								new UnrecoverableEntryException(
										"Got child of unknown parent. This should not happen."));
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
						RCSTransaction parentTransaction = cached.get(parent);
						
						if (!parentTransaction.getBranch().getName().equals(rcsTransaction.getBranch().getName())) {
							// closed branch
							// remove parent transaction from cache
							
							parentTransaction.addChild(rcsTransaction);
							
							// persistenceUtil.update(cached.remove(parentTransaction.getId()));
							cached.remove(parentTransaction.getId());
							persistenceUtil.commitTransaction();
							persistenceUtil.beginTransaction();
							// remove branch from cache
							latest.remove(parentTransaction.getBranch().getName());
							
							parentTransaction.getBranch().setEnd(parentTransaction);
						}
					}
				}
				
				// ++++++ Update caches ++++++
				// remove old "latest transaction" from cache
				if (cached.containsKey(latest.get(revdep.getCommitBranch().getName()))) {
					cached.get(latest.get(revdep.getCommitBranch().getName())).addChild(rcsTransaction);
					
					// persistenceUtil.update(cached.remove(latest.get(revdep.getCommitBranch().getName())));
					cached.remove(latest.get(revdep.getCommitBranch().getName()));
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
				
				// add transaction to cache
				cached.put(rcsTransaction.getId(), rcsTransaction);
				
				// set new "latest transaction" to active branch cache
				latest.put(revdep.getCommitBranch().getName(), rcsTransaction.getId());
				// ------ Update caches ------
			}
		};
		
		new PostExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				cached.clear();
			}
		};
		
	}
	
}

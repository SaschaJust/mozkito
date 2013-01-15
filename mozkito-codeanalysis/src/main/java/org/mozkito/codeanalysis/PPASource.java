/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.codeanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class PPASource.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PPASource extends Source<ChangeSet> {
	
	/** The branch iterator. */
	private Iterator<Branch>    branchIterator   = null;
	
	/** The t iterator. */
	private Iterator<ChangeSet> tIterator        = null;
	
	/** The transaction limit. */
	private Set<String>         transactionLimit = null;
	
	private VersionArchive      versionArchive;
	
	/**
	 * Instantiates a new pPA source.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param persistenceUtil
	 *            the persistence util
	 * @param transactionLimit
	 *            the transaction limit
	 */
	public PPASource(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil,
	        final HashSet<String> transactionLimit) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<ChangeSet, ChangeSet>(this) {
			
			@Override
			public void preExecution() {
				
				if ((PPASource.this.transactionLimit != null) && (!PPASource.this.transactionLimit.isEmpty())) {
					PPASource.this.transactionLimit = transactionLimit;
				} else {
					
					PPASource.this.versionArchive = VersionArchive.loadVersionArchive(persistenceUtil);
					
					final List<Branch> branches = new LinkedList<Branch>();
					branches.add(PPASource.this.versionArchive.getMasterBranch());
					
					for (final Branch branch : PPASource.this.versionArchive.getBranches().values()) {
						if (!branch.isMasterBranch()) {
							branches.add(branch);
						}
					}
					PPASource.this.branchIterator = branches.iterator();
				}
			}
		};
		
		new ProcessHook<ChangeSet, ChangeSet>(this) {
			
			@Override
			public void process() {
				
				if (PPASource.this.transactionLimit != null) {
					// test cases
					if (PPASource.this.tIterator == null) {
						// initialize
						final Criteria<ChangeSet> criteria = persistenceUtil.createCriteria(ChangeSet.class);
						criteria.in("id", PPASource.this.transactionLimit);
						PPASource.this.tIterator = persistenceUtil.load(criteria).iterator();
					}
					if (PPASource.this.tIterator.hasNext()) {
						providePartialOutputData(PPASource.this.tIterator.next());
						if (!PPASource.this.tIterator.hasNext()) {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							setCompleted();
						}
					} else {
						if (Logger.logTrace()) {
							Logger.trace("Setting completed.");
						}
						setCompleted();
					}
				} else {
					// normal behavior
					if ((PPASource.this.tIterator == null) || (!PPASource.this.tIterator.hasNext())) {
						// load new transactions
						if (PPASource.this.branchIterator.hasNext()) {
							final Branch branch = PPASource.this.branchIterator.next();
							
							final RevDependencyGraph revDependencyGraph = PPASource.this.versionArchive.getRevDependencyGraph();
							final Iterable<String> branchTransactions = revDependencyGraph.getBranchTransactions(branch.getName());
							
							// FIXME here we have to set the tIterator
							
							if (Logger.logInfo()) {
								Logger.info("Processing RCSBRanch %s.", branch.toString());
							}
						} else {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							setCompleted();
						}
					}
					
					if (PPASource.this.tIterator.hasNext()) {
						
						final ChangeSet changeSet = PPASource.this.tIterator.next();
						
						// final int numRevision = transaction.getChangedFiles().size();
						// if (numRevision > 50) {
						// if (Logger.logWarn()) {
						// Logger.warn("Skipping transaction %s that touches more than %d > 50 files.",
						// transaction.getId(), numRevision);
						// }
						// }
						
						// test if seen already
						boolean skip = false;
						for (final Revision rCSRevision : changeSet.getRevisions()) {
							final Criteria<JavaChangeOperation> skipCriteria = persistenceUtil.createCriteria(JavaChangeOperation.class)
							                                                                  .eq("revision",
							                                                                      rCSRevision);
							if (!persistenceUtil.load(skipCriteria).isEmpty()) {
								skip = true;
								if (Logger.logDebug()) {
									Logger.debug("Skipping Transaction %s. This transaction was analyzed and persisted already.",
									             changeSet.getId());
								}
								break;
							}
						}
						if (skip) {
							if (Logger.logTrace()) {
								Logger.trace("Skipping output data.");
							}
							skipOutputData();
						} else {
							if (Logger.logTrace()) {
								Logger.trace("Providing partial output data.");
							}
							providePartialOutputData(changeSet);
						}
						if ((!PPASource.this.tIterator.hasNext()) && (!PPASource.this.branchIterator.hasNext())) {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							
							setCompleted();
						}
					} else {
						if (!PPASource.this.branchIterator.hasNext()) {
							if (Logger.logTrace()) {
								Logger.trace("Setting completed.");
							}
							setCompleted();
						} else {
							if (Logger.logTrace()) {
								Logger.trace("Skipping output data.");
							}
							skipOutputData();
						}
					}
					
				}
				
			}
		};
	}
}

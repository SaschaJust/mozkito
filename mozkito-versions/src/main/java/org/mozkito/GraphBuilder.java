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

import java.util.List;
import java.util.Set;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.IRevDependencyGraph;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.RCSBranch;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class GraphBuilder.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GraphBuilder implements Runnable {
	
	private static int                commitLimit = 15;
	
	/** The counter. */
	private final IRevDependencyGraph revDepGraph;
	private final PersistenceUtil     persistenceUtil;
	private final BranchFactory       branchFactory;
	
	/**
	 * Instantiates a new graph builder.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param repository
	 *            the repository
	 * @param persistenceUtil
	 *            the persistence util
	 * @param branchFactory
	 *            the branch factory
	 */
	@NoneNull
	public GraphBuilder(final Repository repository, final PersistenceUtil persistenceUtil) {
		this.revDepGraph = repository.getRevDependencyGraph();
		this.persistenceUtil = persistenceUtil;
		this.branchFactory = new BranchFactory(persistenceUtil);
	}
	
	/**
	 * Phase one: iterate over all transactions and set parents, tags, and branchHEADs
	 */
	public void phaseOne() {
		if (Logger.logInfo()) {
			Logger.info("Phase I: Computing and persisting transaction and branch dependencies ...");
		}
		this.persistenceUtil.beginTransaction();
		int counter = 0;
		for (final String hash : this.revDepGraph.getVertices()) {
			final RCSTransaction rcsTransaction = this.persistenceUtil.loadById(hash, RCSTransaction.class);
			
			if (rcsTransaction == null) {
				throw new UnrecoverableError("Could not load transaction " + hash + " from database.");
			}
			
			if (!this.revDepGraph.hasVertex(hash)) {
				throw new UnrecoverableError("RevDependencyGraph does not contain transaction " + hash);
			}
			
			// set parents
			final String branchParentHash = this.revDepGraph.getBranchParent(hash);
			if (branchParentHash != null) {
				final RCSTransaction branchParent = this.persistenceUtil.loadById(branchParentHash,
				                                                                  RCSTransaction.class);
				rcsTransaction.setBranchParent(branchParent);
			}
			final String mergeParentHash = this.revDepGraph.getMergeParent(hash);
			if (mergeParentHash != null) {
				final RCSTransaction mergeParent = this.persistenceUtil.loadById(mergeParentHash, RCSTransaction.class);
				rcsTransaction.setMergeParent(mergeParent);
			}
			
			// set tags
			final Set<String> tags = this.revDepGraph.getTags(hash);
			if (tags != null) {
				rcsTransaction.addAllTags(tags);
			}
			
			// persist branches
			final String branchName = this.revDepGraph.isBranchHead(hash);
			if (branchName != null) {
				final RCSBranch branch = this.branchFactory.getBranch(branchName);
				if (Logger.logDebug()) {
					Logger.debug("Adding branch " + branchName);
				}
				branch.setHead(rcsTransaction);
				this.persistenceUtil.saveOrUpdate(branch);
			}
			this.persistenceUtil.saveOrUpdate(rcsTransaction);
			if ((++counter % commitLimit) == 0) {
				this.persistenceUtil.commitTransaction();
				this.persistenceUtil.beginTransaction();
			}
		}
		this.persistenceUtil.commitTransaction();
		if (Logger.logInfo()) {
			Logger.info("done");
		}
	}
	
	/**
	 * Phase three: Setting mergedIn values within branches.
	 */
	public void phaseThree() {
		if (Logger.logInfo()) {
			Logger.info("Phase III: Determine and persisting branch merges ...");
		}
		
		int counter = 0;
		this.persistenceUtil.beginTransaction();
		for (final String hash : this.revDepGraph.getVertices()) {
			final String mergeParentHash = this.revDepGraph.getMergeParent(hash);
			if (mergeParentHash != null) {
				final RCSTransaction mergeParent = this.persistenceUtil.loadById(mergeParentHash, RCSTransaction.class);
				if (mergeParent == null) {
					throw new UnrecoverableError("Could not load transaction " + mergeParentHash + " from DB.");
				}
				final Set<String> branchNames = mergeParent.getBranchNames();
				if ((branchNames == null) || (branchNames.isEmpty())) {
					throw new UnrecoverableError("Branches of transaction " + mergeParentHash
					        + " are NULL or empty. Both is a fatal error.");
				}
				for (final String branchName : branchNames) {
					final RCSBranch branch = this.persistenceUtil.loadById(branchName, RCSBranch.class);
					branch.addMergedIn(hash);
					this.persistenceUtil.saveOrUpdate(branch);
					if ((++counter % commitLimit) == 0) {
						this.persistenceUtil.commitTransaction();
						this.persistenceUtil.beginTransaction();
					}
				}
			}
		}
		this.persistenceUtil.commitTransaction();
		
		if (Logger.logInfo()) {
			Logger.info("done");
		}
	}
	
	/**
	 * Phase two: Iterate over all branches and add branch references to transactions contained by branch and their
	 * branch index.
	 */
	public void phaseTwo() {
		if (Logger.logInfo()) {
			Logger.info("Phase II: Persisting branch transaction relationships ...");
		}
		
		final List<RCSBranch> branches = this.persistenceUtil.load(this.persistenceUtil.createCriteria(RCSBranch.class));
		
		if (branches.isEmpty()) {
			throw new UnrecoverableError("Could not load any transactions from DB. This is a fatal error!");
		}
		
		for (final RCSBranch branch : branches) {
			if (Logger.logDebug()) {
				Logger.debug("Handling branch " + branch.getName() + " with headId=" + branch.getHead().getId());
			}
			long index = 0l;
			this.persistenceUtil.beginTransaction();
			for (final String transactionId : this.revDepGraph.getBranchTransactions(branch.getName())) {
				final RCSTransaction transaction = this.persistenceUtil.loadById(transactionId, RCSTransaction.class);
				if (!transaction.addBranch(branch, index)) {
					throw new UnrecoverableError("Could not add branch index " + branch.getName() + " to transaction: "
					        + transaction.getId() + ". It appreas to be set before. Fatal error.");
				}
				--index;
				if ((index % commitLimit) == 0) {
					this.persistenceUtil.commitTransaction();
					this.persistenceUtil.beginTransaction();
				}
			}
			this.persistenceUtil.commitTransaction();
		}
		
		if (Logger.logInfo()) {
			Logger.info("done");
		}
	}
	
	@Override
	public void run() {
		phaseOne();
		phaseTwo();
		phaseThree();
		this.revDepGraph.close();
	}
}

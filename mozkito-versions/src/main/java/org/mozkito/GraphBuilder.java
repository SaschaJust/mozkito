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

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.Repository;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class GraphBuilder.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GraphBuilder implements Runnable {
	
	/** The Constant COMMIT_LIMIT. */
	private static final int         COMMIT_LIMIT = 15;
	
	/** The counter. */
	private final RevDependencyGraph revDepGraph;
	
	/** The persistence util. */
	private final PersistenceUtil    persistenceUtil;
	
	/** The branch factory. */
	private final BranchFactory      branchFactory;
	
	/**
	 * Instantiates a new graph builder.
	 * 
	 * @param repository
	 *            the repository
	 * @param persistenceUtil
	 *            the PersitenceUtil allowing DB connection
	 */
	@NoneNull
	public GraphBuilder(final Repository repository, final PersistenceUtil persistenceUtil) {
		try {
			this.revDepGraph = repository.getRevDependencyGraph();
		} catch (final RepositoryOperationException e) {
			throw new UnrecoverableError(e);
		}
		this.persistenceUtil = persistenceUtil;
		this.branchFactory = new BranchFactory(persistenceUtil);
	}
	
	/**
	 * Phase one: iterate over all transactions and set parents, tags, and branchHEADs.
	 */
	public void phaseOne() {
		if (Logger.logInfo()) {
			Logger.info("Phase I: Computing and persisting transaction and branch dependencies ...");
		}
		this.persistenceUtil.beginTransaction();
		int counter = 0;
		for (final String hash : this.revDepGraph.getVertices()) {
			final ChangeSet changeSet = this.persistenceUtil.loadById(hash, ChangeSet.class);
			
			if (changeSet == null) {
				throw new UnrecoverableError("Could not load transaction " + hash + " from database.");
			}
			
			if (!this.revDepGraph.existsVertex(hash)) {
				throw new UnrecoverableError("RevDependencyGraph does not contain transaction " + hash);
			}
			
			// set parents
			final String branchParentHash = this.revDepGraph.getBranchParent(hash);
			if (branchParentHash != null) {
				final ChangeSet branchParent = this.persistenceUtil.loadById(branchParentHash, ChangeSet.class);
				changeSet.setBranchParent(branchParent);
			}
			final String mergeParentHash = this.revDepGraph.getMergeParent(hash);
			if (mergeParentHash != null) {
				final ChangeSet mergeParent = this.persistenceUtil.loadById(mergeParentHash, ChangeSet.class);
				changeSet.setMergeParent(mergeParent);
			}
			
			// set tags
			final Set<String> tags = this.revDepGraph.getTags(hash);
			if (tags != null) {
				changeSet.addAllTags(tags);
			}
			
			// persist branches
			final String branchName = this.revDepGraph.isBranchHead(hash);
			if (branchName != null) {
				final Branch rCSBranch = this.branchFactory.getBranch(branchName);
				if (Logger.logDebug()) {
					Logger.debug("Adding branch " + branchName);
				}
				rCSBranch.setHead(changeSet);
				this.persistenceUtil.saveOrUpdate(rCSBranch);
			}
			this.persistenceUtil.saveOrUpdate(changeSet);
			if ((++counter % GraphBuilder.COMMIT_LIMIT) == 0) {
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
				final ChangeSet mergeParent = this.persistenceUtil.loadById(mergeParentHash, ChangeSet.class);
				if (mergeParent == null) {
					throw new UnrecoverableError("Could not load transaction " + mergeParentHash + " from DB.");
				}
				final Set<String> branchNames = mergeParent.getBranchNames();
				if ((branchNames == null) || (branchNames.isEmpty())) {
					throw new UnrecoverableError("Branches of transaction " + mergeParentHash
					        + " are NULL or empty. Both is a fatal error.");
				}
				for (final String branchName : branchNames) {
					final Branch rCSBranch = this.persistenceUtil.loadById(branchName, Branch.class);
					rCSBranch.addMergedIn(hash);
					this.persistenceUtil.saveOrUpdate(rCSBranch);
					if ((++counter % GraphBuilder.COMMIT_LIMIT) == 0) {
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
		
		final List<Branch> branches = this.persistenceUtil.load(this.persistenceUtil.createCriteria(Branch.class));
		
		if (branches.isEmpty()) {
			throw new UnrecoverableError("Could not load any transactions from DB. This is a fatal error!");
		}
		
		for (final Branch rCSBranch : branches) {
			if (Logger.logDebug()) {
				Logger.debug("Handling branch " + rCSBranch.getName() + " with headId=" + rCSBranch.getHead().getId());
			}
			long index = 0l;
			this.persistenceUtil.beginTransaction();
			for (final String transactionId : this.revDepGraph.getBranchTransactions(rCSBranch.getName())) {
				final ChangeSet changeSet = this.persistenceUtil.loadById(transactionId, ChangeSet.class);
				if (!changeSet.addBranch(rCSBranch, index)) {
					throw new UnrecoverableError("Could not add branch index " + rCSBranch.getName()
					        + " to transaction: " + changeSet.getId()
					        + ". It appreas to be set before. Fatal error.");
				}
				--index;
				if ((index % GraphBuilder.COMMIT_LIMIT) == 0) {
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		phaseOne();
		phaseTwo();
		phaseThree();
		this.revDepGraph.close();
	}
}

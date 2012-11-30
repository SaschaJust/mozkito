/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package org.mozkito.versions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.Branch;

/**
 * A factory for creating Branch objects.
 */
public class BranchFactory {
	
	/** The branch cache. */
	private final Map<String, Branch> branchCache = new HashMap<String, Branch>();
	
	/** The persistence util. */
	private final PersistenceUtil        persistenceUtil;
	
	/**
	 * Instantiates a new branch factory.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public BranchFactory(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
	
	/**
	 * Gets the branch.
	 * 
	 * @param name
	 *            the name
	 * @return the branch
	 */
	public synchronized Branch getBranch(@NotNull final String name) {
		if (!this.branchCache.containsKey(name)) {
			
			if (this.persistenceUtil == null) {
				
				// create new branch and cache
				final Branch newBranch = new Branch(name);
				if (Logger.logDebug()) {
					Logger.debug("Creating new Branch " + newBranch.toString());
				}
				this.branchCache.put(name, newBranch);
			} else {
				// We could get a valid persistence util.
				// The existed no previous persistence util: try to load
				// persisted
				// MASTER_BRANCH
				final Criteria<Branch> criteria = this.persistenceUtil.createCriteria(Branch.class).eq("name",
				                                                                                             name);
				final List<Branch> loadedBranches = this.persistenceUtil.load(criteria);
				if (loadedBranches.isEmpty()) {
					// We could not load a persisted MASTER_BRANCH. So, create a
					// new one and return.
					if (Logger.logDebug()) {
						Logger.debug("Attempt to load persisted RCSBranch with name " + name
						        + " from existing database connection failed: "
						        + this.persistenceUtil.getToolInformation() + " using criteria " + criteria.toString());
					}
					final Branch newBranch = new Branch(name);
					if (Logger.logDebug()) {
						Logger.debug("Creating new Branch " + newBranch.toString());
					}
					this.persistenceUtil.save(newBranch);
					this.branchCache.put(name, newBranch);
				} else {
					this.branchCache.put(name, loadedBranches.get(0));
				}
			}
		}
		return this.branchCache.get(name);
	}
	
	/**
	 * Gets the master branch.
	 * 
	 * @return the master branch
	 */
	public Branch getMasterBranch() {
		return getBranch(Branch.MASTER_BRANCH_NAME);
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistence util
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
}

package de.unisaarland.cs.st.moskito.rcs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;

public class BranchFactory {
	
	private static Map<String, RCSBranch> branchCache = new HashMap<String, RCSBranch>();
	
	public synchronized static RCSBranch getBranch(String name) {
		
		if (!branchCache.containsKey(name)) {
			
			PersistenceUtil persistenceUtil = null;
			try {
				persistenceUtil = PersistenceManager.getUtil();
			} catch (UninitializedDatabaseException e) {
				if (Logger.logWarn()) {
					Logger.warn("BranchFactory could not connect to database. Creating new branches if required.");
				}
			}
			
			// If we could no load persistenceUtil
			if (persistenceUtil == null) {
				
				//create new branch and cache
				RCSBranch newBranch = new RCSBranch(name);
				if (Logger.logDebug()) {
					Logger.debug("Creating new Branch " + newBranch.toString());
				}
				branchCache.put(name, newBranch);
			} else {
				// We could get a valid persistence util.
				// The existed no previous persistence util: try to load persisted
				// MASTER_BRANCH
				Criteria<RCSBranch> criteria = persistenceUtil.createCriteria(RCSBranch.class).eq("name",
						RCSBranch.MASTER_BRANCH_NAME);
				List<RCSBranch> loadedBranches = persistenceUtil.load(criteria);
				if (loadedBranches.isEmpty()) {
					// We could not load a persisted MASTER_BRANCH. So, create a
					// new one and return.
					if (Logger.logDebug()) {
						Logger.debug("Attempt to lead persisted RCSBranch with name " + name
								+ " from existing database connection failed. " + "No persisted master branch found. "
								+ "Returning new RCSBranch.MASTER_BRANCH.");
					}
					RCSBranch newBranch = new RCSBranch(name);
					if (Logger.logDebug()) {
						Logger.debug("Creating new Branch " + newBranch.toString());
					}
					branchCache.put(name, newBranch);
				} else {
					branchCache.put(name, loadedBranches.get(0));
				}
			}
		}
		return branchCache.get(name);
	}
	
	public static RCSBranch getMasterBranch() {
		return getBranch(RCSBranch.MASTER_BRANCH_NAME);
	}
}

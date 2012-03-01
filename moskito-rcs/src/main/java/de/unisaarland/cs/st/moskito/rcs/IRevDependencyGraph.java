package de.unisaarland.cs.st.moskito.rcs;

import java.util.Set;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

public interface IRevDependencyGraph {
	
	boolean createFromRepository();
	
	String getBranchParent(String hash);
	
	String getMergeParent(String hash);
	
	Set<String> getTags(String hash);
	
	boolean hasVertex(final String hash);
	
	String isBranchHead(String hash);
	
	boolean readFromDB(PersistenceUtil persistenceUtil);
	
}

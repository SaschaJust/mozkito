package de.unisaarland.cs.st.moskito.rcs;

import java.util.Set;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * The Interface IRevDependencyGraph.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface IRevDependencyGraph {
	
	/**
	 * Creates the from repository.
	 * 
	 * @return true, if successful
	 */
	boolean createFromRepository();
	
	boolean existsPath(String fromHash,
	                   String toHash);
	
	/**
	 * Gets the branch parent.
	 * 
	 * @param hash
	 *            the hash
	 * @return the branch parent
	 */
	String getBranchParent(String hash);
	
	Iterable<String> getBranchTransactions(String branchName);
	
	/**
	 * Gets the merge parent.
	 * 
	 * @param hash
	 *            the hash
	 * @return the merge parent
	 */
	String getMergeParent(String hash);
	
	Iterable<String> getPreviousTransactions(String hash);
	
	/**
	 * Gets the tags.
	 * 
	 * @param hash
	 *            the hash
	 * @return the tags
	 */
	Set<String> getTags(String hash);
	
	/**
	 * Gets the vertices.
	 * 
	 * @return the vertices
	 */
	Iterable<String> getVertices();
	
	/**
	 * Checks for vertex.
	 * 
	 * @param hash
	 *            the hash
	 * @return true, if successful
	 */
	boolean hasVertex(final String hash);
	
	/**
	 * Checks if is branch head.
	 * 
	 * @param hash
	 *            the hash
	 * @return the string
	 */
	String isBranchHead(String hash);
	
	/**
	 * Read from db.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return true, if successful
	 */
	boolean readFromDB(PersistenceUtil persistenceUtil);
	
}

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

import java.util.Set;

import org.mozkito.persistence.PersistenceUtil;

/**
 * The Interface IRevDependencyGraph.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public interface IRevDependencyGraph {
	
	/**
	 * Close.
	 */
	void close();
	
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

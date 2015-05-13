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
package org.mozkito.versions.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.datastructures.BidirectionalMultiMap;
import org.mozkito.versions.elements.ChangeSetIterator.ChangeSetOrder;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Interface IRevDependencyGraph.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class RevDependencyGraph {
	
	/**
	 * Possible edge labels.
	 */
	public enum EdgeType {
		
		/** The merge. */
		MERGE_EDGE,
		/** The branch. */
		BRANCH_EDGE,
		
		/** The branch head. */
		BRANCH_HEAD;
	}
	
	/**
	 * The Class PathCache.
	 */
	static final class PathCache {
		
		/** The exists. */
		private final Set<String> exists    = new HashSet<>();
		
		/** The not exists. */
		private final Set<String> notExists = new HashSet<>();
		
		/**
		 * Adds the tuple to the cache.
		 *
		 * @param fromHash
		 *            the from hash
		 * @param toHash
		 *            the to hash
		 * @param exists
		 *            the exists
		 * @return true, if successful
		 */
		public boolean add(final String fromHash,
		                   final String toHash,
		                   final boolean exists) {
			final String composite = fromHash + toHash;
			return exists
			             ? this.exists.add(composite)
			             : this.notExists.add(composite);
		}
		
		/**
		 * If the path is in the cache, returns whether there exists a path or not. If the tuple is not in the cache,
		 * returns null.
		 *
		 * @param fromHash
		 *            the from hash
		 * @param toHash
		 *            the to hash
		 * @return the boolean
		 */
		public Boolean exists(final String fromHash,
		                      final String toHash) {
			final String composite = fromHash + toHash;
			if (this.exists.contains(composite)) {
				return true;
			} else if (this.notExists.contains(composite)) {
				return false;
			} else {
				return null;
			}
		}
		
	}
	
	/**
	 * The Class RevDepEdge.
	 */
	class RevDepEdge implements Comparable<RevDepEdge> {
		
		/** The source. */
		private final String   source;
		
		/** The target. */
		private final String   target;
		
		/** The type. */
		private final EdgeType type;
		
		/** The edge id. */
		private final int      edgeId;
		
		/**
		 * Instantiates a new rev dep edge.
		 *
		 * @param source
		 *            the source
		 * @param target
		 *            the target
		 * @param type
		 *            the type
		 */
		public RevDepEdge(final String source, final String target, final EdgeType type) {
			Condition.check(!source.equals(target), "Edges must not point from a vertex to the vertex itself.");
			this.source = source;
			this.target = target;
			this.type = type;
			this.edgeId = RevDependencyGraph.edgeIdCounter++;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final RevDepEdge o) {
			if (this.edgeId > o.edgeId) {
				return -1;
			} else if (this.edgeId < o.edgeId) {
				return 1;
			} else {
				return 0;
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final RevDepEdge other = (RevDepEdge) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.source == null) {
				if (other.source != null) {
					return false;
				}
			} else if (!this.source.equals(other.source)) {
				return false;
			}
			if (this.target == null) {
				if (other.target != null) {
					return false;
				}
			} else if (!this.target.equals(other.target)) {
				return false;
			}
			if (this.type != other.type) {
				return false;
			}
			return true;
		}
		
		/**
		 * Gets the edge type.
		 *
		 * @return the edge type
		 */
		public EdgeType getEdgeType() {
			return this.type;
		}
		
		/**
		 * Gets the outer type.
		 *
		 * @return the outer type
		 */
		private RevDependencyGraph getOuterType() {
			return RevDependencyGraph.this;
		}
		
		/**
		 * Gets the source.
		 *
		 * @return the source
		 */
		public String getSource() {
			return this.source;
		}
		
		/**
		 * Gets the target.
		 *
		 * @return the target
		 */
		public String getTarget() {
			return this.target;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result) + ((this.source == null)
			                                                  ? 0
			                                                  : this.source.hashCode());
			result = (prime * result) + ((this.target == null)
			                                                  ? 0
			                                                  : this.target.hashCode());
			result = (prime * result) + ((this.type == null)
			                                                ? 0
			                                                : this.type.hashCode());
			return result;
		}
		
	}
	
	/** The edge id counter. */
	private static int                                   edgeIdCounter  = 0;
	
	/** The graph. */
	private final AbstractTypedGraph<String, RevDepEdge> graph;
	
	/** The branch heads. */
	private final Map<String, String>                    branchHeads    = new HashMap<>();
	
	/** The tags. */
	private final BidirectionalMultiMap<String, String>  tags           = new BidirectionalMultiMap<String, String>();
	
	/** The path check cache. */
	private final PathCache                              pathCheckCache = new PathCache();
	
	/**
	 * Create a new RevDependencyGraph based on an underlying GraphDB.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@NoneNull
	public RevDependencyGraph() throws IOException {
		this.graph = new DirectedSparseGraph<>();
	}
	
	/**
	 * Adds the branch with the specified change set as branch head. Returns true if the branch was successfully added
	 * and did not exist before. Returns false otherwise and leaves the data structure unchanged.
	 * 
	 * @param branchName
	 *            the branch name
	 * @param branchHead
	 *            the branch head
	 * @return true, if successful
	 */
	@NoneNull
	public boolean addBranch(@NotEmptyString final String branchName,
	                         final String branchHead) {
		// PRECONDITIONS
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Adding branch with name %s and branch head %s.", branchName, branchHead);
			}
			if (this.branchHeads.containsKey(branchName)) {
				return false;
			}
			this.branchHeads.put(branchName, branchHead);
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adding a change set. Returns true if the underlying data structure was changed. False otherwise.
	 * 
	 * @param v
	 *            the v
	 * @return the vertex
	 */
	@NoneNull
	public boolean addChangeSet(@NotEmptyString final String v) {
		// PRECONDITIONS
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Adding change set node %s to graph.", v);
			}
			if (existsVertex(v)) {
				if (Logger.logDebug()) {
					Logger.debug("Change set node with id `" + v + "` already exists");
				}
				return false;
			}
			return this.graph.addVertex(v);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adds and edge between parent and child using the EdgeType label provided.
	 * 
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 * @param edgeType
	 *            the edge type
	 * @return true, if successful
	 */
	@NoneNull
	public boolean addEdge(@NotEmptyString final String parent,
	                       @NotEmptyString final String child,
	                       final EdgeType edgeType) {
		// PRECONDITIONS
		
		try {
			if (!existsVertex(child)) {
				addChangeSet(child);
			}
			if (!existsVertex(parent)) {
				addChangeSet(parent);
			}
			
			if (containsEdge(child, parent)) {
				if (Logger.logWarn()) {
					Logger.warn("An edge between " + child + " <-- " + parent + " already exists.");
				}
				return false;
			}
			
			final RevDepEdge edge = new RevDepEdge(parent, child, edgeType);
			if (!this.graph.addEdge(edge, new Pair<String>(parent, child),
			                        edu.uci.ics.jung.graph.util.EdgeType.DIRECTED)) {
				if (Logger.logWarn()) {
					Logger.warn("An edge between " + child + " <-- " + parent + " could not be added.");
				}
				return false;
			}
			
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adding a tag for a particular change set hash. Returns false if tag could not be added. See log messages for
	 * reason.
	 * 
	 * @param tagName
	 *            the tag name
	 * @param changeSet
	 *            the change set
	 * @return true, if successful
	 */
	@NoneNull
	public boolean addTag(@NotEmptyString final String tagName,
	                      @NotEmptyString final String changeSet) {
		// PRECONDITIONS
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Adding tag with name %s for change set %s.", tagName, changeSet);
			}
			if (this.tags.containsTo(tagName)) {
				return false;
			}
			this.tags.put(changeSet, tagName);
			return true;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Contains edge.
	 * 
	 * @param node
	 *            the node
	 * @param parent
	 *            the parent
	 * @return true, if successful
	 */
	@NoneNull
	public boolean containsEdge(@NotEmptyString final String node,
	                            @NotEmptyString final String parent) {
		final EdgeType result = getEdge(node, parent);
		return result != null;
	}
	
	/**
	 * Returns true if a branch with the provided name exists.
	 * 
	 * @param branchName
	 *            the branch name
	 * @return true, if successful
	 */
	@NoneNull
	public boolean existsBranch(@NotEmptyString final String branchName) {
		return this.branchHeads.containsKey(branchName);
	}
	
	/**
	 * Return true if there exists a path from fromHash to toHash.
	 * 
	 * @param fromHash
	 *            the from hash
	 * @param toHash
	 *            the to hash
	 * @return true, if successful
	 */
	@NoneNull
	public boolean existsPath(@NotEmptyString final String fromHash,
	                          @NotEmptyString final String toHash) {
		
		if (fromHash.equals(toHash)) {
			return true;
		}
		
		Boolean pathExists = this.pathCheckCache.exists(fromHash, toHash);
		
		if (pathExists == null) {
			if (Logger.logDebug()) {
				Logger.debug("RevDependencyGraph: checking for path between %s and %s", fromHash, toHash);
			}
			
			final UnweightedShortestPath<String, RevDepEdge> path = new UnweightedShortestPath<>(this.graph);
			pathExists = path.getDistance(fromHash, toHash) != null;
			this.pathCheckCache.add(fromHash, toHash, pathExists);
		}
		
		return pathExists;
	}
	
	/**
	 * Returns true if a transaction with hash exists.
	 * 
	 * @param hash
	 *            the hash
	 * @return true, if successful
	 */
	@NoneNull
	public boolean existsVertex(@NotEmptyString final String hash) {
		return this.graph.containsVertex(hash);
	}
	
	/**
	 * Returns the names of all containing branches.
	 * 
	 * @return the branches
	 */
	public Set<String> getBranches() {
		return this.branchHeads.keySet();
	}
	
	/**
	 * Gets the branch head.
	 * 
	 * @param branchName
	 *            the branch name
	 * @return the branch head
	 */
	@NoneNull
	private String getBranchHead(@NotEmptyString final String branchName) {
		return this.branchHeads.get(branchName);
	}
	
	/**
	 * Returns the names of all containing branches.
	 * 
	 * @return the branches
	 */
	private Set<String> getBranchHeads() {
		final Set<String> branchHeads = new HashSet<>();
		for (final String branchName : getBranches()) {
			final String branchHead = getBranchHead(branchName);
			if (branchHead != null) {
				branchHeads.add(branchHead);
			}
		}
		return branchHeads;
	}
	
	/**
	 * Gets the branch parent of the transaction with the provided hash.
	 * 
	 * @param hash
	 *            the hash
	 * @return the branch parent is exists, return null otherwise.
	 */
	@NoneNull
	public String getBranchParent(@NotEmptyString final String hash) {
		
		final Collection<RevDepEdge> inEdges = this.graph.getInEdges(hash);
		for (final RevDepEdge inEdge : inEdges) {
			if (inEdge.getEdgeType().equals(EdgeType.BRANCH_EDGE)) {
				return inEdge.getSource();
			}
		}
		return null;
	}
	
	/**
	 * Gets the transaction hashes in the specified branch (in top-down order).
	 * 
	 * @param branchName
	 *            the branch name
	 * @return the branch transactions
	 */
	@NoneNull
	public Iterable<String> getBranchTransactions(@NotEmptyString final String branchName) {
		final String branchHead = getBranchHead(branchName);
		if (branchHead == null) {
			if (Logger.logWarn()) {
				Logger.warn("Returning empty branch transaction iterator.");
			}
			return new ArrayList<String>(0);
		}
		return new RevDepIterator(branchHead, this);
	}
	
	/**
	 * Gets the change sets in the specified branch (in top-down order).
	 * 
	 * @param branchName
	 *            the branch name
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the branch transactions
	 */
	@NoneNull
	public Iterable<ChangeSet> getBranchTransactions(@NotEmptyString final String branchName,
	                                                 @NotNull final PersistenceUtil persistenceUtil) {
		final String branchHead = getBranchHead(branchName);
		if (branchHead == null) {
			if (Logger.logWarn()) {
				Logger.warn("Returning empty branch transaction iterator.");
			}
			return new ArrayList<ChangeSet>(0);
		}
		return ChangeSetIterator.fromRevDepIterator(persistenceUtil, new RevDepIterator(branchHead, this));
	}
	
	/**
	 * Gets the change sets in the specified branch (in top-down order).
	 * 
	 * @param branchName
	 *            the branch name
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the branch transactions
	 */
	@NoneNull
	public Iterable<ChangeSet> getBranchTransactionsASC(@NotEmptyString final String branchName,
	                                                    @NotNull final PersistenceUtil persistenceUtil) {
		final String branchHead = getBranchHead(branchName);
		if (branchHead == null) {
			if (Logger.logWarn()) {
				Logger.warn("Returning empty branch transaction iterator.");
			}
			return new ArrayList<ChangeSet>(0);
		}
		return ChangeSetIterator.fromRevDepIterator(persistenceUtil, new RevDepIterator(branchHead, this),
		                                            ChangeSetOrder.ASC);
	}
	
	/**
	 * Gets the edge.
	 * 
	 * @param node
	 *            the node
	 * @param parent
	 *            the parent
	 * @return the edge
	 */
	@NoneNull
	private EdgeType getEdge(@NotEmptyString final String node,
	                         @NotEmptyString final String parent) {
		
		final Collection<RevDepEdge> inEdges = this.graph.getInEdges(node);
		for (final RevDepEdge edge : inEdges) {
			if (edge.getSource().equals(parent)) {
				return edge.getEdgeType();
			}
		}
		return null;
	}
	
	/**
	 * Returns the hash of the ChangeSet that tag with the provided tag name points to. Returns null if no such tag
	 * exists or if any other error occurs.
	 * 
	 * @param tagName
	 *            the tag name
	 * @return the hash for tag
	 */
	@NoneNull
	public String getHashForTag(@NotEmptyString final String tagName) {
		final Set<String> hashes = this.tags.getFroms(tagName);
		if (hashes.isEmpty()) {
			return null;
		}
		if (hashes.size() > 1) {
			if (Logger.logError()) {
				Logger.error("A tag must point to exactly ONE revision, but pointed to %s",
				             JavaUtils.collectionToString(hashes));
			}
			return null;
		}
		return hashes.iterator().next();
	}
	
	/**
	 * Returns the merge parent hash of the transaction with the provided hash. Return null if no such merge parent
	 * exists.
	 * 
	 * @param hash
	 *            the hash
	 * @return the merge parent
	 */
	@NoneNull
	public List<String> getMergeParents(@NotEmptyString final String hash) {
		
		final TreeSet<RevDepEdge> inEdges = new TreeSet<>(this.graph.getInEdges(hash));
		final List<String> result = new LinkedList<>();
		for (final RevDepEdge inEdge : inEdges) {
			if (inEdge.getEdgeType().equals(EdgeType.MERGE_EDGE)) {
				result.add(inEdge.getSource());
			}
		}
		return result;
	}
	
	/**
	 * Return a list of hashed of those transactions applied before the transaction corresponding to the provided hash
	 * (top-down order). Returns an empty list if no previous transaction exist or if the transaction hash is unknown.
	 * 
	 * @param hash
	 *            the hash
	 * @return the previous change set hashes
	 */
	@NoneNull
	public Iterable<String> getPreviousTransactions(@NotEmptyString final String hash) {
		final RevDepIterator iter = new RevDepIterator(hash, this);
		if (iter.hasNext()) {
			iter.next();
			return iter;
		}
		return new ArrayList<String>(0);
	}
	
	/**
	 * Return a list of change sets applied before the change set corresponding to the provided hash (top-down order).
	 * The database instances will be fetched using the provided persistenceUtil. Returns an empty list if no previous
	 * transaction exist or if the transaction hash is unknown.
	 * 
	 * @param hash
	 *            the hash
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the previous change sets
	 */
	@NoneNull
	public Iterable<ChangeSet> getPreviousTransactions(@NotEmptyString final String hash,
	                                                   @NotNull final PersistenceUtil persistenceUtil) {
		final RevDepIterator iter = new RevDepIterator(hash, this);
		if (iter.hasNext()) {
			iter.next();
			return ChangeSetIterator.fromRevDepIterator(persistenceUtil, iter);
		}
		return new ArrayList<ChangeSet>(0);
	}
	
	/**
	 * Return a list of change sets applied before the change set corresponding to the provided hash (top-down order).
	 * The database instances will be fetched using the provided persistenceUtil. Returns an empty list if no previous
	 * transaction exist or if the transaction hash is unknown.
	 * 
	 * @param hash
	 *            the hash
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the previous change sets
	 */
	@NoneNull
	public Iterable<ChangeSet> getPreviousTransactionsASC(@NotEmptyString final String hash,
	                                                      @NotNull final PersistenceUtil persistenceUtil) {
		final RevDepIterator iter = new RevDepIterator(hash, this);
		if (iter.hasNext()) {
			iter.next();
			return ChangeSetIterator.fromRevDepIterator(persistenceUtil, iter, ChangeSetOrder.ASC);
		}
		return new ArrayList<ChangeSet>(0);
	}
	
	/**
	 * Gets the tags.
	 * 
	 * @param hash
	 *            the hash
	 * @return the tags
	 */
	public Set<String> getTags(final String hash) {
		final Set<String> tagNames = this.tags.getTos(hash);
		if (tagNames == null) {
			return new HashSet<String>();
		}
		return tagNames;
	}
	
	/**
	 * Returns all transaction hashes. No specified order
	 * 
	 * @return the vertices
	 */
	public Iterable<String> getVertices() {
		return this.graph.getVertices();
	}
	
	/**
	 * Returns the name of the branch the transaction corresponding to provided hash is the last transaction in that
	 * branch. Returns null if the transaction is no branch head for all branches.
	 * 
	 * @param hash
	 *            the hash
	 * @return the string
	 */
	@NoneNull
	public String isBranchHead(@NotEmptyString final String hash) {
		for (final Entry<String, String> entry : this.branchHeads.entrySet()) {
			if (entry.getValue().equals(hash)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	/**
	 * Checks if this RevDependencyGraph contains the same RevDepdendecy structure as the provided other
	 * RevDependencyGraph.
	 * 
	 * @param other
	 *            the other
	 * @return true, if is equals to
	 */
	public boolean isEqualsTo(final RevDependencyGraph other) {
		if (!CollectionUtils.isEqualCollection(this.tags.fromKeySet(), other.tags.fromKeySet())) {
			return false;
		}
		for (final String key : this.tags.fromKeySet()) {
			if (!CollectionUtils.isEqualCollection(this.tags.getTos(key), other.tags.getTos(key))) {
				return false;
			}
		}
		final Set<String> thisBranchHeads = getBranchHeads();
		final Set<String> otherBranchHeads = other.getBranchHeads();
		
		if (!CollectionUtils.isEqualCollection(thisBranchHeads, otherBranchHeads)) {
			return false;
		}
		for (final String branchHead : thisBranchHeads) {
			final Iterator<String> thisIter = getPreviousTransactions(branchHead).iterator();
			final Iterator<String> otherIter = other.getPreviousTransactions(branchHead).iterator();
			while (thisIter.hasNext()) {
				if (!otherIter.hasNext()) {
					return false;
				}
				if (!thisIter.next().equals(otherIter.next())) {
					return false;
				}
			}
			if (otherIter.hasNext()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Removes a ChangeSet from the underlying data structure.
	 * 
	 * @param hash
	 *            the hash
	 */
	@NoneNull
	public void removeChangeSet(@NotEmptyString final String hash) {
		if (Logger.logDebug()) {
			Logger.debug("Removing change set node %s from graph.", hash);
		}
		this.graph.removeVertex(hash);
	}
	
	/**
	 * Removes a tag.
	 * 
	 * @param tagName
	 *            the tag name
	 */
	@NoneNull
	public void removeTag(@NotEmptyString final String tagName) {
		if (this.tags.containsTo(tagName)) {
			this.tags.removeTo(tagName);
		}
	}
}

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
package org.mozkito.versions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.StringCondition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.mozkito.datastructures.BidirectionalMultiMap;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;

/**
 * The Interface IRevDependencyGraph.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class RevDependencyGraph {
	
	/**
	 * Possible edge labels
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
	 * Possible vertex types
	 */
	private enum NodeType {
		
		/** The change set. */
		CHANGE_SET,
		/** The branch. */
		BRANCH;
	}
	
	/** The Constant NODE_ID. */
	private static final String                         NODE_ID   = "revhash";
	
	/** The Constant BRANCH. */
	private static final String                         BRANCH_ID = "branch_name";
	
	private final BidirectionalMultiMap<String, String> tags      = new BidirectionalMultiMap<String, String>();
	
	/** The Constant NODE_TYPE. */
	private static final String                         NODE_TYPE = "type";
	
	/** The graph. */
	private final TitanGraph                            graph;
	
	private final File                                  dbFile;
	
	/**
	 * Create a new RevDependencyGraph based on an underlying GraphDB.
	 * 
	 */
	@NoneNull
	public RevDependencyGraph() {
		this.dbFile = FileUtils.createRandomDir("mozkito_", "rev_dep_graph_db", FileShutdownAction.DELETE);
		this.graph = TitanFactory.open(this.dbFile.getAbsolutePath());
		this.graph.createKeyIndex(NODE_ID, Vertex.class);
		this.graph.createKeyIndex(BRANCH_ID, Vertex.class);
	}
	
	/**
	 * Adds the branch with the specified change set as branch head.
	 * 
	 * @param branchName
	 *            the branch name
	 * @param branchHead
	 *            the branch head
	 * @return the vertex
	 */
	@NoneNull
	public Vertex addBranch(@NotEmptyString final String branchName,
	                        final String branchHead) {
		// PRECONDITIONS
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Adding branch with name %s and branch head %s.", branchName, branchHead);
			}
			final Vertex branchHeadVertex = addChangeSet(branchHead);
			if (branchHeadVertex == null) {
				if (Logger.logError()) {
					Logger.error("Trying to add %s as branch head but no vertex with this ID exists.", branchHead);
				}
				return null;
			}
			final Vertex branchVertex = getBranch(branchName);
			if (branchVertex != null) {
				return branchVertex;
			}
			final TitanTransaction titanTransaction = this.graph.startTransaction();
			final Vertex vertex = this.graph.addVertex(null);
			vertex.setProperty(BRANCH_ID, branchName);
			vertex.setProperty(NODE_TYPE, NodeType.BRANCH);
			this.graph.addEdge(null, vertex, branchHeadVertex, EdgeType.BRANCH_HEAD.toString());
			titanTransaction.stopTransaction(Conclusion.SUCCESS);
			return vertex;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adding a change set. Returns the vertex that was newly added or an existing vertex if the change set was added
	 * before. Return null if the vertex could not be added.
	 * 
	 * @param v
	 *            the v
	 * @return the vertex
	 */
	@NoneNull
	public Vertex addChangeSet(@NotEmptyString final String v) {
		// PRECONDITIONS
		
		try {
			if (Logger.logDebug()) {
				Logger.debug("Adding change set node %s to graph.", v);
			}
			if (existsVertex(v)) {
				if (Logger.logDebug()) {
					Logger.debug("Change set node with id `" + v + "` already exists");
				}
				return getChangeSet(v);
			}
			final TitanTransaction titanTransaction = this.graph.startTransaction();
			final Vertex vertex = this.graph.addVertex(null);
			vertex.setProperty(NODE_ID, v);
			vertex.setProperty(NODE_TYPE, NodeType.CHANGE_SET);
			titanTransaction.stopTransaction(Conclusion.SUCCESS);
			return vertex;
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
				if (Logger.logError()) {
					Logger.error("An edge between " + child + " <-- " + parent + " already exists.");
				}
				return false;
			}
			
			final Vertex parentNode = getChangeSet(parent);
			final Vertex childNode = getChangeSet(child);
			final TitanTransaction titanTransaction = this.graph.startTransaction();
			final Edge newEdge = this.graph.addEdge(null, parentNode, childNode, edgeType.toString());
			if (newEdge == null) {
				this.graph.stopTransaction(Conclusion.FAILURE);
				return false;
			}
			titanTransaction.stopTransaction(Conclusion.SUCCESS);
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
			final Vertex changeSetVertex = addChangeSet(changeSet);
			if (changeSetVertex == null) {
				if (Logger.logError()) {
					Logger.error("Trying to add tag %s for not existing change set %s.", tagName, changeSet);
				}
				return false;
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
	 * Closes the underlying graph DB layer.
	 */
	public void close() {
		try {
			this.graph.shutdown();
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
		return getBranch(branchName) != null;
	}
	
	/**
	 * Return true if there exists a path from fromHash to toHash
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
		
		final GremlinPipeline<Object, Vertex> pipe = new GremlinPipeline<>(getChangeSet(fromHash)).out(EdgeType.BRANCH_EDGE.toString(),
		                                                                                               EdgeType.MERGE_EDGE.toString())
		                                                                                          .loop(1,
		                                                                                                new PipeFunction<LoopBundle<Vertex>, Boolean>() {
			                                                                                                
			                                                                                                @Override
			                                                                                                public Boolean compute(final LoopBundle<Vertex> argument) {
				                                                                                                return !toHash.equals(argument.getObject()
				                                                                                                                              .getProperty(NODE_ID)
				                                                                                                                              .toString());
			                                                                                                }
		                                                                                                })
		                                                                                          .filter(new PipeFunction<Vertex, Boolean>() {
			                                                                                                  
			                                                                                                  @Override
			                                                                                                  public Boolean compute(final Vertex argument) {
				                                                                                                  return toHash.equals(argument.getProperty(NODE_ID)
				                                                                                                                               .toString());
			                                                                                                  }
		                                                                                                  });
		for (final Object hit : pipe) {
			StringCondition.equals(toHash, ((Vertex) hit).getProperty(NODE_ID).toString(),
			                       "Path must end with specified toHash.");
			return true;
		}
		// final Query query = from.query().direction(Direction.OUT).has(NODE_ID, toHash);
		// final Iterable<Vertex> vertices = query.vertices();
		return false;
		// vertices.iterator().hasNext();
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
		return (getChangeSet(hash) != null);
	}
	
	@NoneNull
	private Vertex getBranch(@NotEmptyString final String nodeID) {
		return getNode(BRANCH_ID, nodeID);
	}
	
	/**
	 * Returns the names of all containing branches.
	 * 
	 * @return the branches
	 */
	public Set<String> getBranches() {
		final Set<String> result = new HashSet<String>();
		for (final Vertex node : this.graph.getVertices()) {
			final Object property = node.getProperty(RevDependencyGraph.BRANCH_ID);
			if (property != null) {
				result.add(property.toString());
			}
		}
		return result;
	}
	
	@NoneNull
	private Vertex getBranchHead(@NotEmptyString final String branchName) {
		final Vertex branchVertex = getBranch(branchName);
		if (branchVertex == null) {
			if (Logger.logWarn()) {
				Logger.warn("Cannot find branch %s.");
			}
			return null;
		}
		final Iterator<Vertex> iter = branchVertex.query().direction(Direction.OUT)
		                                          .labels(EdgeType.BRANCH_HEAD.toString()).vertices().iterator();
		Vertex result = null;
		while (iter.hasNext()) {
			result = iter.next();
			if (iter.hasNext()) {
				throw new UnrecoverableError(
				                             "Found more that one branch head for branch %s. This indicates data inconsistency!");
			}
		}
		return result;
	}
	
	/**
	 * Returns the names of all containing branches.
	 * 
	 * @return the branches
	 */
	private Set<String> getBranchHeads() {
		final Set<String> branchHeads = new HashSet<>();
		final Set<String> branches = getBranches();
		for (final String branchName : branches) {
			final Vertex branchHead = getBranchHead(branchName);
			if (branchHead != null) {
				branchHeads.add(branchHead.getProperty(NODE_ID).toString());
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
		final Vertex node = getChangeSet(hash);
		if (node == null) {
			throw new UnrecoverableError("Requsting branch parent of node not contained by GitRevDependencyGraph.");
		}
		int counter = 0;
		String result = null;
		for (final Edge relation : node.getEdges(Direction.IN, EdgeType.BRANCH_EDGE.toString())) {
			if (result == null) {
				result = relation.getVertex(Direction.OUT).getProperty(RevDependencyGraph.NODE_ID).toString();
			}
			++counter;
		}
		if (counter > 1) {
			throw new UnrecoverableError(String.format("Node %s  has more than one branch parent. This is impossible.",
			                                           hash));
		}
		return result;
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
		final Vertex branchHeadVertex = getBranchHead(branchName);
		if (branchHeadVertex == null) {
			if (Logger.logWarn()) {
				Logger.warn("Returning empty branch transaction iterator.");
			}
			return new ArrayList<String>(0);
		}
		return new TransactionIterator(branchHeadVertex.getProperty(NODE_ID).toString(), this);
	}
	
	@NoneNull
	private Vertex getChangeSet(@NotEmptyString final String fromHash) {
		return getNode(NODE_ID, fromHash);
	}
	
	@NoneNull
	private EdgeType getEdge(@NotEmptyString final String node,
	                         @NotEmptyString final String parent) {
		final Vertex nodeNode = getChangeSet(node);
		final Vertex parentNode = getChangeSet(parent);
		if ((nodeNode == null) || (parentNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for NULL vertices. Returning empty null.");
			}
			return null;
		}
		
		for (final Edge rel : parentNode.getEdges(Direction.OUT, EdgeType.BRANCH_EDGE.toString(),
		                                          EdgeType.MERGE_EDGE.toString())) {
			if (rel.getVertex(Direction.IN).equals(nodeNode)) {
				return EdgeType.valueOf(rel.getLabel());
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
	public String getMergeParent(@NotEmptyString final String hash) {
		final Vertex node = getChangeSet(hash);
		if (node == null) {
			throw new UnrecoverableError("Requsting branch parent of node not contained by GitRevDependencyGraph.");
		}
		int counter = 0;
		String result = null;
		for (final Edge relation : node.getEdges(Direction.IN, EdgeType.MERGE_EDGE.toString())) {
			if (result == null) {
				result = relation.getVertex(Direction.OUT).getProperty(RevDependencyGraph.NODE_ID).toString();
			}
			++counter;
		}
		if (counter > 1) {
			throw new UnrecoverableError("Node " + hash + " has more than one merge parent. This should never occur.");
		}
		return result;
	}
	
	@NoneNull
	private Vertex getNode(@NotEmptyString final String nodeIDProperty,
	                       @NotEmptyString final String nodeID) {
		if (Logger.logDebug()) {
			Logger.debug("Querying for node %s in neo4j graph.", nodeID);
		}
		Vertex result = null;
		final Iterator<Vertex> iterator = this.graph.getVertices(nodeIDProperty, nodeID).iterator();
		while (iterator.hasNext()) {
			result = iterator.next();
			if (iterator.hasNext()) {
				throw new UnrecoverableError(
				                             String.format("Found multiple nodes with nodeIDProperty %s and nodeID %s.",
				                                           nodeIDProperty, nodeID));
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
	 * @return the previous transactions
	 */
	@NoneNull
	public Iterable<String> getPreviousTransactions(@NotEmptyString final String hash) {
		final TransactionIterator iter = new TransactionIterator(hash, this);
		if (iter.hasNext()) {
			iter.next();
			return iter;
		}
		return new ArrayList<String>(0);
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
		final Set<String> result = new HashSet<String>();
		for (final Vertex node : this.graph.getVertices()) {
			final Object property = node.getProperty(RevDependencyGraph.NODE_ID);
			if (property != null) {
				result.add(property.toString());
			}
		}
		return new Iterable<String>() {
			
			@Override
			public Iterator<String> iterator() {
				// PRECONDITIONS
				
				try {
					return result.iterator();
				} finally {
					// POSTCONDITIONS
				}
			}
			
		};
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
		final Vertex node = getChangeSet(hash);
		if (node == null) {
			throw new UnrecoverableError(
			                             "Requsting a node not contained by GitRevDependencyGraph. This might indicate a inconsistent data state!");
		}
		final Iterator<Vertex> iterator = node.query().direction(Direction.IN).labels(EdgeType.BRANCH_HEAD.toString())
		                                      .vertices().iterator();
		String result = null;
		while (iterator.hasNext()) {
			result = iterator.next().getProperty(BRANCH_ID).toString();
			if (iterator.hasNext()) {
				throw new UnrecoverableError(
				                             "Found change set that is branch head of multiple branches. This is impossible. This might indicate a inconsistent data state!");
			}
		}
		return result;
	}
	
	/**
	 * Checks if this RevDependencyGraph contains the same RevDepdendecy structure as the provided other
	 * RevDependencyGraph
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
	 * Removes a ChangeSet from the underlying graphDB.
	 * 
	 * @param hash
	 *            the hash
	 */
	@NoneNull
	public void removeChangeSet(@NotEmptyString final String hash) {
		if (Logger.logDebug()) {
			Logger.debug("Removing change set node %s from graph.", hash);
		}
		final Vertex vertex = getChangeSet(hash);
		if (vertex != null) {
			final TitanTransaction titanTransaction = this.graph.startTransaction();
			this.graph.removeVertex(vertex);
			titanTransaction.stopTransaction(Conclusion.SUCCESS);
		}
	}
	
	/**
	 * Removes a tag
	 * 
	 * @param tagName
	 */
	@NoneNull
	public void removeTag(@NotEmptyString final String tagName) {
		if (this.tags.containsTo(tagName)) {
			this.tags.removeTo(tagName);
		}
	}
}

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

package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kisa.Logger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;

public class TestChangeGenealogy implements ChangeGenealogy<String> {
	
	public static TestChangeGenealogy readFromDB(final File dbFile) {
		final GraphDatabaseService graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		final TestChangeGenealogy genealogy = new TestChangeGenealogy(graph, dbFile);
		return genealogy;
	}
	
	private final GraphDatabaseService graph;
	private final File                 dbFile;
	private final IndexManager         indexManager;
	
	private final Index<Node>          nodeIndex;
	
	private final Index<Node>          rootIndex;
	
	private TestChangeGenealogy(final GraphDatabaseService graph, final File dbFile) {
		this.graph = graph;
		this.dbFile = dbFile;
		this.indexManager = graph.index();
		this.nodeIndex = this.indexManager.forNodes(CoreChangeGenealogy.NODE_ID);
		this.rootIndex = this.indexManager.forNodes(CoreChangeGenealogy.ROOT_VERTICES);
	}
	
	public boolean addEdge(@NotEmpty final String dependent,
	                       @NotEmpty final String target,
	                       final GenealogyEdgeType edgeType) {
		
		// add both vertices
		if (!containsVertex(dependent)) {
			addVertex(dependent);
		}
		if (!containsVertex(target)) {
			addVertex(target);
		}
		
		// we know that they have to exist
		final Node from = getNodeForVertex(dependent);
		final Node to = getNodeForVertex(target);
		
		if ((from == null) || (to == null)) {
			return false;
		}
		
		final Transaction tx = this.graph.beginTx();
		final Relationship relationship = from.createRelationshipTo(to, edgeType);
		if (relationship == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		
		if (getRoots().contains(to)) {
			this.rootIndex.remove(to, CoreChangeGenealogy.ROOT_VERTICES);
		}
		
		tx.success();
		tx.finish();
		
		return true;
	}
	
	@NoneNull
	public boolean addVertex(@NotEmpty final String v) {
		if (containsVertex(v)) {
			if (Logger.logWarn()) {
				Logger.warn("JavaChangeOperations with id `" + v + "` already exists");
			}
			return false;
		}
		final Transaction tx = this.graph.beginTx();
		final Node node = this.graph.createNode();
		if (node == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		node.setProperty(CoreChangeGenealogy.NODE_ID, v);
		
		this.nodeIndex.add(node, CoreChangeGenealogy.NODE_ID, node.getProperty(CoreChangeGenealogy.NODE_ID));
		this.rootIndex.add(node, CoreChangeGenealogy.ROOT_VERTICES, 1);
		
		tx.success();
		tx.finish();
		
		return true;
		
	}
	
	@Override
	public void close() {
		this.graph.shutdown();
	}
	
	@Override
	public boolean containsEdge(final String from,
	                            final String to) {
		final GenealogyEdgeType result = getEdge(from, to);
		return result != null;
	}
	
	@Override
	public boolean containsVertex(final String vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	@Override
	public int edgeSize() {
		int result = 0;
		final IndexHits<Node> nodes = nodes();
		for (final Node node : nodes) {
			result += getAllDependents(node).size();
		}
		nodes.close();
		return result;
	}
	
	@Override
	public Collection<String> getAllDependants(final String t) {
		return getDependants(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                     GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                     GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	private Collection<Node> getAllDependents(final Node node) {
		return getDependents(node, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                     GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                     GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	@Override
	public Collection<String> getAllParents(final String t) {
		return getParents(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                  GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                  GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                  GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	@Override
	@Deprecated
	public CoreChangeGenealogy getCore() {
		return null;
	}
	
	@Override
	public Collection<String> getDependants(final String t,
	                                        final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(t);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<String>();
		}
		final Collection<Node> dependentNodes = getDependents(node, edgeTypes);
		final Set<String> parentOperations = new HashSet<String>();
		for (final Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	private Collection<Node> getDependents(final Node node,
	                                       final GenealogyEdgeType... edgeTypes) {
		final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
		final Set<Node> parents = new HashSet<Node>();
		for (final Relationship rel : relationships) {
			parents.add(rel.getStartNode());
		}
		return parents;
	}
	
	private GenealogyEdgeType getEdge(final String from,
	                                  final String to) {
		final Node fromNode = getNodeForVertex(from);
		final Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		final Iterable<Relationship> relationships = fromNode.getRelationships(Direction.OUTGOING,
		                                                                       GenealogyEdgeType.CallOnDefinition,
		                                                                       GenealogyEdgeType.DefinitionOnDefinition,
		                                                                       GenealogyEdgeType.DefinitionOnDeletedDefinition,
		                                                                       GenealogyEdgeType.DeletedCallOnCall,
		                                                                       GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                                                                       GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		for (final Relationship rel : relationships) {
			if (rel.getEndNode().equals(toNode)) {
				final RelationshipType relationshipType = rel.getType();
				return GenealogyEdgeType.valueOf(relationshipType.toString());
			}
		}
		return null;
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(final String from,
	                                              final String to) {
		final Collection<GenealogyEdgeType> result = new ArrayList<GenealogyEdgeType>(1);
		result.add(getEdge(from, to));
		return result;
	}
	
	@Override
	public Set<GenealogyEdgeType> getExistingEdgeTypes() {
		final Set<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		final List<GenealogyEdgeType> values = Arrays.asList(GenealogyEdgeType.values());
		final Iterable<RelationshipType> relationshipTypes = GlobalGraphOperations.at(getGraphDBService())
		                                                                          .getAllRelationshipTypes();
		for (final RelationshipType type : relationshipTypes) {
			if (values.contains(type)) {
				final GenealogyEdgeType edgeType = GenealogyEdgeType.valueOf(type.toString());
				result.add(edgeType);
			}
		}
		return result;
	}
	
	@Override
	public File getGraphDBDir() {
		return this.dbFile;
	}
	
	@Override
	public GraphDatabaseService getGraphDBService() {
		return this.graph;
	}
	
	private Node getNodeForVertex(final String from) {
		final IndexHits<Node> indexHits = this.nodeIndex.query(CoreChangeGenealogy.NODE_ID, from);
		if (!indexHits.hasNext()) {
			return null;
		}
		final Node node = indexHits.next();
		indexHits.close();
		return node;
	}
	
	@Override
	public String getNodeId(final String t) {
		if (containsVertex(t)) {
			return String.valueOf(t);
		}
		return null;
	}
	
	private Collection<Node> getParents(final Node node,
	                                    final GenealogyEdgeType[] edgeTypes) {
		final Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		final Set<Node> parents = new HashSet<Node>();
		for (final Relationship rel : relationships) {
			parents.add(rel.getEndNode());
		}
		return parents;
	}
	
	@Override
	public Collection<String> getParents(final String t,
	                                     final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(t);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<String>();
		}
		final Collection<Node> dependentNodes = getParents(node, edgeTypes);
		final Set<String> parentOperations = new HashSet<String>();
		for (final Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	@Override
	public Collection<String> getRoots() {
		final Collection<String> result = new HashSet<String>();
		final IndexHits<Node> indexHits = this.rootIndex.query(CoreChangeGenealogy.ROOT_VERTICES, 1);
		while (indexHits.hasNext()) {
			result.add(getVertexForNode(indexHits.next()));
		}
		return result;
	}
	
	private String getVertexForNode(final Node dependentNode) {
		return dependentNode.getProperty(CoreChangeGenealogy.NODE_ID).toString();
	}
	
	@Override
	public int inDegree(final String s) {
		return inDegree(s, GenealogyEdgeType.values());
	}
	
	@Override
	public int inDegree(final String s,
	                    final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(s);
		final Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	/**
	 * Vertex set.
	 * 
	 * @return the genealogy vertex iterator
	 */
	public IndexHits<Node> nodes() {
		return this.nodeIndex.query(CoreChangeGenealogy.NODE_ID, "*");
	}
	
	@Override
	public int outDegree(final String s) {
		return outDegree(s, GenealogyEdgeType.values());
	}
	
	@Override
	public int outDegree(final String s,
	                     final GenealogyEdgeType... edgeTypes) {
		final Node node = getNodeForVertex(s);
		final Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		int numEdges = 0;
		for (@SuppressWarnings ("unused")
		final Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	@Override
	public Iterable<String> vertexSet() {
		final IndexHits<Node> indexHits = this.nodeIndex.query(CoreChangeGenealogy.NODE_ID, "*");
		
		final Set<String> operations = new HashSet<String>();
		for (final Node node : indexHits) {
			operations.add(node.getProperty(CoreChangeGenealogy.NODE_ID).toString());
		}
		indexHits.close();
		return operations;
	}
	
	@Override
	public int vertexSize() {
		final IndexHits<Node> indexHits = this.nodeIndex.query(CoreChangeGenealogy.NODE_ID, "*");
		final int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}

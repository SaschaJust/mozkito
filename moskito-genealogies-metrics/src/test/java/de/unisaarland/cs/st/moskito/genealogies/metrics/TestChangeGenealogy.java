/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;


public class TestChangeGenealogy implements ChangeGenealogy<String> {
	
	public static TestChangeGenealogy readFromDB(final File dbFile) {
		GraphDatabaseService graph = new EmbeddedGraphDatabase(dbFile.getAbsolutePath());
		TestChangeGenealogy genealogy = new TestChangeGenealogy(graph, dbFile);
		return genealogy;
	}
	private GraphDatabaseService graph;
	private File                 dbFile;
	private IndexManager         indexManager;
	
	private Index<Node>          nodeIndex;
	
	private Index<Node>          rootIndex;
	
	private TestChangeGenealogy(final GraphDatabaseService graph, File dbFile) {
		this.graph = graph;
		this.dbFile = dbFile;
		indexManager = graph.index();
		nodeIndex = indexManager.forNodes(CoreChangeGenealogy.NODE_ID);
		rootIndex = indexManager.forNodes(CoreChangeGenealogy.ROOT_VERTICES);
	}
	
	public boolean addEdge(@NotEmpty final String dependent,
			@NotEmpty final String target, final GenealogyEdgeType edgeType) {
		
		//add both vertices
		if (!containsVertex(dependent)) {
			addVertex(dependent);
		}
		if (!containsVertex(target)) {
			addVertex(target);
		}
		
		//we know that they have to exist
		Node from = this.getNodeForVertex(dependent);
		Node to = this.getNodeForVertex(target);
		
		if ((from == null) || (to == null)) {
			return false;
		}
		
		Transaction tx = graph.beginTx();
		Relationship relationship = from.createRelationshipTo(to, edgeType);
		if (relationship == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		
		if (getRoots().contains(to)) {
			rootIndex.remove(to, CoreChangeGenealogy.ROOT_VERTICES);
		}
		
		tx.success();
		tx.finish();
		
		return true;
	}
	
	@NoneNull
	public boolean addVertex(@NotEmpty final String v) {
		if (this.containsVertex(v)) {
			if (Logger.logWarn()) {
				Logger.warn("JavaChangeOperations with id `" + v + "` already exists");
			}
			return false;
		}
		Transaction tx = this.graph.beginTx();
		Node node = graph.createNode();
		if (node == null) {
			tx.failure();
			tx.finish();
			return false;
		}
		node.setProperty(CoreChangeGenealogy.NODE_ID, v);
		
		nodeIndex.add(node, CoreChangeGenealogy.NODE_ID, node.getProperty(CoreChangeGenealogy.NODE_ID));
		rootIndex.add(node, CoreChangeGenealogy.ROOT_VERTICES, 1);
		
		tx.success();
		tx.finish();
		
		return true;
		
	}
	
	@Override
	public void close() {
		this.graph.shutdown();
	}
	
	@Override
	public boolean containsEdge(String from, String to) {
		GenealogyEdgeType result = this.getEdge(from, to);
		return result != null;
	}
	
	@Override
	public boolean containsVertex(String vertex) {
		return (getNodeForVertex(vertex) != null);
	}
	
	@Override
	public int edgeSize() {
		int result = 0;
		IndexHits<Node> nodes = nodes();
		for (Node node : nodes) {
			result += getAllDependents(node).size();
		}
		nodes.close();
		return result;
	}
	
	@Override
	public Collection<String> getAllDependants(String t) {
		return getDependants(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	private Collection<Node> getAllDependents(Node node) {
		return getDependents(node, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	@Override
	public Collection<String> getAllParents(String t) {
		return getParents(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	@Override
	@Deprecated
	public CoreChangeGenealogy getCore() {
		return null;
	}
	
	@Override
	public Collection<String> getDependants(String t, GenealogyEdgeType... edgeTypes) {
		Node node = getNodeForVertex(t);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<String>();
		}
		Collection<Node> dependentNodes = getDependents(node, edgeTypes);
		Set<String> parentOperations = new HashSet<String>();
		for (Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	private Collection<Node> getDependents(Node node, GenealogyEdgeType... edgeTypes) {
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
		Set<Node> parents = new HashSet<Node>();
		for (Relationship rel : relationships) {
			parents.add(rel.getStartNode());
		}
		return parents;
	}
	
	private GenealogyEdgeType getEdge(String from, String to) {
		Node fromNode = getNodeForVertex(from);
		Node toNode = getNodeForVertex(to);
		if ((fromNode == null) || (toNode == null)) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve edges for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty null.");
			}
			return null;
		}
		
		Iterable<Relationship> relationships = fromNode.getRelationships(Direction.OUTGOING,
				GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
		
		for (Relationship rel : relationships) {
			if (rel.getEndNode().equals(toNode)) {
				RelationshipType relationshipType = rel.getType();
				return GenealogyEdgeType.valueOf(relationshipType.toString());
			}
		}
		return null;
	}
	
	@Override
	public Collection<GenealogyEdgeType> getEdges(String from, String to) {
		Collection<GenealogyEdgeType> result = new ArrayList<GenealogyEdgeType>(1);
		result.add(getEdge(from, to));
		return result;
	}
	
	@Override
	public Set<GenealogyEdgeType> getExistingEdgeTypes() {
		Set<GenealogyEdgeType> result = new HashSet<GenealogyEdgeType>();
		List<GenealogyEdgeType> values = Arrays.asList(GenealogyEdgeType.values());
		Iterable<RelationshipType> relationshipTypes = graph.getRelationshipTypes();
		for (RelationshipType type : relationshipTypes) {
			if (values.contains(type)) {
				GenealogyEdgeType edgeType = GenealogyEdgeType.valueOf(type.toString());
				result.add(edgeType);
			}
		}
		return result;
	}
	
	@Override
	public File getGraphDBDir() {
		return dbFile;
	}
	
	@Override
	public GraphDatabaseService getGraphDBService() {
		return graph;
	}
	
	private Node getNodeForVertex(String from) {
		IndexHits<Node> indexHits = nodeIndex.query(CoreChangeGenealogy.NODE_ID, from);
		if(!indexHits.hasNext()){
			return null;
		}
		Node node = indexHits.next();
		indexHits.close();
		return node;
	}
	
	@Override
	public String getNodeId(String t) {
		if (this.containsVertex(t)) {
			return String.valueOf(t);
		}
		return null;
	}
	
	private Collection<Node> getParents(Node node, GenealogyEdgeType[] edgeTypes) {
		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		Set<Node> parents = new HashSet<Node>();
		for (Relationship rel : relationships) {
			parents.add(rel.getEndNode());
		}
		return parents;
	}
	
	@Override
	public Collection<String> getParents(String t, GenealogyEdgeType... edgeTypes) {
		Node node = getNodeForVertex(t);
		if (node == null) {
			if (Logger.logWarn()) {
				Logger.warn("You cannot retrieve dependent genealogy vertives for JavaChangeOperations that have no corresponding within the ChangeGenealogy. Returning empty collection.");
			}
			return new HashSet<String>();
		}
		Collection<Node> dependentNodes = getParents(node, edgeTypes);
		Set<String> parentOperations = new HashSet<String>();
		for (Node dependentNode : dependentNodes) {
			parentOperations.add(getVertexForNode(dependentNode));
		}
		return parentOperations;
	}
	
	@Override
	public Collection<String> getRoots() {
		Collection<String> result = new HashSet<String>();
		IndexHits<Node> indexHits = rootIndex.query(CoreChangeGenealogy.ROOT_VERTICES, 1);
		while (indexHits.hasNext()) {
			result.add(this.getVertexForNode(indexHits.next()));
		}
		return result;
	}
	
	private String getVertexForNode(Node dependentNode) {
		return dependentNode.getProperty(CoreChangeGenealogy.NODE_ID).toString();
	}
	
	@Override
	public int inDegree(String s) {
		return inDegree(s, GenealogyEdgeType.values());
	}
	
	@Override
	public int inDegree(String s, GenealogyEdgeType... edgeTypes) {
		Node node = getNodeForVertex(s);
		Iterable<Relationship> relationships = node.getRelationships(Direction.INCOMING, edgeTypes);
		int numEdges = 0;
		for (@SuppressWarnings("unused") Relationship r : relationships) {
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
		return nodeIndex.query(CoreChangeGenealogy.NODE_ID, "*");
	}
	
	@Override
	public int outDegree(String s) {
		return outDegree(s, GenealogyEdgeType.values());
	}
	
	@Override
	public int outDegree(String s, GenealogyEdgeType... edgeTypes) {
		Node node = getNodeForVertex(s);
		Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING, edgeTypes);
		int numEdges = 0;
		for (@SuppressWarnings("unused") Relationship r : relationships) {
			++numEdges;
		}
		return numEdges;
	}
	
	@Override
	public Iterable<String> vertexSet() {
		IndexHits<Node> indexHits = nodeIndex.query(CoreChangeGenealogy.NODE_ID, "*");
		
		Set<String> operations = new HashSet<String>();
		for (Node node : indexHits) {
			operations.add(node.getProperty(CoreChangeGenealogy.NODE_ID).toString());
		}
		indexHits.close();
		return operations;
	}
	
	@Override
	public int vertexSize() {
		IndexHits<Node> indexHits = nodeIndex.query(CoreChangeGenealogy.NODE_ID, "*");
		int result = indexHits.size();
		indexHits.close();
		return result;
	}
	
}

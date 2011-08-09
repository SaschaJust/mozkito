package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * The Class GenealogyVertex.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyVertex {
	
	/** The transaction_id. */
	public static String transaction_id = "transaction_id";
	
	/**
	 * Creates a GenealogyVertex
	 * 
	 * @param graph
	 *            the graph
	 * @param transactionId
	 *            the transaction id
	 * @return the genealogy vertex
	 */
	public static GenealogyVertex create(final GraphDatabaseService graph, final String transactionId) {
		Transaction tx = graph.beginTx();
		Node node = graph.createNode();
		node.setProperty(transaction_id, transactionId);
		Index<Node> index = node.getGraphDatabase().index().forNodes(transaction_id);
		index.add(node, transaction_id, node.getProperty(transaction_id));
		tx.success();
		tx.finish();
		return new GenealogyVertex(node);
	}
	
	/** The node. */
	private final Node node;
	
	/**
	 * Instantiates a new genealogy vertex.
	 * 
	 * @param node
	 *            the node
	 */
	protected GenealogyVertex(final Node node) {
		this.node = node;
	}
	
	/**
	 * Associates this vertex with the given GraphDBChangeOperation.
	 * 
	 * @param genOpVertex
	 *            the GraphDBChangeOperation this vertex will be associated with
	 */
	protected void addChangeOperation(final GraphDBChangeOperation genOpVertex) {
		Transaction tx = node.getGraphDatabase().beginTx();
		node.createRelationshipTo(genOpVertex.getNode(), RelationshipTypes.CONTAINS);
		tx.success();
		tx.finish();
	}
	
	/**
	 * Make this vertex dependent on the given GenealogyVertex (outgoing edge).
	 * The edge added will be of type edgeType: other <--edgeType-- this
	 * 
	 * @param other
	 *            the vertex this vertex will be depending on
	 * @param edgeType
	 *            the type of the edge to be added
	 */
	public void addDepencyTo(final GenealogyVertex other, final GenealogyEdgeType edgeType) {
		node.createRelationshipTo(other.getNode(), edgeType);
	}
	
	/**
	 * Associates this vertex with the given JavaChangeOperation.
	 * 
	 * @param operation
	 *            the operation this vertex will be associated with
	 */
	public void addJavaChangeOperation(final JavaChangeOperation operation) {
		GraphDBChangeOperation op = ChangeGenealogy.getGraphDBChangeOperationById(node.getGraphDatabase(),
				operation.getId());
		if (op == null) {
			op = GraphDBChangeOperation.create(node.getGraphDatabase(), operation.getId());
		}
		this.addChangeOperation(op);
	}
	
	/*
	 * (non-Javadoc)
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
		GenealogyVertex other = (GenealogyVertex) obj;
		if (node == null) {
			if (other.node != null) {
				return false;
			}
		} else if (node.getId() != other.node.getId()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns a collection containing the vertices that depend on this vertex
	 * (incoming edges).
	 * 
	 * @return all dependents
	 */
	public Collection<GenealogyVertex> getAllDependents() {
		//TODO implement
		return null;
	}
	
	/**
	 * Returns the collection of vertices this vertex depends on (outgoing
	 * edges).
	 * 
	 * @return the parents
	 */
	public Collection<GenealogyVertex> getAllVerticesDependingOn() {
		//TODO implement
		return null;
	}
	
	/**
	 * Gets the change operation IDs associated with this vertex.
	 * 
	 * @return the change operation ids
	 */
	protected Collection<Long> getChangeOperationIds() {
		Set<Long> result = new HashSet<Long>();
		Iterable<Relationship> relationships = this.node.getRelationships(Direction.OUTGOING,
				RelationshipTypes.CONTAINS);
		for (Relationship rel : relationships) {
			Node opNode = rel.getEndNode();
			result.add(GraphDBChangeOperation.getChangeOperationId(opNode));
		}
		return result;
	}
	
	/**
	 * Returns a collection containing vertices depending on this vertex via an
	 * edge of a type is contained within the specified edge type array.
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the dependants
	 */
	public Collection<GenealogyVertex> getDependants(final GenealogyEdgeType[] types) {
		//TODO implement
		return null;
	}
	
	/**
	 * Gets the java change operations.
	 * 
	 * @return the java change operations
	 */
	public Collection<JavaChangeOperation> getJavaChangeOperations() {
		//TODO implement
		return null;
	}
	
	/**
	 * Gets the node.
	 * 
	 * @return the node
	 */
	protected Node getNode() {
		return node;
	}
	
	/**
	 * Returns a collection containing vertices this vertex depends on via an
	 * edge of a type is contained within the specified edge type array.
	 * 
	 * @param types
	 *            consider only edges of these types
	 * @return the vertices depending on
	 */
	public Collection<GenealogyVertex> getVerticesDependingOn(final GenealogyEdgeType[] types) {
		//TODO implement
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) ((prime * result) + ((node == null) ? 0 : node.getId()));
		return result;
	}
	
}

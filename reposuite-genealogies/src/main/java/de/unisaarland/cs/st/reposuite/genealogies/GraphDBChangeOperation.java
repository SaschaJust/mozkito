package de.unisaarland.cs.st.reposuite.genealogies;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

class GraphDBChangeOperation {
	
	public static String keyName = "javachangeoperation_id";
	
	protected static GraphDBChangeOperation create(final GraphDatabaseService graph, final Long id) {
		Transaction tx = graph.beginTx();
		Node node = graph.createNode();
		node.setProperty(keyName, id);
		Index<Node> index = node.getGraphDatabase().index().forNodes(keyName);
		index.add(node, keyName, node.getProperty(keyName));
		tx.success();
		tx.finish();
		return new GraphDBChangeOperation(node);
	}
	
	protected static Long getChangeOperationId(final Node node){
		return (Long) node.getProperty(keyName);
	}
	
	private final Node node;
	
	protected GraphDBChangeOperation(final Node node) {
		this.node = node;
	}
	
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
		GraphDBChangeOperation other = (GraphDBChangeOperation) obj;
		if (node == null) {
			if (other.node != null) {
				return false;
			}
		} else if (node.getId() != other.node.getId()) {
			return false;
		}
		return true;
	}
	
	protected Long getJavaChangeOperationId() {
		return (Long) this.node.getProperty(keyName);
	}
	
	protected Node getNode() {
		return this.node;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) ((prime * result) + ((node == null) ? 0 : node.getId()));
		return result;
	}
	
}

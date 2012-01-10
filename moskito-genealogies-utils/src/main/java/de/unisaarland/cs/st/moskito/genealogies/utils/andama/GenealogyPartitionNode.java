package de.unisaarland.cs.st.moskito.genealogies.utils.andama;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class GenealogyPartitionNode {
	
	private Collection<JavaChangeOperation> t;
	private String nodeId;
	private boolean lastNode;
	
	public GenealogyPartitionNode(Collection<JavaChangeOperation> t, String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = false;
	}
	
	public GenealogyPartitionNode(Collection<JavaChangeOperation> t, String nodeId, boolean lastNode) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = lastNode;
	}
	
	public Collection<JavaChangeOperation> getNode() {
		return t;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public boolean isLast() {
		return lastNode;
	}
	
}

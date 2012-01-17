package de.unisaarland.cs.st.moskito.genealogies.metrics;

import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class GenealogyTransactionNode {
	
	private RCSTransaction t;
	private String nodeId;
	private boolean lastNode;
	
	public GenealogyTransactionNode(RCSTransaction t, String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = false;
	}
	
	public GenealogyTransactionNode(RCSTransaction t, String nodeId, boolean lastNode) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = lastNode;
	}
	
	public RCSTransaction getNode() {
		return t;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public boolean isLast() {
		return lastNode;
	}
	
}

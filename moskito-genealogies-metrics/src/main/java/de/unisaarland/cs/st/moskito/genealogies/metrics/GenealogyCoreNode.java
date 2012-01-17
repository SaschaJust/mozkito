package de.unisaarland.cs.st.moskito.genealogies.metrics;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class GenealogyCoreNode {
	
	private JavaChangeOperation t;
	private String nodeId;
	private boolean lastNode;
	
	public GenealogyCoreNode(JavaChangeOperation t, String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = false;
	}
	
	public GenealogyCoreNode(JavaChangeOperation t, String nodeId, boolean lastNode) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = lastNode;
	}
	
	public JavaChangeOperation getNode() {
		return t;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public boolean isLast() {
		return lastNode;
	}
	
}

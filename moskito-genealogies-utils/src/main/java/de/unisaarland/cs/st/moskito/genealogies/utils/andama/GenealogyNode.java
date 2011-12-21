package de.unisaarland.cs.st.moskito.genealogies.utils.andama;


public class GenealogyNode<T> {
	
	private T      t;
	private String nodeId;
	private boolean lastNode;
	
	public GenealogyNode(T t, String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = false;
	}
	
	public GenealogyNode(T t, String nodeId, boolean lastNode) {
		this.t = t;
		this.nodeId = nodeId;
		this.lastNode = lastNode;
	}
	
	public T getNode() {
		return t;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public boolean isLast() {
		return lastNode;
	}
	
}

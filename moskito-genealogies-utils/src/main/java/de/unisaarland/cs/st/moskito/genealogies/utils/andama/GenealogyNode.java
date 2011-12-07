package de.unisaarland.cs.st.moskito.genealogies.utils.andama;


public class GenealogyNode<T> {
	
	private T      t;
	private String nodeId;
	
	public GenealogyNode(T t, String nodeId) {
		this.t = t;
		this.nodeId = nodeId;
	}
	
	public T getNode() {
		return t;
	}
	
	public String getNodeId() {
		return nodeId;
	}

}

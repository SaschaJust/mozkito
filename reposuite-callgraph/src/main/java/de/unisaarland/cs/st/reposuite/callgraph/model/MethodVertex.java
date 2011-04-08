package de.unisaarland.cs.st.reposuite.callgraph.model;

import java.io.Serializable;

/**
 * The Class MethodVertex.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MethodVertex extends CallGraphVertex implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4013481775627394171L;
	private String methodName;
	/**
	 * Instantiates a new method vertex.
	 * 
	 * @param id
	 *            the id
	 */
	protected MethodVertex(final String id, final String filename) {
		super(id, filename);
		this.parent = VertexFactory.createClassVertex(this);
		this.parent.addChild(this);
	}
	
	
	public String getFullQualifiedMethodName() {
		return this.parent.getFullQualifiedName() + "." + getMethodName();
	}
	
	public String getMethodName() {
		return this.methodName;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@Override
	public ClassVertex getParent() {
		return this.parent;
	}
}

package de.unisaarland.cs.st.reposuite.genealogies;

import de.unisaarland.cs.st.reposuite.genealogies.model.GraphDBChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

/**
 * The Class GenealogyChangeOperation.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyChangeOperation {
	
	/** The node. */
	private final GraphDBChangeOperation node;
	
	/**
	 * Instantiates a new genealogy change operation.
	 * 
	 * @param node
	 *            the node
	 */
	protected GenealogyChangeOperation(final GraphDBChangeOperation node) {
		this.node = node;
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
		GenealogyChangeOperation other = (GenealogyChangeOperation) obj;
		return node.getId().equals(other.node.getId());
	}
	
	/**
	 * Gets the change operation id.
	 * 
	 * @return the change operation id
	 */
	public long getChangeOperationId() {
		return node.getChangeOperationId();
	}
	
	/**
	 * Gets the JavaChangeOperation from relational database.
	 * 
	 * @return the java change operation
	 */
	public JavaChangeOperation getJavaChangeOperation() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return node.getId().hashCode();
	}
}

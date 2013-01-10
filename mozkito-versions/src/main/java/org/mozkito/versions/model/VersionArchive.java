package org.mozkito.versions.model;

import org.mozkito.persistence.Annotated;
import org.mozkito.versions.RevDependencyGraph;

/**
 * The Class VersionArchive.
 */
public class VersionArchive implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID = -3701231007051514130L;
	
	/** The rev dep graph. */
	private RevDependencyGraph revDepGraph;
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public String getHandle() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Gets the master branch.
	 * 
	 * @return the master branch
	 */
	public Branch getMasterBranch() {
		// TODO implement
		return null;
	}
	
	/**
	 * Gets the rev dependency graph.
	 * 
	 * @return the rev dependency graph
	 */
	public RevDependencyGraph getRevDependencyGraph() {
		return this.revDepGraph;
	}
	
	/**
	 * Gets the transaction by id.
	 * 
	 * @param id
	 *            the id
	 * @return the transaction by id
	 */
	public ChangeSet getTransactionById(final String id) {
		// TODO implement
		return null;
	}
	
	/**
	 * Sets the rev dependency graph.
	 * 
	 * @param revDepGraph
	 *            the new rev dependency graph
	 */
	public void setRevDependencyGraph(final RevDependencyGraph revDepGraph) {
		this.revDepGraph = revDepGraph;
	}
	
}

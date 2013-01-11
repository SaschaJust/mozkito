/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.versions.model;

import javax.persistence.Table;

import org.mozkito.persistence.Annotated;
import org.mozkito.versions.RevDependencyGraph;

/**
 * The Class VersionArchive.
 */
@Table (name = "version_archive")
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
	public String getClassName() {
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

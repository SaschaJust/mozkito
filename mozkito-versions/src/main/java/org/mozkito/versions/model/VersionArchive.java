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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mozkito.persistence.Annotated;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.RevDependencyGraph;

/**
 * The Class VersionArchive.
 */
@Entity
@Table (name = "version_archive")
public class VersionArchive implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long  serialVersionUID = -3701231007051514130L;
	
	/** The rev dep graph. */
	private RevDependencyGraph revDepGraph;
	
	/** The generated id. */
	private long               generatedId;
	
	private BranchFactory      branchFactory;
	
	/**
	 * Gets the branch factory.
	 * 
	 * @return the branch factory
	 */
	@Transient
	public BranchFactory getBranchFactory() {
		return this.branchFactory;
	}
	
	/**
	 * Gets the transaction by id.
	 * 
	 * @param id
	 *            the id
	 * @return the transaction by id
	 */
	public ChangeSet getChangeSetById(final String id) {
		// TODO implement
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	@Transient
	public String getClassName() {
		return VersionArchive.class.getSimpleName();
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generated id
	 */
	@Id
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the master branch.
	 * 
	 * @return the master branch
	 */
	@Transient
	public Branch getMasterBranch() {
		return this.branchFactory.getMasterBranch();
	}
	
	/**
	 * Gets the rev dependency graph.
	 * 
	 * @return the rev dependency graph
	 */
	@Transient
	public RevDependencyGraph getRevDependencyGraph() {
		return this.revDepGraph;
	}
	
	/**
	 * Sets the branch factory.
	 * 
	 * @param branchFactory
	 *            the new branch factory
	 */
	public void setBranchFactory(final BranchFactory branchFactory) {
		this.branchFactory = branchFactory;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the new generated id
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
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

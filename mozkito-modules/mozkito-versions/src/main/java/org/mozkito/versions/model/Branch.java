/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
/**
 * 
 */
package org.mozkito.versions.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.persistence.Annotated;

/**
 * The Class RCSBranch.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@Entity
@Table (name = "branch")
public class Branch implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID   = 5419737140470855522L;
	
	/** The name. */
	private String            name;
	
	/** The head. */
	private ChangeSet         head               = null;
	
	/** The merged in. */
	private Set<String>       mergedIn           = new HashSet<String>();
	
	private VersionArchive    versionArchive;
	
	/** The Constant MASTER_BRANCH_NAME. */
	public static String      MASTER_BRANCH_NAME = "master";             //$NON-NLS-1$
	                                                                      
	/**
	 * Sets the master branch name.
	 * 
	 * @param name
	 *            the new master branch name
	 */
	public static void setMasterBranchName(final String name) {
		Branch.MASTER_BRANCH_NAME = name;
	}
	
	/**
	 * Instantiates a new rCS branch.
	 */
	protected Branch() {
		
	}
	
	/**
	 * Instantiates a new rCS branch.
	 * 
	 * @param versionArchive
	 *            the version archive
	 * @param name
	 *            the name
	 */
	public Branch(final VersionArchive versionArchive, final String name) {
		setName(name);
		setVersionArchive(versionArchive);
	}
	
	/**
	 * Adds the merged in.
	 * 
	 * @param mergedIn
	 *            the merged in
	 */
	@Transient
	@NoneNull
	public void addMergedIn(final String mergedIn) {
		getMergedIn().add(mergedIn);
	}
	
	/**
	 * Checks if any of the given transactions was committed into this branch.
	 * 
	 * @param tIds
	 *            the transaction ids to check for
	 * @return A sorted set of transactions committed into this branch
	 */
	@Transient
	public TreeSet<ChangeSet> containsAnyTransaction(final Collection<String> tIds) {
		final TreeSet<ChangeSet> result = new TreeSet<ChangeSet>();
		for (final String id : tIds) {
			final ChangeSet t = containsTransaction(id);
			if (t != null) {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Checks if the given transaction id was committed in this branch. Returns the Transaction if found, otherwise
	 * <code>null</code>.
	 * 
	 * @param tId
	 *            the t id
	 * @return the transaction if found. Otherwise <code>null</code>
	 */
	@Transient
	public ChangeSet containsTransaction(final String tId) {
		ChangeSet current = getHead();
		while (current != null) {
			if (current.getId().equals(tId)) {
				return current;
			}
			current = current.getBranchParent();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Branch other = (Branch) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Override
	@Transient
	public String getClassName() {
		return Branch.class.getSimpleName();
	}
	
	/**
	 * Returns the head transaction of the branch.
	 * 
	 * @return the end
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	public ChangeSet getHead() {
		return this.head;
	}
	
	/**
	 * Gets the merged in.
	 * 
	 * @return the name of the branch this branch was merged in (if any)
	 */
	@ElementCollection
	public Set<String> getMergedIn() {
		return this.mergedIn;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Id
	@Basic
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the version archive.
	 * 
	 * @return the version archive
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Column (nullable = false)
	public VersionArchive getVersionArchive() {
		return this.versionArchive;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getName() == null)
		                                                ? 0
		                                                : getName().hashCode());
		return result;
	}
	
	/**
	 * Checks if is master branch.
	 * 
	 * @return true, if is master branch
	 */
	@Transient
	public boolean isMasterBranch() {
		return getName().equals(Branch.MASTER_BRANCH_NAME);
	}
	
	/**
	 * Checks if is open.
	 * 
	 * @return true, if is open
	 */
	@Transient
	public boolean isOpen() {
		return getHead() == null;
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the end to set
	 */
	public void setHead(final ChangeSet end) {
		this.head = end;
	}
	
	/**
	 * Sets the merged in.
	 * 
	 * @param mergedIn
	 *            the new merged in
	 */
	public void setMergedIn(final Set<String> mergedIn) {
		this.mergedIn = mergedIn;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Sets the version archive.
	 * 
	 * @param versionArchive
	 *            the new version archive
	 */
	public void setVersionArchive(final VersionArchive versionArchive) {
		this.versionArchive = versionArchive;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("RCSBranch [name=");
		sb.append(getName());
		sb.append(", parent=");
		sb.append(", head=");
		if (getHead() != null) {
			sb.append(getHead().getId());
		} else {
			sb.append("null");
		}
		sb.append("]");
		return sb.toString();
	}
	
}

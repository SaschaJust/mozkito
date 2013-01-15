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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;

import org.mozkito.persistence.Annotated;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.exceptions.NoSuchHandleException;

/**
 * The Class File.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "handle")
public class Handle implements Annotated, Serializable {
	
	/** The archive. */
	private VersionArchive        versionArchive   = null;
	
	/** The Constant serialVersionUID. */
	private static final long     serialVersionUID = 7232712367403624199L;
	
	/** The generated id. */
	private long                  generatedId;
	
	/** The changed names. */
	private Map<Revision, String> changedNames     = new HashMap<Revision, String>();
	
	/**
	 * used by PersistenceUtil to create a {@link Handle} instance.
	 */
	protected Handle() {
		
	}
	
	/**
	 * Instantiates a new handle in the given VersionArchive.
	 * 
	 * @param archive
	 *            the archive
	 */
	public Handle(final VersionArchive archive) {
		this.versionArchive = archive;
	}
	
	/**
	 * Assign a revision as a revision changing the file name of this file handle.
	 * 
	 * @param revision
	 *            the revision that changed the file name of this handle to the specified pathName
	 * @param pathName
	 *            the new path name of this handle as changed in the revision
	 */
	@Transient
	public void assignRevision(final Revision revision,
	                           final String pathName) {
		getChangedNames().put(revision, pathName);
	}
	
	/**
	 * Checks if the given transaction changed the underlyign handle.
	 * 
	 * @param transaction
	 *            the transaction
	 * @return true, if successful
	 */
	@Transient
	public boolean changedHandleName(final ChangeSet transaction) {
		for (final Revision revision : transaction.getRevisions()) {
			if (getChangedNames().containsKey(revision)) {
				return true;
			}
		}
		return false;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Handle other = (Handle) obj;
		if (getGeneratedId() != other.getGeneratedId()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the changed names.
	 * 
	 * @return the changedNames
	 */
	@ElementCollection
	@JoinTable (name = "filename_changes", joinColumns = { @JoinColumn (name = "fileid", nullable = false) })
	public Map<Revision, String> getChangedNames() {
		return this.changedNames;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	@Override
	@Transient
	public String getClassName() {
		return Handle.class.getSimpleName();
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the latest path of the handle in the master branch head.
	 * 
	 * @return the latest path
	 * @throws NoSuchHandleException
	 *             if the handle could not be found in the master branch
	 */
	@Transient
	public String getLatestPath() throws NoSuchHandleException {
		final Branch masterBranch = this.versionArchive.getMasterBranch();
		final ChangeSet masterBranchHead = masterBranch.getHead();
		try {
			final String path = getPath(masterBranchHead);
			return path;
		} catch (final NoSuchHandleException e) {
			throw NoSuchHandleException.format(e, "Could not determine path for File (id=%s) in master branch.",
			                                   String.valueOf(getGeneratedId()));
		}
	}
	
	/**
	 * Gets the path the file has in transactions.
	 * 
	 * @param transaction
	 *            the transaction to retrieve the file's path for
	 * @return the path of the Handle as set in transaction
	 * @throws NoSuchHandleException
	 *             if the handle could not be found in one of the branches the given transaction is part of
	 */
	@Transient
	public String getPath(final ChangeSet transaction) throws NoSuchHandleException {
		
		final RevDependencyGraph revDependencyGraph = this.versionArchive.getRevDependencyGraph();
		
		for (final Revision revision : transaction.getRevisions()) {
			if (getChangedNames().containsKey(revision)) {
				return getChangedNames().get(revision);
			}
		}
		
		for (final String parentId : revDependencyGraph.getPreviousTransactions(transaction.getId())) {
			final ChangeSet parentTransaction = this.versionArchive.getChangeSetById(parentId);
			for (final Revision revision : parentTransaction.getRevisions()) {
				if (getChangedNames().containsKey(revision)) {
					return getChangedNames().get(revision);
				}
			}
		}
		
		throw NoSuchHandleException.format("Could not determine path for File (id=%s) for transaction %s. Returning latestPath.",
		                                   String.valueOf(getGeneratedId()), transaction.getId());
	}
	
	/**
	 * Returns the new file name of the handle if the provided revision changed the file name of the handle.
	 * 
	 * @param revision
	 *            the revision
	 * @return the path
	 * @throws NoSuchHandleException
	 *             if the file name of the handle was not changed by the revision
	 */
	@Transient
	public String getPath(final Revision revision) throws NoSuchHandleException {
		if (getChangedNames().containsKey(revision)) {
			return getChangedNames().get(revision);
		}
		throw NoSuchHandleException.format("Could not determine path for File (id=%s) for revision %s. Returning latestPath.",
		                                   String.valueOf(getGeneratedId()), revision.toString());
	}
	
	/**
	 * Gets the archive.
	 * 
	 * @return the archive
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
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
		result = (prime * result) + (int) (getGeneratedId() ^ (getGeneratedId() >>> 32));
		return result;
	}
	
	/**
	 * Check if the Handle got saved in the DB.
	 * 
	 * @return true, if successful saved in DB, false otherwise
	 */
	@Transient
	public boolean saved() {
		return getGeneratedId() != 0;
	}
	
	/**
	 * Sets the changed names.
	 * 
	 * @param changedNames
	 *            the changed names
	 */
	protected void setChangedNames(final Map<Revision, String> changedNames) {
		this.changedNames = changedNames;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Sets the archive.
	 * 
	 * @param archive
	 *            the new archive
	 */
	public void setVersionArchive(final VersionArchive archive) {
		this.versionArchive = archive;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "File [id=" + getGeneratedId() + ", changedNames="
		        + JavaUtils.collectionToString(getChangedNames().values()) + "]";
	}
	
}

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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.database.Artifact;
import org.mozkito.database.Layout;
import org.mozkito.database.Layout.TableType;
import org.mozkito.database.PersistenceUtil;
import org.mozkito.database.constraints.column.ForeignKey;
import org.mozkito.database.constraints.column.NotNull;
import org.mozkito.database.constraints.column.PrimaryKey;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.index.Index;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;
import org.mozkito.persistence.FieldKey;
import org.mozkito.persistence.IterableFieldKey;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.exceptions.NoSuchHandleException;

/**
 * The Class File.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Handle extends Artifact {
	
	/** The Constant LAYOUT. */
	public static final Layout<Handle> LAYOUT           = new Layout<>();
	
	/** The Constant TABLE. */
	public static final Table          TABLE;
	
	/** The Constant PATHINREVISION_TABLE. */
	public static final Table          HANDLE_PATHS_TABLE;
	
	static {
		try {
			
			// @formatter:off
			
			TABLE = new Table("handles", 
			                  new Column("id", Type.getSerial(), 
			                             new PrimaryKey()),
			                  new Column("version_archive_id", Type.getLong(),
			                             new ForeignKey(VersionArchive.MAIN_TABLE,
			                                            VersionArchive.MAIN_TABLE.column("id")), 
                                         new NotNull()));
			
			HANDLE_PATHS_TABLE = new Table("handle_paths",
			                               new Column("handle_id", Type.getLong(), 
			                                          new ForeignKey(TABLE, TABLE.column("id")), 
			                                          new NotNull()),
                                           new Column("revision_id", Type.getLong(),
//			                                          new ForeignKey(Revision.TABLE,
//			                                                         Revision.TABLE.column("id")), 
			                                          new NotNull()),
			                               new Column("path", Type.getVarChar(255), 
			                                          new NotNull()));
			HANDLE_PATHS_TABLE.setConstraints(new org.mozkito.database.constraints.table.PrimaryKey(HANDLE_PATHS_TABLE.column("handle_id"),
			                                                                                        HANDLE_PATHS_TABLE.column("revision_id")));
			HANDLE_PATHS_TABLE.setIndexes(new Index(HANDLE_PATHS_TABLE.column("handle_id")),
			                              new Index(HANDLE_PATHS_TABLE.column("revision_id")),
			                              new Index(HANDLE_PATHS_TABLE.column("handle_id"),
			                                        HANDLE_PATHS_TABLE.column("revision_id")));
			
			// @formatter:on
			
			LAYOUT.addTable(TABLE, TableType.MAIN);
			LAYOUT.addTable(HANDLE_PATHS_TABLE, TableType.JOIN);
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/** The archive. */
	private VersionArchive             versionArchive   = null;
	
	/** The Constant serialVersionUID. */
	private static final long          serialVersionUID = 7232712367403624199L;
	
	/** The generated id. */
	private long                       id;
	
	/** The changed names. */
	private Map<Revision, String>      changedNames     = new HashMap<Revision, String>();
	
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
	
	public boolean changedHandleName(final ChangeSet transaction) {
		for (final Artifact revision : transaction.getRevisions()) {
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
		if (getId() != other.getId()) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#get(org.mozkito.database.PersistenceUtil, org.mozkito.persistence.FieldKey)
	 */
	@Override
	public <T> T get(final PersistenceUtil util,
	                 final FieldKey key) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'get' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#get(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.IterableFieldKey)
	 */
	@Override
	public <T> Collection<T> get(final PersistenceUtil util,
	                             final IterableFieldKey key) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'get' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#get(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.IterableFieldKey, int)
	 */
	@Override
	public <T> T get(final PersistenceUtil util,
	                 final IterableFieldKey key,
	                 final int index) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'get' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getAll(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.FieldKey[])
	 */
	@Override
	public Map<FieldKey, Object> getAll(final PersistenceUtil util,
	                                    final FieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getAll' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getAll(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.IterableFieldKey[])
	 */
	@Override
	public Map<IterableFieldKey, Object> getAll(final PersistenceUtil util,
	                                            final IterableFieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getAll' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getAny(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.FieldKey[])
	 */
	@Override
	public <T> T getAny(final PersistenceUtil util,
	                    final FieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getAny' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getAny(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.IterableFieldKey[])
	 */
	@Override
	public <T> T getAny(final PersistenceUtil util,
	                    final IterableFieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getAny' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getAsOneString(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.FieldKey[])
	 */
	@Override
	public String getAsOneString(final PersistenceUtil util,
	                             final FieldKey... fKeys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getAsOneString' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getAsOneString(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.IterableFieldKey)
	 */
	@Override
	public String getAsOneString(final PersistenceUtil util,
	                             final IterableFieldKey iKeys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getAsOneString' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the changed names.
	 * 
	 * @return the changedNames
	 */
	public Map<Revision, String> getChangedNames() {
		return this.changedNames;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	@Override
	public String getClassName() {
		return Handle.class.getSimpleName();
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Override
	public Long getId() {
		return this.id;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getIDString()
	 */
	@Override
	public String getIDString() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getIDString' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the latest path of the handle in the master branch head.
	 * 
	 * @param util
	 *            the util
	 * @return the latest path
	 * @throws NoSuchHandleException
	 *             if the handle could not be found in the master branch
	 */
	
	public String getLatestPath(final PersistenceUtil util) throws NoSuchHandleException {
		final Branch masterBranch = getVersionArchive().getMasterBranch(util);
		final ChangeSet masterBranchHead = masterBranch.getHead();
		try {
			final String path = getPath(masterBranchHead, util);
			return path;
		} catch (final NoSuchHandleException e) {
			throw NoSuchHandleException.format(e, "Could not determine path for File (id=%s) in master branch.",
			                                   String.valueOf(getId()));
		}
	}
	
	/**
	 * Gets the path the file has in transactions.
	 * 
	 * @param transaction
	 *            the transaction to retrieve the file's path for
	 * @param util
	 *            the util
	 * @return the path of the Handle as set in transaction
	 * @throws NoSuchHandleException
	 *             if the handle could not be found in one of the branches the given transaction is part of
	 */
	
	public String getPath(final ChangeSet transaction,
	                      final PersistenceUtil util) throws NoSuchHandleException {
		
		final RevDependencyGraph revDependencyGraph = getVersionArchive().getRevDependencyGraph(util);
		
		for (final Artifact revision : transaction.getRevisions()) {
			if (getChangedNames().containsKey(revision)) {
				return getChangedNames().get(revision);
			}
		}
		
		for (final String parentId : revDependencyGraph.getPreviousTransactions(transaction.getId())) {
			final ChangeSet parentTransaction = getVersionArchive().getChangeSetById(util, parentId);
			for (final Artifact revision : parentTransaction.getRevisions()) {
				if (getChangedNames().containsKey(revision)) {
					return getChangedNames().get(revision);
				}
			}
		}
		
		throw NoSuchHandleException.format("Could not determine path for File (id=%s) for transaction %s. Returning latestPath.",
		                                   String.valueOf(getId()), transaction.getId());
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
	
	public String getPath(final Artifact revision) throws NoSuchHandleException {
		if (getChangedNames().containsKey(revision)) {
			return getChangedNames().get(revision);
		}
		throw NoSuchHandleException.format("Could not determine path for File (id=%s) for revision %s. Returning latestPath.",
		                                   String.valueOf(getId()), revision.toString());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getSize(org.mozkito.database.PersistenceUtil,
	 *      org.mozkito.persistence.IterableFieldKey)
	 */
	@Override
	public int getSize(final PersistenceUtil util,
	                   final IterableFieldKey key) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return 0;
			throw new RuntimeException("Method 'getSize' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getText(org.mozkito.database.PersistenceUtil)
	 */
	@Override
	public String getText(final PersistenceUtil util) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getText' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the archive.
	 * 
	 * @return the archive
	 */
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
		result = (prime * result) + (int) (getId() ^ (getId() >>> 32));
		return result;
	}
	
	/**
	 * Check if the Handle got saved in the DB.
	 * 
	 * @return true, if successful saved in DB, false otherwise
	 */
	
	public boolean saved() {
		return getId() != 0;
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
		this.id = generatedId;
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#supportedFields()
	 */
	@Override
	public Set<FieldKey> supportedFields() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'supportedFields' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#supportedIteratableFields()
	 */
	@Override
	public Set<IterableFieldKey> supportedIteratableFields() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'supportedIteratableFields' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "File [id=" + getId() + ", changedNames=" + JavaUtils.collectionToString(getChangedNames().values())
		        + "]";
	}
	
}

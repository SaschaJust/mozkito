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
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.database.Artifact;
import org.mozkito.database.Layout;
import org.mozkito.database.Layout.TableType;
import org.mozkito.database.PersistenceUtil;
import org.mozkito.database.constraints.column.ForeignKey;
import org.mozkito.database.constraints.column.NotNull;
import org.mozkito.database.constraints.column.PrimaryKey;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;
import org.mozkito.persistence.FieldKey;
import org.mozkito.persistence.IterableFieldKey;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.versions.elements.ChangeType;

/**
 * The Class Revision.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Revision extends Artifact {
	
	/** The Constant LAYOUT. */
	public static final Layout<Revision> LAYOUT           = new Layout<>();
	
	/** The Constant TABLE. */
	public static final Table            MAIN_TABLE;
	
	/** The Constant MAP_TABLE. */
	private static final Table           MAP_TABLE;
	
	static {
		try {
			// @formatter:off
			
			MAIN_TABLE = new Table("revision", 
			                  new Column("id", Type.getSerial(), 
			                             new PrimaryKey()),
			                  new Column("changed_file_id", Type.getLong(), 
			                             new ForeignKey(Handle.TABLE, Handle.TABLE.column("id")), 
			                             new NotNull()),
			                  new Column("change_type", Type.getVarChar(ChangeType.class), 
			                             new NotNull())
			);
			
			MAP_TABLE = new Table("changeset_to_revision", 
			                      new Column("changeset_id", Type.getVarChar(40), 
			                                 new ForeignKey(ChangeSet.MAIN_TABLE, ChangeSet.MAIN_TABLE.column("id")), 
			                                 new NotNull()), 
			                      new Column("revision_id", Type.getLong(), 
			                                 new ForeignKey(Revision.MAIN_TABLE, Revision.MAIN_TABLE.column("id")), 
			                                 new NotNull())
			);
		
			LAYOUT.addTable(MAIN_TABLE, TableType.MAIN);
			LAYOUT.addTable(MAP_TABLE, TableType.JOIN);
			LAYOUT.makeImmutable();
			
			// @formatter:on
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long            serialVersionUID = 2149118675856446526L;
	
	/** The change type. */
	private ChangeType                   changeType;
	
	/** The transaction. */
	private ChangeSet                    changeSet;
	
	/** The changed file. */
	private Handle                       changedFile;
	
	/** The revision id. */
	private long                         id;
	
	/** The changed file id. */
	private long                         changedFileId;
	
	/** The change set id. */
	private String                       changeSetId;
	
	/**
	 * used by PersistenceUtil to instantiate a {@link Revision} object.
	 */
	protected Revision() {
		
	}
	
	/**
	 * Instantiates a new revision.
	 * 
	 * @param changeSet
	 *            the rcs transaction
	 * @param handle
	 *            the rcs file
	 * @param changeType
	 *            the change type
	 */
	public Revision(final ChangeSet changeSet, final Handle handle, final ChangeType changeType) {
		PRECONDITIONS: {
			if (changeSet == null) {
				throw new NullPointerException();
			}
			if (handle == null) {
				throw new NullPointerException();
			}
			if (changeType == null) {
				throw new NullPointerException();
			}
		}
		
		setChangeSet(changeSet);
		setChangedFile(handle);
		setChangeType(changeType);
		
		final boolean success = changeSet.addRevision(this);
		Condition.check(success, "Revision could not be registered at transaction.");
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getClassName() + ": " + this);
		}
		
		POSTCONDITIONS: {
			Condition.notNull(this.changeSet, "Transaction may never be null after creation.");
			Condition.notNull(this.changedFile, "Changed file may never be null after creation.");
		}
		
	}
	
	/**
	 * {@inheritDoc}
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
		final Revision other = (Revision) obj;
		if (this.changeSetId == null) {
			if (other.changeSetId != null) {
				return false;
			}
		} else if (!this.changeSetId.equals(other.changeSetId)) {
			return false;
		}
		if (this.changeType != other.changeType) {
			return false;
		}
		if (this.changedFileId != other.changedFileId) {
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	
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
	 * Gets the changed file.
	 * 
	 * @param util
	 *            the util
	 * @return the changedFile
	 */
	// @MapsId ("changedFile")
	// @ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	// @Column (nullable = false)
	
	public Handle getChangedFile(final PersistenceUtil util) {
		PRECONDITIONS: {
			if (util == null) {
				throw new NullPointerException();
			}
		}
		
		if (this.changedFile == null) {
			this.changedFile = util.loadById(Handle.class, this.changedFileId);
		} else {
			SANITY: {
				assert this.changedFileId > 0;
				assert this.changedFile.getId() > 0;
				assert this.changedFile.getId() == this.changedFileId;
			}
		}
		
		return this.changedFile;
	}
	
	/**
	 * Gets the changed file id.
	 * 
	 * @return the changed file id
	 */
	public long getChangedFileId() {
		return this.changedFileId;
	}
	
	/**
	 * Gets the transaction.
	 * 
	 * @param util
	 *            the util
	 * @return the transaction
	 */
	public ChangeSet getChangeSet(final PersistenceUtil util) {
		PRECONDITIONS: {
			if (util == null) {
				throw new NullPointerException();
			}
		}
		
		if (this.changeSet == null) {
			this.changeSet = util.loadById(ChangeSet.class, this.changeSetId);
		} else {
			SANITY: {
				assert this.changeSetId != null;
				assert !this.changeSetId.isEmpty();
				assert this.changeSet != null;
				assert this.changeSetId.equals(this.changeSet.getId());
			}
		}
		
		return this.changeSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	/**
	 * Gets the change set id.
	 * 
	 * @return the change set id
	 */
	public String getChangeSetId() {
		return this.changeSetId;
	}
	
	/**
	 * Gets the change type.
	 * 
	 * @return the changeType
	 */
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Entity#getClassName()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(Revision.class);
	}
	
	/**
	 * Gets the revision id.
	 * 
	 * @return the revisionId
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
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.changeSetId == null)
		                                                       ? 0
		                                                       : this.changeSetId.hashCode());
		result = (prime * result) + ((this.changeType == null)
		                                                      ? 0
		                                                      : this.changeType.hashCode());
		result = (prime * result) + (int) (this.changedFileId ^ (this.changedFileId >>> 32));
		return result;
	}
	
	/**
	 * Sets the changed file.
	 * 
	 * @param changedFile
	 *            the changedFile to set
	 */
	public void setChangedFile(final Handle changedFile) {
		this.changedFile = changedFile;
	}
	
	/**
	 * Sets the transaction.
	 * 
	 * @param changeSet
	 *            the transaction to set
	 */
	public void setChangeSet(final ChangeSet changeSet) {
		this.changeSet = changeSet;
	}
	
	/**
	 * Sets the change type.
	 * 
	 * @param changeType
	 *            the changeType to set
	 */
	protected void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * Sets the revision id.
	 * 
	 * @param revisionId
	 *            the revisionId to set
	 */
	public void setRevisionId(final long revisionId) {
		this.id = revisionId;
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Revision [changeSetId=" + getChangeSetId() + ", changedFile=" + getChangedFileId() + ", changeType="
		        + getChangeType() + "]";
	}
	
}

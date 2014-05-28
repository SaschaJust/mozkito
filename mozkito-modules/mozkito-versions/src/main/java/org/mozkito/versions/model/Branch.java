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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

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

/**
 * The Class Branch.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Branch extends Artifact {
	
	/** The Constant TABLE. */
	public static final Table          TABLE;
	
	/** The Constant MERGE_TABLE. */
	public static final Table          MERGE_TABLE;
	
	/** The Constant LAYOUT. */
	public static final Layout<Branch> LAYOUT           = new Layout<>();
	
	static {
		try {
			// @formatter:off

		      TABLE = new Table("branches",
		                        new Column("name", Type.getVarChar(255), 
		                                   new PrimaryKey(), 
		                                   new NotNull()),
		                        new Column("head", Type.getVarChar(40), 
		                                   new ForeignKey(ChangeSet.MAIN_TABLE, ChangeSet.MAIN_TABLE.column("id")))
		      );
		      
		      TABLE.setIndexes(new Index(TABLE.column("name")));
		      
		      MERGE_TABLE = new Table("branch_merges", 
		                              new Column("id", Type.getSerial(), 
		                                         new PrimaryKey()),
		                              new Column("branch_name", Type.getVarChar(255),
		                                         new ForeignKey(TABLE, TABLE.column("name")), 
		                                         new NotNull()),
		                              new Column("merge_changeset", Type.getVarChar(40),
		                                         new ForeignKey(ChangeSet.MAIN_TABLE, ChangeSet.MAIN_TABLE.column("id")))
		      );
		      
		      MERGE_TABLE.setIndexes(new Index(MERGE_TABLE.column("id")), 
		                             new Index(MERGE_TABLE.column("branch_name")), 
		                             new Index(MERGE_TABLE.column("merge_changeset"))
		      );
		      
		      LAYOUT.addTable(TABLE, TableType.MAIN);
		      LAYOUT.addTable(MERGE_TABLE, TableType.JOIN);
		      LAYOUT.makeImmutable();
		      
		// @formatter:on
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long          serialVersionUID = 5419737140470855522L;
	
	/**
	 * Sets the master branch name.
	 * 
	 * @param name
	 *            the new master branch name
	 */
	public static void setMasterBranchName(final String name) {
		Branch.MASTER_BRANCH_NAME = name;
	}
	
	/** The name. */
	private String         name;
	
	/** The head. */
	private ChangeSet      head               = null;
	
	/** The merged in. */
	private Set<String>    mergedIn           = new HashSet<String>();
	
	/** The version archive. */
	private VersionArchive versionArchive;
	
	/** The Constant MASTER_BRANCH_NAME. */
	public static String   MASTER_BRANCH_NAME = "master";             //$NON-NLS-1$
	                                                                   
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
		
		throw new RuntimeException("Method 'get' has not yet been implemented."); //$NON-NLS-1$    
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
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Override
	public String getClassName() {
		return Branch.class.getSimpleName();
	}
	
	/**
	 * Returns the head transaction of the branch.
	 * 
	 * @return the end
	 */
	public ChangeSet getHead() {
		return this.head;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getId()
	 */
	@Override
	public String getId() {
		return this.name;
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
	 * Gets the merged in.
	 * 
	 * @return the name of the changeset this branch was merged in (if any)
	 */
	public Set<String> getMergedIn() {
		return this.mergedIn;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
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
	 * Gets the version archive.
	 * 
	 * @return the version archive
	 */
	public VersionArchive getVersionArchive() {
		return this.versionArchive;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * {@inheritDoc}
	 * 
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
	
	public boolean isMasterBranch() {
		return getName().equals(Branch.MASTER_BRANCH_NAME);
	}
	
	/**
	 * Checks if is open.
	 * 
	 * @return true, if is open
	 */
	
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
	 * @param mergedIn2
	 *            the new merged in
	 */
	public void setMergedIn(final Set<String> mergedIn2) {
		this.mergedIn = mergedIn2;
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
	/**
	 * {@inheritDoc}
	 * 
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

/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.versions.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

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
import org.mozkito.persons.model.Person;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.versions.exceptions.NoSuchHandleException;

/**
 * The Class Transaction.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ChangeSet extends Artifact {
	
	/** The Constant LAYOUT. */
	public static final Layout<ChangeSet> LAYOUT           = new Layout<>();
	
	/** The Constant TABLE. */
	public static final Table             MAIN_TABLE;
	
	/** The Constant TAGS_TABLE. */
	public static final Table             TAGS_TABLE;
	
	/** The Constant CHILDREN_TABLE. */
	public static final Table             CHILDREN_TABLE;
	
	/** The Constant PARENTS_TABLE. */
	public static final Table             PARENTS_TABLE;
	
	/** The Constant REVISIONS_TABLE. */
	public static final Table             REVISIONS_TABLE;
	
	/** The Constant BRANCH_INDEXES_TABLE. */
	public static final Table             BRANCH_INDEXES_TABLE;
	
	static {
		try {
			// @formatter:off
			
			MAIN_TABLE = new Table("changesets", 
			                  new Column("id", Type.getVarChar(40),
			                             new PrimaryKey()),
			                  new Column("author", Type.getLong(), 
			                             new ForeignKey(Person.MAIN_TABLE, Person.MAIN_TABLE.column("id")), 
			                             new NotNull()),
			                  new Column("message", Type.getText(), 
			                             new NotNull()),
			                  new Column ("timestamp", Type.getDateTime(), 
			                              new NotNull()),
			                  new Column("original_id", Type.getVarChar(40)),
			                  new Column("atomic", Type.getBoolean(), 
			                             new NotNull()),
			                  new Column("version_archive_id", Type.getLong(), 
			                             new NotNull(), 
			                             new ForeignKey(VersionArchive.MAIN_TABLE, VersionArchive.MAIN_TABLE.column("id"))),
							  new Column("branch_parent_id", Type.getVarChar(40)) // nullable
			);
			
//			MAIN_TABLE.setConstraints(new org.mozkito.database.constraints.table.
//			                              ForeignKey(MAIN_TABLE.column("branch_parent_id"), MAIN_TABLE, MAIN_TABLE.column("id")));
			MAIN_TABLE.setIndexes(new Index(MAIN_TABLE.column("id")),
			                      new Index(MAIN_TABLE.column("branch_parent_id"))
			);
			
			TAGS_TABLE = new Table("changeset_tags",
			                       new Column("changeset_id", Type.getVarChar(40),
			                                  new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
			                                  new NotNull()),
			                       new Column("tag", Type.getVarChar(255), 
			                                  new NotNull())			
			);
			TAGS_TABLE.setPrimaryKey(new org.mozkito.database.constraints.table.
			                             PrimaryKey(TAGS_TABLE.column("changeset_id"), TAGS_TABLE.column("tag")));
			TAGS_TABLE.setIndexes(new Index(TAGS_TABLE.column("changeset_id")));
			
			CHILDREN_TABLE = new Table("changeset_children", 
									   new Column("changeset_id", Type.getVarChar(40), 
									              new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
									              new NotNull()),
									   new Column("child_id", Type.getVarChar(40), 
									              new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
									              new NotNull())
			);
			CHILDREN_TABLE.setPrimaryKey(new org.mozkito.database.constraints.table.
			                                 PrimaryKey(CHILDREN_TABLE.column("changeset_id"), CHILDREN_TABLE.column("child_id")));
			CHILDREN_TABLE.setIndexes(new Index(CHILDREN_TABLE.column("changeset_id")),
			                          new Index(CHILDREN_TABLE.column("child_id"))                          
			);
			
			PARENTS_TABLE = new Table("changeset_parents", 
			                          new Column("changeset_id", Type.getVarChar(40), 
									              new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
									              new NotNull()),
									  new Column("parent_id", Type.getVarChar(40), 
									              new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
									              new NotNull())
			);
			PARENTS_TABLE.setPrimaryKey(new org.mozkito.database.constraints.table.
		                                    PrimaryKey(PARENTS_TABLE.column("changeset_id"), PARENTS_TABLE.column("parent_id")));
			PARENTS_TABLE.setIndexes(new Index(PARENTS_TABLE.column("changeset_id")),
		                             new Index(PARENTS_TABLE.column("parent_id"))                          
			);
			
			REVISIONS_TABLE = new Table("changeset_revisions",
			                            new Column("changeset_id", Type.getVarChar(40), 
										           new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
										           new NotNull()),
										new Column("revision_id", Type.getLong(),
										           new ForeignKey(Revision.MAIN_TABLE, Revision.MAIN_TABLE.column("id")),
										           new NotNull())
			);
			REVISIONS_TABLE.setPrimaryKey(new org.mozkito.database.constraints.table.
			                                  PrimaryKey(REVISIONS_TABLE.column("changeset_id"), REVISIONS_TABLE.column("revision_id")));
			REVISIONS_TABLE.setIndexes(new Index(REVISIONS_TABLE.column("changeset_id")), 
			                           new Index(REVISIONS_TABLE.column("revision_id")));
			
			BRANCH_INDEXES_TABLE = new Table("changeset_branch_indexes", 
			                                 new Column("changeset_id", Type.getVarChar(40), 
												           new ForeignKey(MAIN_TABLE, MAIN_TABLE.column("id")),
												           new NotNull()),
								             new Column("branch_index", Type.getLong(), 
								                        new NotNull())
			);
			BRANCH_INDEXES_TABLE.setPrimaryKey(new org.mozkito.database.constraints.table.
		                                           PrimaryKey(BRANCH_INDEXES_TABLE.column("changeset_id"), BRANCH_INDEXES_TABLE.column("branch_index")));
			BRANCH_INDEXES_TABLE.setIndexes(new Index(BRANCH_INDEXES_TABLE.column("changeset_id")), 
		                                    new Index(BRANCH_INDEXES_TABLE.column("branch_index")));
			
			LAYOUT.addTable(MAIN_TABLE, TableType.MAIN);
			LAYOUT.addTable(TAGS_TABLE, TableType.JOIN);
			LAYOUT.addTable(CHILDREN_TABLE, TableType.JOIN);
			LAYOUT.addTable(PARENTS_TABLE, TableType.JOIN);
			LAYOUT.addTable(REVISIONS_TABLE, TableType.JOIN);
			LAYOUT.addTable(BRANCH_INDEXES_TABLE, TableType.JOIN);
			LAYOUT.makeImmutable();
			
			// @formatter:on
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long             serialVersionUID = -7619009648634901112L;
	
	/** The persons. */
	private Person                        author;
	
	/** The id. */
	private String                        id;
	
	/** The message. */
	private String                        message;
	
	/** The children. */
	private Set<ChangeSet>                children         = new HashSet<ChangeSet>();
	
	/** The branch parent. */
	private ChangeSet                     branchParent     = null;
	
	/** The merge parent. */
	private List<ChangeSet>               mergeParents     = new LinkedList<ChangeSet>();
	
	/** The revisions. */
	private Collection<Revision>          revisions        = new LinkedList<Revision>();
	
	/** The java timestamp. */
	private DateTime                      javaTimestamp;
	
	/** The tags. */
	private Set<String>                   tags             = new HashSet<String>();
	
	/** The original id. */
	private String                        originalId;
	
	/** The atomic. */
	private boolean                       atomic           = false;
	
	/** The version archive. */
	private VersionArchive                versionArchive;
	
	/** The branch indices. */
	private Map<String, Long>             branchIndices    = new HashMap<String, Long>();
	
	/**
	 * used by PersistenceUtil to create Transaction instance.
	 */
	protected ChangeSet() {
		this.atomic = false;
	}
	
	/**
	 * Instantiates a new rCS transaction.
	 * 
	 * @param versionArchive
	 *            the version archive
	 * @param id
	 *            the id
	 * @param message
	 *            the message
	 * @param timestamp
	 *            the timestamp
	 * @param author
	 *            the author
	 * @param originalId
	 *            the original id
	 */
	public ChangeSet(final VersionArchive versionArchive, final String id, final String message,
	        final DateTime timestamp, final Person author, final String originalId) {
		PRECONDITIONS: {
			if (versionArchive == null) {
				throw new NullPointerException();
			}
			if (id == null) {
				throw new NullPointerException();
			}
			if (message == null) {
				throw new NullPointerException();
			}
			if (timestamp == null) {
				throw new NullPointerException();
			}
			if (author == null) {
				throw new NullPointerException();
			}
		}
		
		setId(id);
		setMessage(message);
		setTimestamp(timestamp);
		setAuthor(author);
		setOriginalId(originalId);
		setVersionArchive(versionArchive);
		getVersionArchive().addChangeSet(this);
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getClassName() + ": " + this);
		}
		
	}
	
	/**
	 * Adds the all tags.
	 * 
	 * @param tagNames
	 *            the tag names
	 * @return true, if successful
	 */
	
	public boolean addAllTags(final Collection<String> tagNames) {
		boolean ret = false;
		final Set<String> tags = getTags();
		ret = tags.addAll(tagNames);
		setTags(tags);
		return ret;
	}
	
	/**
	 * Adds the branch.
	 * 
	 * @param rCSBranch
	 *            the branch
	 * @param index
	 *            the index
	 * @return true, if successful
	 */
	
	public boolean addBranch(final Branch rCSBranch,
	                         final Long index) {
		if (getBranchIndices().containsKey(rCSBranch.getName())) {
			return false;
		}
		getBranchIndices().put(rCSBranch.getName(), index);
		return true;
	}
	
	/**
	 * Adds the child.
	 * 
	 * @param changeSet
	 *            the rcs transaction
	 * @return true, if successful
	 */
	
	public boolean addChild(final ChangeSet changeSet) {
		CompareCondition.notEquals(changeSet, this, "a transaction may never be a child of its own: %s", this); //$NON-NLS-1$
		boolean ret = false;
		
		if (!getChildren().contains(changeSet)) {
			final Set<ChangeSet> children = getChildren();
			ret = children.add(changeSet);
			setChildren(children);
		}
		
		return ret;
	}
	
	/**
	 * Sets the merge parent.
	 * 
	 * @param mergeParent
	 *            the merge parent
	 */
	
	public void addMergeParent(final ChangeSet mergeParent) {
		PRECONDITIONS: {
			if (mergeParent == null) {
				throw new NullPointerException();
			}
		}
		
		this.mergeParents.add(mergeParent);
		
	}
	
	/**
	 * Adds the revision. Is automatically called from the constructor of Revision
	 * 
	 * @param rCSRevision
	 *            the revision
	 * @return true, if successful
	 */
	
	protected boolean addRevision(final Revision rCSRevision) {
		PRECONDITIONS: {
			if (rCSRevision == null) {
				throw new NullPointerException();
			}
		}
		
		return getRevisions().add(rCSRevision);
	}
	
	/**
	 * Adds the tag.
	 * 
	 * @param tagName
	 *            the tag name
	 * @return true, if successful
	 */
	
	public boolean addTag(final String tagName) {
		PRECONDITIONS: {
			if (tagName == null) {
				throw new NullPointerException();
			}
		}
		
		return getTags().add(tagName);
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
			if (key == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			
			Object o = null;
			switch (key) {
				case AUTHOR:
					o = getAuthor();
					break;
				case BODY:
					o = getMessage();
					break;
				case CREATION_TIMESTAMP:
					o = getTimestamp();
					break;
				case DESCRIPTION:
					final String[] split = getMessage().split("\\\n\\\n", 2);
					if (split.length > 1) {
						o = split[1];
					} else {
						o = "";
					}
					break;
				case ID:
					o = getId();
					break;
				case SUMMARY:
					final String[] split2 = getMessage().split("\\\n\\\n", 2);
					if (split2.length > 1) {
						o = split2[0];
					} else {
						o = getMessage();
					}
					break;
				case TYPE:
					o = getClassName();
					break;
				default:
					SANITY: {
						assert !supportedFields().contains(key);
					}
					throw new IllegalArgumentException(key.name());
			}
			
			try {
				@SuppressWarnings ("unchecked")
				final T result = (T) o;
				return result;
			} catch (final ClassCastException e) {
				throw new IllegalArgumentException(key.getDeclaringClass().getSimpleName() + " " + key.name()
				        + " resolves to type " + key.resultType().getCanonicalName(), e);
			}
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
	@SuppressWarnings ("unchecked")
	@Override
	public <T> Collection<T> get(final PersistenceUtil util,
	                             final IterableFieldKey key) {
		PRECONDITIONS: {
			if (key == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			
			Collection<T> collection = null;
			switch (key) {
				case INVOLVED:
					collection = (Collection<T>) new ArrayList<Person>(1) {
						
						/**
                         * 
                         */
						private static final long serialVersionUID = 1L;
						
						{
							add(getAuthor());
						}
					};
					break;
				case FILES:
					final Collection<Handle> changedFiles = getChangedFiles(util);
					SANITY: {
						assert changedFiles != null;
					}
					collection = (Collection<T>) new ArrayList<String>(changedFiles.size());
					for (final Handle handle : changedFiles) {
						try {
							((Collection<String>) collection).add(handle.getPath(this, util));
						} catch (final NoSuchHandleException ignore) {
							if (Logger.logWarn()) {
								Logger.warn("Handle not found in owning change set: %s should be owned by %s.", handle,
								            this);
							}
						}
					}
					break;
				default:
					SANITY: {
						assert !supportedIteratableFields().contains(key);
					}
					throw new IllegalArgumentException(key.name());
			}
			
			SANITY: {
				assert collection != null;
			}
			
			return collection;
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
			return org.mozkito.database.Artifact.Static.get(util, this, key, index);
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
			return org.mozkito.database.Artifact.Static.getAll(util, this, keys);
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
			return org.mozkito.database.Artifact.Static.getAll(util, this, keys);
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
	public Object getAny(final PersistenceUtil util,
	                     final FieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return org.mozkito.database.Artifact.Static.getAny(util, this, keys);
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
	public Object getAny(final PersistenceUtil util,
	                     final IterableFieldKey... keys) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return org.mozkito.database.Artifact.Static.getAny(util, this, keys);
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
			return org.mozkito.database.Artifact.Static.getAsOneString(util, this, fKeys);
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
			return org.mozkito.database.Artifact.Static.getAsOneString(util, this, iKeys);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * Gets the branch indices.
	 * 
	 * @return the branch indices
	 */
	public Map<String, Long> getBranchIndices() {
		// PRECONDITIONS
		
		try {
			return this.branchIndices;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the branches.
	 * 
	 * @return the branches
	 */
	
	public Set<String> getBranchNames() {
		// PRECONDITIONS
		
		try {
			return getBranchIndices().keySet();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the branch parent.
	 * 
	 * @return the branch parent
	 */
	
	/**
	 * Gets the branch parent.
	 * 
	 * @return the branch parent
	 */
	public ChangeSet getBranchParent() {
		return this.branchParent;
	}
	
	/**
	 * Gets the changed files.
	 * 
	 * @param util
	 *            the util
	 * @return the changed files
	 */
	
	public Collection<Handle> getChangedFiles(final PersistenceUtil util) {
		final List<Handle> changedFiles = new LinkedList<Handle>();
		for (final Revision rCSRevision : getRevisions()) {
			changedFiles.add(rCSRevision.getChangedFile(util));
		}
		return changedFiles;
	}
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	//
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	public Set<ChangeSet> getChildren() {
		return this.children != null
		                            ? this.children
		                            : new HashSet<ChangeSet>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Entity#getClassName()
	 */
	@Override
	public final String getClassName() {
		return JavaUtils.getHandle(ChangeSet.class);
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Override
	public String getId() {
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
			return getId();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	protected Date getJavaTimestamp() {
		return getTimestamp() != null
		                             ? getTimestamp().toDate()
		                             : null;
	}
	
	/**
	 * Gets the merge parent.
	 * 
	 * @return the merge parent
	 */
	
	/**
	 * Gets the merge parents.
	 * 
	 * @return the merge parents
	 */
	public List<ChangeSet> getMergeParents() {
		// PRECONDITIONS
		
		try {
			return this.mergeParents;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Gets the original id.
	 * 
	 * @return the original id
	 */
	public String getOriginalId() {
		return this.originalId;
	}
	
	/**
	 * Gets the revision that changed the specified path.
	 * 
	 * @param path
	 *            the path
	 * @param util
	 *            the util
	 * @return the revision for path if found. Returns <code>null</code> otherwise.
	 */
	
	public Revision getRevisionForPath(final String path,
	                                   final PersistenceUtil util) {
		String comparePath = path;
		if (path.startsWith("/")) {
			comparePath = path.substring(1);
		}
		for (final Revision revision : getRevisions()) {
			try {
				final String fileName = revision.getChangedFile(util).getPath(this, util);
				if (fileName.equals(comparePath)) {
					return revision;
				}
			} catch (final NoSuchHandleException e) {
				// ignore
			}
		}
		return null;
	}
	
	/**
	 * Gets the revisions.
	 * 
	 * @return the revisions
	 */
	
	/**
	 * Gets the revisions.
	 * 
	 * @return the revisions
	 */
	public Collection<Revision> getRevisions() {
		return this.revisions != null
		                             ? this.revisions
		                             : new HashSet<Revision>();
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
		return org.mozkito.database.Artifact.Static.getSize(util, this, key);
	}
	
	/**
	 * Gets the tags.
	 * 
	 * @return the tag
	 */
	public Set<String> getTags() {
		return this.tags;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getText(org.mozkito.database.PersistenceUtil)
	 */
	@Override
	public String getText(final PersistenceUtil util) {
		return get(util, FieldKey.BODY);
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	
	public DateTime getTimestamp() {
		return this.javaTimestamp;
	}
	
	/**
	 * Gets the version archive.
	 * 
	 * @return the version archive
	 */
	
	/**
	 * Gets the version archive.
	 * 
	 * @return the version archive
	 */
	public VersionArchive getVersionArchive() {
		return this.versionArchive;
	}
	
	/**
	 * Checks if is atomic.
	 * 
	 * @return true, if is atomic
	 */
	// @Column (columnDefinition = "boolean default 'FALSE'")
	public boolean isAtomic() {
		return this.atomic;
	}
	
	/**
	 * Sets the atomic.
	 * 
	 * @param atomic
	 *            the new atomic
	 */
	public void setAtomic(final boolean atomic) {
		this.atomic = atomic;
	}
	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the new author
	 */
	
	public void setAuthor(final Person author) {
		this.author = author;
	}
	
	/**
	 * Sets the branch indices.
	 * 
	 * @param branchIndices
	 *            the branch indices
	 */
	public void setBranchIndices(final Map<String, Long> branchIndices) {
		// PRECONDITIONS
		try {
			this.branchIndices = branchIndices;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the branch parent.
	 * 
	 * @param branchParent
	 *            the new branch parent
	 */
	public void setBranchParent(final ChangeSet branchParent) {
		// PRECONDITIONS
		try {
			this.branchParent = branchParent;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the children.
	 * 
	 * @param children
	 *            the children to set
	 */
	public void setChildren(final Set<ChangeSet> children) {
		this.children = children;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	protected void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @param date
	 *            the new java timestamp
	 */
	protected void setJavaTimestamp(final Date date) {
		this.javaTimestamp = date != null
		                                 ? new DateTime(date)
		                                 : null;
	}
	
	/**
	 * Sets the merge parent.
	 * 
	 * @param mergeParents
	 *            merge parents
	 */
	
	protected void setMergeParent(final ChangeSet... mergeParents) {
		// PRECONDITIONS
		try {
			final List<ChangeSet> mergeParentList = new ArrayList<>(mergeParents.length);
			for (final ChangeSet mergeParent : mergeParents) {
				mergeParentList.add(mergeParent);
			}
			setMergeParents(mergeParentList);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the merge parent.
	 * 
	 * @param mergeParent
	 *            the new merge parent
	 */
	public void setMergeParents(final List<ChangeSet> mergeParent) {
		// PRECONDITIONS
		try {
			this.mergeParents = mergeParent;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the message to set
	 */
	protected void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * Sets the original id.
	 * 
	 * @param originalId
	 *            the new original id
	 */
	protected void setOriginalId(final String originalId) {
		this.originalId = originalId;
	}
	
	/**
	 * Sets the revisions.
	 * 
	 * @param rCSRevisions
	 *            the revisions to set
	 */
	protected void setRevisions(final Collection<Revision> rCSRevisions) {
		this.revisions = rCSRevisions;
	}
	
	/**
	 * Sets the tags.
	 * 
	 * @param tagName
	 *            the new tags
	 */
	public void setTags(final Set<String> tagName) {
		this.tags = tagName;
	}
	
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	protected void setTimestamp(final DateTime timestamp) {
		this.javaTimestamp = timestamp;
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
			final Set<FieldKey> set = new HashSet<FieldKey>() {
				
				/**
                 * 
                 */
				private static final long serialVersionUID = 1L;
				
				{
					add(FieldKey.AUTHOR);
					add(FieldKey.BODY);
					add(FieldKey.CREATION_TIMESTAMP);
					add(FieldKey.DESCRIPTION);
					add(FieldKey.ID);
					add(FieldKey.SUMMARY);
					add(FieldKey.TYPE);
				}
			};
			
			return set;
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
			final Set<IterableFieldKey> set = new HashSet<IterableFieldKey>() {
				
				/**
                 * 
                 */
				private static final long serialVersionUID = 1L;
				
				{
					add(IterableFieldKey.COMMENTS);
					add(IterableFieldKey.FILES);
					add(IterableFieldKey.INVOLVED);
				}
			};
			
			return set;
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
		
		final StringBuilder string = new StringBuilder();
		string.append(getClassName());
		string.append(" [id="); //$NON-NLS-1$
		string.append(getId());
		string.append(", message="); //$NON-NLS-1$
		string.append(StringEscapeUtils.escapeJava(getMessage()));
		string.append(", timestamp="); //$NON-NLS-1$
		string.append(getTimestamp());
		string.append(", originalid="); //$NON-NLS-1$
		string.append(getOriginalId());
		string.append(", revisionCount="); //$NON-NLS-1$
		string.append(getRevisions().size());
		string.append(", author="); //$NON-NLS-1$
		string.append(getAuthor());
		string.append(", branchParents="); //$NON-NLS-1$
		if (getBranchParent() == null) {
			string.append("null"); //$NON-NLS-1$
		} else {
			string.append(getBranchParent().getId());
		}
		string.append(", mergeParents="); //$NON-NLS-1$
		string.append("["); //$NON-NLS-1$
		final StringBuilder builder2 = new StringBuilder();
		
		for (final ChangeSet changeSet : getMergeParents()) {
			if (builder2.length() > 0) {
				builder2.append(", "); //$NON-NLS-1$
			}
			builder2.append(changeSet.getId());
		}
		string.append(builder2.toString());
		string.append("]"); //$NON-NLS-1$
		
		string.append(", children="); //$NON-NLS-1$
		string.append("["); //$NON-NLS-1$
		final StringBuilder builder3 = new StringBuilder();
		
		for (final ChangeSet changeSet : getChildren()) {
			if (builder3.length() > 0) {
				builder3.append(", "); //$NON-NLS-1$
			}
			builder3.append(changeSet.getId());
		}
		string.append(builder3.toString());
		string.append("]"); //$NON-NLS-1$
		
		string.append("]"); //$NON-NLS-1$
		return string.toString();
	}
	
}

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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.openjpa.persistence.jdbc.Index;
import org.joda.time.DateTime;

import org.mozkito.exceptions.InvalidFieldKeyException;
import org.mozkito.persistence.FieldKey;
import org.mozkito.persistence.model.Artifact;
import org.mozkito.persons.model.Person;
import org.mozkito.persons.model.PersonContainer;
import org.mozkito.versions.exceptions.NoSuchHandleException;

/**
 * The Class Transaction.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "changeset")
public class ChangeSet extends Artifact {
	
	/** The Constant serialVersionUID. */
	private static final long    serialVersionUID = -7619009648634901112L;
	
	/** The persons. */
	private PersonContainer      persons          = new PersonContainer();
	
	/** The id. */
	private String               id;
	
	/** The message. */
	private String               message;
	
	/** The children. */
	private Set<ChangeSet>       children         = new HashSet<ChangeSet>();
	
	/** The branch parent. */
	private ChangeSet            branchParent     = null;
	
	/** The merge parent. */
	private List<ChangeSet>      mergeParents     = new LinkedList<ChangeSet>();
	
	/** The revisions. */
	private Collection<Revision> revisions        = new LinkedList<Revision>();
	
	/** The java timestamp. */
	private DateTime             javaTimestamp;
	
	/** The tags. */
	private Set<String>          tags             = new HashSet<String>();
	
	/** The original id. */
	private String               originalId;
	
	/** The atomic. */
	private boolean              atomic           = false;
	
	private VersionArchive       versionArchive;
	
	/** The branch indices. */
	private Map<String, Long>    branchIndices    = new HashMap<String, Long>();
	
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
	public ChangeSet(@NotNull final VersionArchive versionArchive, @NotNull final String id,
	        @NotNull final String message, @NotNull final DateTime timestamp, @NotNull final Person author,
	        final String originalId) {
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
	@Transient
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
	@Transient
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
	@Transient
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
	@Transient
	@NoneNull
	public void addMergeParent(final ChangeSet mergeParent) {
		// PRECONDITIONS
		try {
			this.mergeParents.add(mergeParent);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Adds the revision. Is automatically called from the constructor of Revision
	 * 
	 * @param rCSRevision
	 *            the revision
	 * @return true, if successful
	 */
	@Transient
	protected boolean addRevision(@NotNull final Revision rCSRevision) {
		final Collection<Revision> rCSRevisions = getRevisions();
		final boolean ret = rCSRevisions.add(rCSRevision);
		setRevisions(rCSRevisions);
		return ret;
	}
	
	/**
	 * Adds the tag.
	 * 
	 * @param tagName
	 *            the tag name
	 * @return true, if successful
	 */
	@Transient
	public boolean addTag(@NotNull final String tagName) {
		final boolean ret = false;
		final Set<String> tags = getTags();
		tags.add(tagName);
		setTags(tags);
		return ret;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws InvalidFieldKeyException
	 * 
	 * @see org.mozkito.persistence.model.Artifact#get(org.mozkito.persistence.FieldKey)
	 */
	@Override
	@Transient
	public <T> T get(final FieldKey key) throws InvalidFieldKeyException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			switch (key) {
				case AUTHOR:
					return (T) getAuthor();
				case BODY:
					return (T) getMessage();
				case CLOSED_TIMESTAMP:
					return (T) getTimestamp();
				case CLOSER:
					return (T) getAuthor();
				case CREATION_TIMESTAMP:
					return (T) getTimestamp();
				case FILE:
					// this should probably be a collection of mappings-files (own
					// class for mappings)
					return (T) getChangedFiles();
				case PATH:
					return (T) getChangedFiles();
				case RESOLUTION_TIMESTAMP:
					return (T) getTimestamp();
				case SUMMARY:
					return (T) getMessage();
				case ID:
					return (T) getId();
				case CHANGER:
					return (T) getAuthor();
				case COMMENT:
					return (T) getMessage();
				case MODIFICATION_TIMESTAMP:
					return (T) getTimestamp();
				default:
					break;
			}
			
			throw new InvalidFieldKeyException(key);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.model.Artifact#get(org.mozkito.persistence.FieldKey, int)
	 */
	@Override
	@Transient
	public <T> T get(final FieldKey key,
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
	 * Gets the author.
	 * 
	 * @return the author
	 */
	@Transient
	public Person getAuthor() {
		assert getPersons() != null;
		return getPersons().get("author"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.model.Artifact#getBaseType()
	 */
	@Override
	@Transient
	public Class<?> getBaseType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getBaseType' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the branch indices.
	 * 
	 * @return the branch indices
	 */
	@ElementCollection
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
	@Transient
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
	@ManyToOne (cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	public ChangeSet getBranchParent() {
		return this.branchParent;
	}
	
	/**
	 * Gets the changed files.
	 * 
	 * @return the changed files
	 */
	@Transient
	public Collection<Handle> getChangedFiles() {
		final List<Handle> changedFiles = new LinkedList<Handle>();
		for (final Revision rCSRevision : getRevisions()) {
			changedFiles.add(rCSRevision.getChangedFile());
		}
		return changedFiles;
	}
	
	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	// @Transient
	@ManyToMany (fetch = FetchType.LAZY, cascade = {})
	@JoinTable (name = "changeset_children", joinColumns = { @JoinColumn (nullable = true, name = "childrenid") })
	public Set<ChangeSet> getChildren() {
		return this.children != null
		                            ? this.children
		                            : new HashSet<ChangeSet>();
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Override
	@Id
	@Index (name = "idx_changesetid")
	public String getId() {
		return this.id;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "timestamp")
	@Index (name = "idx_timestamp")
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
	@ManyToMany (cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
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
	@Lob
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
	 * Gets the persons.
	 * 
	 * @return the persons
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public PersonContainer getPersons() {
		return this.persons;
	}
	
	/**
	 * Gets the revision that changed the specified path.
	 * 
	 * @param path
	 *            the path
	 * @return the revision for path if found. Returns <code>null</code> otherwise.
	 */
	@Transient
	public Revision getRevisionForPath(final String path) {
		String comparePath = path;
		if (path.startsWith("/")) {
			comparePath = path.substring(1);
		}
		for (final Revision revision : getRevisions()) {
			try {
				final String fileName = revision.getChangedFile().getPath(this);
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
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, targetEntity = Revision.class)
	public Collection<Revision> getRevisions() {
		return this.revisions != null
		                             ? this.revisions
		                             : new HashSet<Revision>();
	}
	
	/**
	 * Gets the tags.
	 * 
	 * @return the tag
	 */
	@ElementCollection
	public Set<String> getTags() {
		return this.tags;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.model.Artifact#getText()
	 */
	@Override
	@Transient
	public String getText() {
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
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.javaTimestamp;
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
	@Transient
	public void setAuthor(final Person author) {
		getPersons().add("author", author); //$NON-NLS-1$
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
	@Transient
	@NoneNull
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
	 * Sets the persons.
	 * 
	 * @param persons
	 *            the persons to set
	 */
	public void setPersons(final PersonContainer persons) {
		this.persons = persons;
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
	 * @see org.mozkito.persistence.model.Artifact#supported()
	 */
	@Override
	@Transient
	public Set<FieldKey> supported() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'supported' has not yet been implemented."); //$NON-NLS-1$
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

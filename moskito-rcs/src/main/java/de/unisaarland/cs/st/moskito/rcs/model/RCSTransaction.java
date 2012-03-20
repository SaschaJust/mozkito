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
package de.unisaarland.cs.st.moskito.rcs.model;

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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.openjpa.persistence.jdbc.Index;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;

/**
 * The Class RCSTransaction.Please use the {@link RCSTransaction#save(Session)} method to write instances of this Object
 * to database. The attached {@link RCSFile} will not be saved cascaded due to {@link RevisionPrimaryKey}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcstransaction")
public class RCSTransaction implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7619009648634901112L;
	
	/**
	 * Creates the transaction.
	 * 
	 * @param id
	 *            the id
	 * @param message
	 *            the message
	 * @param timestamp
	 *            the timestamp
	 * @param author
	 *            the author
	 * @return the rCS transaction
	 */
	public static RCSTransaction createTransaction(@NotNull final String id,
	                                               @NotNull final String message,
	                                               @NotNull final DateTime timestamp,
	                                               @NotNull final Person author,
	                                               final String originalId) {
		final RCSTransaction transaction = new RCSTransaction(id, message, timestamp, author, originalId);
		return transaction;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	@Transient
	public static String getHandle() {
		return RCSTransaction.class.getSimpleName();
	}
	
	private PersonContainer         persons       = new PersonContainer();
	private String                  id;
	private String                  message;
	private Set<RCSTransaction>     children      = new HashSet<RCSTransaction>();
	private RCSTransaction          branchParent  = null;
	private RCSTransaction          mergeParent   = null;
	private Collection<RCSRevision> revisions     = new LinkedList<RCSRevision>();
	private DateTime                javaTimestamp;
	private Set<String>             tags          = new HashSet<String>();
	private String                  originalId;
	private boolean                 atomic        = false;
	private Map<String, Long>       branchIndices = new HashMap<String, Long>();
	
	/**
	 * used by PersistenceUtil to create RCSTransaction instance.
	 */
	protected RCSTransaction() {
	}
	
	/**
	 * Instantiates a new rCS transaction.
	 * 
	 * @param id
	 *            the id
	 * @param message
	 *            the message
	 * @param timestamp
	 *            the timestamp
	 * @param author
	 *            the author
	 * @param previousRcsTransaction
	 *            the previous rcs transaction
	 */
	protected RCSTransaction(@NotNull final String id, @NotNull final String message,
	        @NotNull final DateTime timestamp, @NotNull final Person author, final String originalId) {
		setId(id);
		setMessage(message);
		setTimestamp(timestamp);
		setAuthor(author);
		setOriginalId(originalId);
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * Adds the all tags.
	 * 
	 * @param tagNames
	 *            the tag names
	 */
	@Transient
	public boolean addAllTags(final Collection<String> tagNames) {
		boolean ret = false;
		final Set<String> tags = getTags();
		ret = tags.addAll(tagNames);
		setTags(tags);
		return ret;
	}
	
	@Transient
	public boolean addBranch(final RCSBranch branch,
	                         final Long index) {
		if (this.branchIndices.containsKey(branch.getName())) {
			return false;
		}
		this.branchIndices.put(branch.getName(), index);
		return true;
	}
	
	/**
	 * @param rcsTransaction
	 */
	@Transient
	public boolean addChild(final RCSTransaction rcsTransaction) {
		CompareCondition.notEquals(rcsTransaction, this, "a transaction may never be a child of its own: %s", this);
		boolean ret = false;
		
		if (!getChildren().contains(rcsTransaction)) {
			final Set<RCSTransaction> children = getChildren();
			ret = children.add(rcsTransaction);
			setChildren(children);
		}
		
		return ret;
	}
	
	/**
	 * Adds the revision. Is automatically called from the constructor of RCSRevision
	 * 
	 * @param revision
	 *            the revision
	 * @return true, if successful
	 */
	@Transient
	protected boolean addRevision(@NotNull final RCSRevision revision) {
		final Collection<RCSRevision> revisions = getRevisions();
		final boolean ret = revisions.add(revision);
		setRevisions(revisions);
		return ret;
	}
	
	/**
	 * Adds the tag.
	 * 
	 * @param tagName
	 *            the tag name
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
	 * @return
	 */
	@Transient
	public Person getAuthor() {
		return getPersons().get("author");
	}
	
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
	 * @param branch
	 * @return
	 */
	@ManyToOne (cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	public RCSTransaction getBranchParent() {
		return this.branchParent;
	}
	
	/**
	 * Gets the changed files.
	 * 
	 * @return the changed files
	 */
	@Transient
	public Collection<RCSFile> getChangedFiles() {
		final List<RCSFile> changedFiles = new LinkedList<RCSFile>();
		for (final RCSRevision revision : getRevisions()) {
			changedFiles.add(revision.getChangedFile());
		}
		return changedFiles;
	}
	
	/**
	 * @return the children
	 */
	// @Transient
	@ManyToMany (fetch = FetchType.LAZY, cascade = {})
	@JoinTable (name = "rcstransaction_children", joinColumns = { @JoinColumn (nullable = true, name = "childrenid") })
	public Set<RCSTransaction> getChildren() {
		return this.children;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Index (name = "idx_transactionid")
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
	
	@ManyToOne (cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	public RCSTransaction getMergeParent() {
		// PRECONDITIONS
		
		try {
			return this.mergeParent;
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
	 * @return
	 */
	public String getOriginalId() {
		return this.originalId;
	}
	
	/**
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
	public RCSRevision getRevisionForPath(final String path) {
		for (final RCSRevision revision : getRevisions()) {
			if (revision.getChangedFile().equals(path)) {
				return revision;
			}
		}
		return null;
	}
	
	/**
	 * Gets the revisions.
	 * 
	 * @return the revisions
	 */
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, targetEntity = RCSRevision.class)
	public Collection<RCSRevision> getRevisions() {
		return this.revisions;
	}
	
	/**
	 * @return the tag
	 */
	@ElementCollection
	public Set<String> getTags() {
		return this.tags;
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
	
	@Column (columnDefinition = "boolean default 'FALSE'")
	public boolean isAtomic() {
		return this.atomic;
	}
	
	public void setAtomic(final boolean atomic) {
		this.atomic = atomic;
	}
	
	/**
	 * @param author
	 */
	@Transient
	public void setAuthor(final Person author) {
		getPersons().add("author", author);
	}
	
	public void setBranchIndices(final Map<String, Long> branchIndices) {
		// PRECONDITIONS
		try {
			this.branchIndices = branchIndices;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public void setBranchParent(final RCSTransaction branchParent) {
		// PRECONDITIONS
		try {
			this.branchParent = branchParent;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(final Set<RCSTransaction> children) {
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
	
	public void setMergeParent(final RCSTransaction mergeParent) {
		// PRECONDITIONS
		try {
			this.mergeParent = mergeParent;
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
	 * @param originalId
	 */
	protected void setOriginalId(final String originalId) {
		this.originalId = originalId;
	}
	
	/**
	 * @param persons
	 *            the persons to set
	 */
	public void setPersons(final PersonContainer persons) {
		this.persons = persons;
	}
	
	/**
	 * Sets the revisions.
	 * 
	 * @param revisions
	 *            the revisions to set
	 */
	protected void setRevisions(final Collection<RCSRevision> revisions) {
		this.revisions = revisions;
	}
	
	/**
	 * @param tagName
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder string = new StringBuilder();
		string.append("RCSTransaction [id=");
		string.append(getId());
		string.append(", message=");
		string.append(StringEscapeUtils.escapeJava(getMessage()));
		string.append(", timestamp=");
		string.append(getTimestamp());
		string.append(", originalid=");
		string.append(getOriginalId());
		string.append(", revisionCount=");
		string.append(getRevisions().size());
		string.append(", author=");
		string.append(getAuthor());
		string.append(", branchParents=");
		if (this.branchParent == null) {
			string.append("null");
		} else {
			string.append(this.branchParent.getId());
		}
		string.append(", mergeParents=");
		if (this.mergeParent == null) {
			string.append("null");
		} else {
			string.append(this.mergeParent.getId());
		}
		string.append(", children=");
		string.append("[");
		final StringBuilder builder2 = new StringBuilder();
		
		for (final RCSTransaction transaction : getChildren()) {
			if (builder2.length() > 0) {
				builder2.append(", ");
			}
			builder2.append(transaction.getId());
		}
		string.append(builder2.toString());
		string.append("]");
		
		string.append("]");
		return string.toString();
	}
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.openjpa.persistence.jdbc.Index;
import org.dom4j.Document;
import org.joda.time.DateTime;
import org.w3c.dom.html.HTMLDocument;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.output.Displayable;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.rcs.elements.PreviousTransactionIterator;

/**
 * The Class RCSTransaction.Please use the {@link RCSTransaction#save(Session)}
 * method to write instances of this Object to database. The attached
 * {@link RCSFile} will not be saved cascaded due to {@link RevisionPrimaryKey}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcstransaction")
public class RCSTransaction implements Annotated, Comparable<RCSTransaction>, Displayable {
	
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
	@NoneNull
	public static RCSTransaction createTransaction(final String id,
	                                               final String message,
	                                               final DateTime timestamp,
	                                               final Person author,
	                                               final String originalId) {
		return new RCSTransaction(id, message, timestamp, author, originalId);
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
	
	private PersonContainer         persons   = new PersonContainer();
	private String                  id;
	private String                  message;
	private Set<RCSTransaction>     children  = new HashSet<RCSTransaction>();
	private Set<RCSTransaction>     parents   = new HashSet<RCSTransaction>();
	private RCSBranch               branch    = RCSBranch.MASTER;
	private Collection<RCSRevision> revisions = new LinkedList<RCSRevision>();
	private DateTime                timestamp;
	private Set<String>             tags      = new HashSet<String>();
	private String                  originalId;
	private boolean                 atomic    = false;
	
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
	@NoneNull
	protected RCSTransaction(final String id, final String message, final DateTime timestamp, final Person author,
	                         final String originalId) {
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
		Set<String> tags = getTags();
		ret = tags.addAll(tagNames);
		setTags(tags);
		return ret;
	}
	
	/**
	 * @param rcsTransaction
	 */
	@Transient
	public boolean addChild(final RCSTransaction rcsTransaction) {
		CompareCondition.notEquals(rcsTransaction, this, "a transaction may never be a child of its own: %s", this);
		boolean ret = false;
		
		if (!getChildren().contains(rcsTransaction)) {
			Set<RCSTransaction> children = getChildren();
			ret = children.add(rcsTransaction);
			setChildren(children);
		}
		
		return ret;
	}
	
	/**
	 * @param parentTransaction
	 */
	@Transient
	public boolean addParent(final RCSTransaction parentTransaction) {
		CompareCondition.notEquals(parentTransaction, this, "a transaction may never be a parent of its own: %s", this);
		boolean ret = false;
		
		if (!getParents().contains(parentTransaction)) {
			Set<RCSTransaction> parents = getParents();
			ret = parents.add(parentTransaction);
			setParents(parents);
		}
		
		return ret;
	}
	
	/**
	 * Adds the revision. Is automatically called from the constructor of
	 * RCSRevision
	 * 
	 * @param revision
	 *            the revision
	 * @return true, if successful
	 */
	@Transient
	protected boolean addRevision(@NotNull final RCSRevision revision) {
		Collection<RCSRevision> revisions = getRevisions();
		boolean ret = revisions.add(revision);
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
		boolean ret = false;
		Set<String> tags = getTags();
		tags.add(tagName);
		setTags(tags);
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RCSTransaction transaction) {
		if (transaction == null) {
			return 1;
		}
		
		if (getBranch() == null) {
			throw new UnrecoverableError("Branch of a transaction should never be NULL");
		}
		if (transaction.getBranch() == null) {
			throw new UnrecoverableError("Branch of a transaction to be compared should never be NULL");
		}
		if (Logger.logDebug()) {
			Logger.debug("Comparing transactions: `" + getId() + "` and `" + transaction.getId() + "`");
		}
		if (equals(transaction)) {
			return 0;
		} else if (getBranch().equals(transaction.getBranch())) {
			if ((getBranch().getBegin() == null) || (transaction.getBranch().getBegin() == null)) {
				
				if (Logger.logWarn()) {
					Logger.warn("Comparing transactions of uninitialized branches: start transaction is unknown");
				}
				return -1;
			}
			if (getBranch().getBegin().equals(this)) {
				return -1;
			} else if (getBranch().getBegin().equals(transaction)) {
				return 1;
			} else {
				RCSTransaction cache = getParent(getBranch());
				if (cache == null) {
					return -1;
				}
				while (cache != getBranch().getBegin()) {
					if (cache.equals(transaction)) {
						return 1;
					}
					cache = cache.getParent(getBranch());
				}
				return -1;
			}
		} else if (getBranch().equals(RCSBranch.MASTER)) {
			if (Logger.logDebug()) {
				Logger.debug(transaction.getId() + " in " + transaction.getBranch().toString());
			}
			if ((transaction.getBranch().getEnd() == null)
					|| (transaction.getBranch().getEnd().getChild(transaction.getBranch()) == null)) {
				return -1;
			}
			int subresult = compareTo(transaction.getBranch().getEnd().getChild(transaction.getBranch()));
			if (subresult >= 0) {
				return 1;
			} else {
				return -1;
			}
		} else if (transaction.getBranch().equals(RCSBranch.MASTER)) {
			if ((getBranch().getEnd() == null) || (getBranch().getEnd().getChild(getBranch()) == null)) {
				return 1;
			}
			int sub_result = getBranch().getEnd().getChild(getBranch()).compareTo(transaction);
			if (sub_result <= 0) {
				return -1;
			} else {
				return 1;
			}
		} else {
			if ((transaction.getBranch().getEnd() == null)
					|| (transaction.getBranch().getEnd().getChild(transaction.getBranch()) == null)) {
				return -1;
			} else if ((getBranch().getEnd() == null) || (getBranch().getEnd().getChild(getBranch()) == null)) {
				return 1;
			} else {
				int r = getBranch().getEnd().getChild(getBranch())
				.compareTo(transaction.getBranch().getEnd().getChild(transaction.getBranch()));
				if (r != 0) {
					return r;
				} else {
					if (getTimestamp().isBefore(transaction.getTimestamp())) {
						return -1;
					} else if (getTimestamp().isAfter(transaction.getTimestamp())) {
						return 1;
					}
					return 0;
				}
			}
		}
	}
	
	/**
	 * @return
	 */
	@Transient
	public Person getAuthor() {
		return getPersons().get("author");
	}
	
	/**
	 * @return the branch
	 */
	@ManyToOne (fetch = FetchType.LAZY,
	            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
	            optional = false)
	            @JoinColumn (nullable = false)
	            public RCSBranch getBranch() {
		return branch;
	}
	
	/**
	 * Gets the changed files.
	 * 
	 * @return the changed files
	 */
	@Transient
	public Collection<RCSFile> getChangedFiles() {
		List<RCSFile> changedFiles = new LinkedList<RCSFile>();
		for (RCSRevision revision : getRevisions()) {
			changedFiles.add(revision.getChangedFile());
		}
		return changedFiles;
	}
	
	/**
	 * @param branch
	 * @return
	 */
	@Transient
	public RCSTransaction getChild(final RCSBranch branch) {
		if ((branch.getEnd() != null) && (branch.getEnd().equals(this))) {
			return getChildren().isEmpty()
			? null
			: getChildren().iterator().next();
		} else {
			return (RCSTransaction) CollectionUtils.find(getChildren(), new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					RCSTransaction transaction = (RCSTransaction) object;
					return transaction.getBranch().equals(branch);
				}
			});
		}
	}
	
	/**
	 * @return the children
	 */
	// @Transient
	@ManyToMany (fetch = FetchType.LAZY, cascade = {})
	@JoinTable (name = "rcstransaction_children", joinColumns = { @JoinColumn (nullable = true, name = "childrenid") })
	public Set<RCSTransaction> getChildren() {
		return children;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Index (name = "idx_transactionid")
	public String getId() {
		return id;
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
	 * Gets the message.
	 * 
	 * @return the message
	 */
	@Lob
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return
	 */
	public String getOriginalId() {
		return originalId;
	}
	
	/**
	 * @param branch
	 * @return
	 */
	@Transient
	public RCSTransaction getParent(final RCSBranch branch) {
		if (branch == null) {
			return null;
		}
		
		if ((branch.getBegin() != null) && branch.getBegin().equals(this)) {
			return getParents().isEmpty()
			? null
			: getParents().iterator().next();
		} else {
			return (RCSTransaction) CollectionUtils.find(getParents(), new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					RCSTransaction transaction = (RCSTransaction) object;
					if (transaction.getBranch() == null) {
						return false;
					}
					return transaction.getBranch().getName().equals(branch.getName());
				}
			});
		}
	}
	
	/**
	 * @return the parents
	 */
	@ManyToMany (fetch = FetchType.LAZY, cascade = {})
	@JoinTable (name = "rcstransaction_parents", joinColumns = { @JoinColumn (nullable = true, name = "parentsid") })
	public Set<RCSTransaction> getParents() {
		return parents;
	}
	
	/**
	 * @return the persons
	 */
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	public PersonContainer getPersons() {
		return persons;
	}
	
	@Transient
	public Iterator<RCSTransaction> getPreviousTransactions() {
		return new PreviousTransactionIterator(this);
	}
	
	/**
	 * Gets the revision that changed the specified path.
	 * 
	 * @param path
	 *            the path
	 * @return the revision for path if found. Returns <code>null</code>
	 *         otherwise.
	 */
	@Transient
	public RCSRevision getRevisionForPath(final String path) {
		for (RCSRevision revision : getRevisions()) {
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
		return revisions;
	}
	
	/**
	 * @return the tag
	 */
	@ElementCollection
	public Set<String> getTags() {
		return tags;
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return timestamp;
	}
	
	@Column (columnDefinition = "boolean default 'FALSE'")
	public boolean isAtomic() {
		return atomic;
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
	
	/**
	 * @param branch
	 *            the branch to set
	 */
	public void setBranch(@NotNull ("You cannot set the branch of a transaction to NULL") final RCSBranch branch) {
		this.branch = branch;
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
		timestamp = date != null
		? new DateTime(date)
		: null;
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
	 * @param parents
	 *            the parents to set
	 */
	public void setParents(final Set<RCSTransaction> parents) {
		this.parents = parents;
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
		tags = tagName;
	}
	
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	protected void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toCSV() {
		// REMARK Auto-generated method stub
		return null;
	}
	
	@Override
	public void toCSV(final OutputStream stream) {
		// REMARK Auto-generated method stub
		
	}
	
	@Override
	public HTMLDocument toHTML() {
		// REMARK Auto-generated method stub
		return null;
	}
	
	@Override
	public void toHTML(final OutputStream stream) {
		// REMARK Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder string = new StringBuilder();
		string.append("RCSTransaction [id=");
		string.append(getId());
		string.append(", message=");
		string.append(StringEscapeUtils.escapeJava(getMessage()));
		string.append(", timestamp=");
		string.append(getTimestamp());
		string.append(", revisionCount=");
		string.append(getRevisions().size());
		string.append(", author=");
		string.append(getAuthor());
		string.append(", parents=");
		string.append("[");
		StringBuilder builder = new StringBuilder();
		
		for (RCSTransaction transaction : getParents()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(transaction.getId());
		}
		string.append(builder.toString());
		string.append("]");
		
		string.append(", children=");
		string.append("[");
		StringBuilder builder2 = new StringBuilder();
		
		for (RCSTransaction transaction : getChildren()) {
			if (builder2.length() > 0) {
				builder2.append(", ");
			}
			builder2.append(transaction.getId());
		}
		string.append(builder2.toString());
		string.append("]");
		
		if (getBranch() != null) {
			string.append(", branch=");
			string.append(getBranch());
		}
		string.append("]");
		return string.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.output.Displayable#toText()
	 */
	@Override
	public String toTerm() {
		return null;
	}
	
	@Override
	public void toTerm(final OutputStream stream) {
		try {
			stream.write(this.toTerm().getBytes());
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public String toText() {
		return null;
	}
	
	@Override
	public void toText(final OutputStream stream) {
		// REMARK Auto-generated method stub
		
	}
	
	@Override
	public Document toXML() {
		// REMARK Auto-generated method stub
		return null;
	}
	
	@Override
	public void toXML(final OutputStream stream) {
		// REMARK Auto-generated method stub
		
	}
	
}

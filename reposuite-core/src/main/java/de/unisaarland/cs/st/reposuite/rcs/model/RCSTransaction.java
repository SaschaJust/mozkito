/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Session;
import org.hibernate.annotations.Index;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

/**
 * The Class RCSTransaction.Please use the {@link RCSTransaction#save(Session)}
 * method to write instances of this Object to database. The attached
 * {@link RCSFile} will not be saved cascaded due to {@link RevisionPrimaryKey}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcstransaction")
public class RCSTransaction implements Annotated, Comparable<RCSTransaction> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7619009648634901112L;
	
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
	
	/**
	 * used by Hibernate to create RCSTransaction instance.
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
	public RCSTransaction(final String id, final String message, final DateTime timestamp, final Person author) {
		Condition.notNull(id);
		Condition.notNull(message);
		Condition.notNull(timestamp);
		Condition.notNull(author);
		
		setId(id);
		setMessage(message);
		setTimestamp(timestamp);
		setAuthor(author);
		author.assignTransaction(this);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * Adds the all tags.
	 *
	 * @param tagNames the tag names
	 */
	@Transient
	public void addAllTags(final Collection<String> tagNames) {
		tags.addAll(tagNames);
	}
	
	/**
	 * @param rcsTransaction
	 */
	@Transient
	public void addChild(final RCSTransaction rcsTransaction) {
		if (!children.contains(rcsTransaction)) {
			children.add(rcsTransaction);
		}
	}
	
	/**
	 * @param parentTransaction
	 */
	@Transient
	public void addParent(final RCSTransaction parentTransaction) {
		if (!parents.contains(parentTransaction)) {
			parents.add(parentTransaction);
		}
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
	@NoneNull
	protected boolean addRevision(final RCSRevision revision) {
		Condition.notNull(revision);
		return getRevisions().add(revision);
	}
	
	/**
	 * Adds the tags.
	 *
	 * @param tagName the tag name
	 */
	@Transient
	public void addTags(final String tagName) {
		tags.add(tagName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RCSTransaction transaction) {
		if (transaction == null) {
			return 1;
		} else if (equals(transaction)) {
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
				while (cache != getBranch().getBegin()) {
					if (cache.equals(transaction)) {
						return 1;
					}
					cache = cache.getParent(getBranch());
				}
				return -1;
			}
		} else if (getBranch().equals(RCSBranch.MASTER)) {
			return -1;
		} else if (transaction.getBranch().equals(RCSBranch.MASTER)) {
			return 1;
		} else {
			// since none of the branches is a master branch, every
			// getBegin() transaction must have exactly one parent.
			return getBranch().getBegin().getParents().iterator().next()
			.compareTo(transaction.getBranch().getBegin().getParents().iterator().next());
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
	@ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	@JoinColumn (nullable = false)
	public RCSBranch getBranch() {
		return branch;
	}
	
	/**
	 * @param branch
	 * @return
	 */
	@Transient
	public RCSTransaction getChild(final RCSBranch branch) {
		if (branch.getEnd().equals(this)) {
			return getChildren().isEmpty()
			? null
					: getChildren().iterator().next();
		} else {
			return (RCSTransaction) CollectionUtils.find(children, new Predicate() {
				
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
	@ManyToMany (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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
	private Date getJavaTimestamp() {
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
	 * @param branch
	 * @return
	 */
	@Transient
	public RCSTransaction getParent(final RCSBranch branch) {
		if (branch.getBegin().equals(this)) {
			return getParents().isEmpty()
			? null
					: getParents().iterator().next();
		} else {
			return (RCSTransaction) CollectionUtils.find(parents, new Predicate() {
				
				@Override
				public boolean evaluate(final Object object) {
					RCSTransaction transaction = (RCSTransaction) object;
					return transaction.getBranch().equals(branch);
				}
			});
		}
	}
	
	/**
	 * @return the parents
	 */
	@ManyToMany (fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinTable (name = "rcstransaction_parents", joinColumns = { @JoinColumn (nullable = true, name = "parentsid") })
	public Set<RCSTransaction> getParents() {
		return parents;
	}
	
	/**
	 * @return the persons
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public PersonContainer getPersons() {
		return persons;
	}
	
	/**
	 * Gets the revisions.
	 * 
	 * @return the revisions
	 */
	@OneToMany (cascade = { CascadeType.ALL }, mappedBy = "primaryKey.transaction", fetch = FetchType.LAZY)
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
	
	@SuppressWarnings ("unchecked")
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		Collection<Annotated> ret = new LinkedList<Annotated>();
		
		ret.addAll(CollectionUtils.collect(getRevisions(), new Transformer() {
			
			@Override
			public Object transform(final Object input) {
				RCSRevision revision = (RCSRevision) input;
				return revision.getChangedFile();
			}
		}));
		
		ret.add(persons);
		// ret.add(getBranch());
		//
		// for (Collection<Annotated> coll : (Collection<Collection<Annotated>>)
		// CollectionUtils.collect(getParents(),
		// new Transformer() {
		//
		// @Override
		// public Object transform(final Object input) {
		// RCSTransaction transaction = (RCSTransaction) input;
		// return transaction.saveFirst();
		// }
		// })) {
		// if (coll != null) {
		// ret.addAll(coll);
		// }
		// }
		
		// for (Collection<Annotated> coll : (Collection<Collection<Annotated>>)
		// CollectionUtils.collect(getChildren(),
		// new Transformer() {
		//
		// @Override
		// public Object transform(final Object input) {
		// RCSTransaction transaction = (RCSTransaction) input;
		// return transaction.saveFirst();
		// }
		// })) {
		// if (coll != null) {
		// ret.addAll(coll);
		// }
		// }
		
		return ret;
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
	public void setBranch(final RCSBranch branch) {
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
	private void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @param date
	 *            the new java timestamp
	 */
	@SuppressWarnings ("unused")
	private void setJavaTimestamp(final Date date) {
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
	private void setMessage(final String message) {
		this.message = message;
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
	@SuppressWarnings ("unused")
	private void setRevisions(final List<RCSRevision> revisions) {
		this.revisions = revisions;
	}
	
	/**
	 * @param tagName
	 */
	public void setTags(final Set<String> tagName) {
		tags = new HashSet<String>(tagName);
	}
	
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	private void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
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
		if (branch != null) {
			string.append(", branch=");
			string.append(branch);
		}
		string.append("]");
		return string.toString();
	}
	
}

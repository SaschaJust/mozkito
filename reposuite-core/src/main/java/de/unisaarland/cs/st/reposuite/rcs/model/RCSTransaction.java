/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Session;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	
	private RCSTransaction          nextTransaction;
	private RCSTransaction          prevTransaction;
	private RCSTransaction          nextTransactionByTimestamp;               // following
	                                                                           // the
	                                                                           // branch
	                                                                           // path
	private RCSTransaction          prevTransactionByTimestamp;               // following
	                                                                           // the
	                                                                           // branch
	                                                                           // path
	private RCSBranch               branch;
	private Collection<RCSRevision> revisions = new LinkedList<RCSRevision>();
	private DateTime                timestamp;
	
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
	public RCSTransaction(final String id, final String message, final DateTime timestamp, final Person author,
	        final RCSTransaction previousRcsTransaction) {
		Condition.notNull(id);
		Condition.notNull(message);
		Condition.notNull(timestamp);
		Condition.notNull(author);
		
		setId(id);
		setMessage(message);
		setTimestamp(timestamp);
		setAuthor(author);
		setPrevTransaction(previousRcsTransaction);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
		
		Condition.check((getPrevTransactionByTimestamp() == null) || (compareTo(getPrevTransactionByTimestamp()) >= 0));
	}
	
	/**
	 * Adds the revision.
	 * 
	 * @param revision
	 *            the revision
	 * @return true, if successful
	 */
	@Transient
	public boolean addRevision(final RCSRevision revision) {
		Condition.notNull(revision);
		return getRevisions().add(revision);
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
		} else if (getTimestamp().equals(transaction.getTimestamp())) {
			RCSTransaction currentTransaction = this;
			while (currentTransaction.getPrevTransaction() != null) {
				if (currentTransaction.getPrevTransaction().equals(transaction)) {
					return 1;
				} else if (currentTransaction.getPrevTransaction().getTimestamp().isBefore(transaction.getTimestamp())) {
					return -1;
				} else if (currentTransaction.getPrevTransaction().getTimestamp().isAfter(transaction.getTimestamp())) {
					if (Logger.logError()) {
						Logger.error("Found previous transaction with larger timestamp then current: " + toString()
						        + " vs " + currentTransaction.getPrevTransaction().toString());
					}
					
					throw new RuntimeException();
				}
				
				currentTransaction = currentTransaction.getPrevTransaction();
			}
			
			return -1;
		} else {
			return getTimestamp().isAfter(transaction.getTimestamp()) ? 1 : -1;
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
	// @Column (updatable = false)
	@OneToOne (fetch = FetchType.LAZY)
	public RCSBranch getBranch() {
		return this.branch;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	public String getId() {
		return this.id;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	@SuppressWarnings ("unused")
	@Temporal (TemporalType.TIMESTAMP)
	@Column (name = "timestamp")
	private Date getJavaTimestamp() {
		return getTimestamp() != null ? getTimestamp().toDate() : null;
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
	 * @return the nextTransaction
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getNextTransaction() {
		return this.nextTransaction;
	}
	
	/**
	 * @return the nextTransactionByTimestamp
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getNextTransactionByTimestamp() {
		return this.nextTransactionByTimestamp;
	}
	
	/**
	 * @return the persons
	 */
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public PersonContainer getPersons() {
		return this.persons;
	}
	
	/**
	 * @return the prevTransaction
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getPrevTransaction() {
		return this.prevTransaction;
	}
	
	/**
	 * @return the prevTransactionByTimestamp
	 */
	@OneToOne (fetch = FetchType.LAZY)
	public RCSTransaction getPrevTransactionByTimestamp() {
		return this.prevTransactionByTimestamp;
	}
	
	/**
	 * Gets the revisions.
	 * 
	 * @return the revisions
	 */
	@OneToMany (cascade = { CascadeType.ALL }, mappedBy = "primaryKey.transaction", fetch = FetchType.LAZY)
	public Collection<RCSRevision> getRevisions() {
		return this.revisions;
	}
	
	/**
	 * Gets the timestamp.
	 * 
	 * @return the timestamp
	 */
	@Transient
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		Collection<Annotated> ret = CollectionUtils.collect(getRevisions(), new Transformer() {
			
			@Override
			public Object transform(final Object input) {
				RCSRevision revision = (RCSRevision) input;
				return revision.getChangedFile();
			}
		});
		ret.addAll(this.persons.getPersons());
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
		this.timestamp = date != null ? new DateTime(date) : null;
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
	 * @param nextTransaction
	 *            the nextTransaction to set
	 */
	public void setNextTransaction(final RCSTransaction nextTransaction) {
		this.nextTransaction = nextTransaction;
	}
	
	/**
	 * @param nextTransactionByTimestamp
	 *            the nextTransactionByTimestamp to set
	 */
	public void setNextTransactionByTimestamp(final RCSTransaction nextTransactionByTimestamp) {
		this.nextTransactionByTimestamp = nextTransactionByTimestamp;
	}
	
	/**
	 * @param persons
	 *            the persons to set
	 */
	public void setPersons(final PersonContainer persons) {
		this.persons = persons;
	}
	
	/**
	 * @param prevTransaction
	 *            the prevTransaction to set
	 */
	public void setPrevTransaction(final RCSTransaction prevTransaction) {
		this.prevTransaction = prevTransaction;
	}
	
	/**
	 * @param prevTransactionByTimestamp
	 *            the prevTransactionByTimestamp to set
	 */
	public void setPrevTransactionByTimestamp(final RCSTransaction prevTransactionByTimestamp) {
		this.prevTransactionByTimestamp = prevTransactionByTimestamp;
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
		return "RCSTransaction [id=" + getId() + ", message=" + StringEscapeUtils.escapeJava(getMessage())
		        + ", timestamp=" + getTimestamp() + ", revisionCount=" + getRevisions().size() + ", author="
		        + getAuthor() + "]";
	}
	
}

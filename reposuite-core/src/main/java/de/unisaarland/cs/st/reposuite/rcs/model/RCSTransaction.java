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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * The Class RCSTransaction.Please use the {@link RCSTransaction#save(Session)}
 * method to write instances of this Object to database. The attached
 * {@link RCSFile} will not be saved cascaded due to {@link RevisionPrimaryKey}.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table(name = "rcstransaction")
public class RCSTransaction implements Annotated, Comparable<RCSTransaction> {
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	@Transient
	public static String getHandle() {
		return RCSTransaction.class.getSimpleName();
	}
	
	private Person                  author;
	private String                  id;
	private String                  message;
	private RCSTransaction          previousRCSRcsTransaction;
	private Collection<RCSRevision> revisions = new LinkedList<RCSRevision>();
	
	private DateTime                timestamp;
	
	/**
	 * used by Hibernate to create RCSTransaction instance.
	 */
	@SuppressWarnings("unused")
	private RCSTransaction() {
		
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
		assert (id != null);
		assert (message != null);
		assert (timestamp != null);
		assert (author != null);
		
		this.id = id;
		this.message = message;
		this.timestamp = timestamp;
		this.author = author;
		previousRCSRcsTransaction = previousRcsTransaction;
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
		
		assert ((previousRcsTransaction == null) || (compareTo(previousRcsTransaction) >= 0));
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
		assert (revision != null);
		return revisions.add(revision);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RCSTransaction transaction) {
		if (transaction == null) {
			return 1;
		} else if (equals(transaction)) {
			return 0;
		} else if (timestamp.equals(transaction.timestamp)) {
			RCSTransaction currentTransaction = this;
			while (currentTransaction.previousRCSRcsTransaction != null) {
				if (currentTransaction.previousRCSRcsTransaction.equals(transaction)) {
					return 1;
				} else if (currentTransaction.previousRCSRcsTransaction.timestamp.isBefore(transaction.timestamp)) {
					return -1;
				} else if (currentTransaction.previousRCSRcsTransaction.timestamp.isAfter(transaction.timestamp)) {
					if (Logger.logError()) {
						Logger.error("Found previous transaction with larger timestamp then current: " + toString()
						        + " vs " + currentTransaction.previousRCSRcsTransaction.toString());
					}
					
					throw new RuntimeException();
				}
				
				currentTransaction = currentTransaction.previousRCSRcsTransaction;
			}
			
			return -1;
		} else {
			return timestamp.isAfter(transaction.timestamp) ? 1 : -1;
		}
	}
	
	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Person getAuthor() {
		return author;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	public String getId() {
		return id;
	}
	
	/**
	 * Gets the java timestamp.
	 * 
	 * @return the java timestamp
	 */
	@SuppressWarnings("unused")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp")
	private Date getJavaTimestamp() {
		return timestamp.toDate();
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets the previous rcs rcs transaction.
	 * 
	 * @return the previousRCSTransaction
	 */
	@OneToOne(fetch = FetchType.LAZY)
	public RCSTransaction getPreviousRCSRcsTransaction() {
		return previousRCSRcsTransaction;
	}
	
	/**
	 * Gets the revisions.
	 * 
	 * @return the revisions
	 */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "primaryKey.transaction", fetch = FetchType.LAZY)
	public Collection<RCSRevision> getRevisions() {
		return revisions;
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
	
	/**
	 * Save this RCSTransaction using Hibernate. This method should used instead
	 * of <code>Session.save(RCSTransaction)</code> since one has to save the
	 * attached <code>RCSFiles</code> before.
	 * 
	 * @param session
	 *            the session to be used to save the Objects
	 */
	public void save(Session session) {
		session.beginTransaction();
		for (RCSRevision rev : getRevisions()) {
			session.saveOrUpdate(rev.getChangedFile());
		}
		session.saveOrUpdate(this);
	}
	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the author to set
	 */
	@SuppressWarnings("unused")
	private void setAuthor(final Person author) {
		this.author = author;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	@SuppressWarnings("unused")
	private void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * Sets the java timestamp.
	 * 
	 * @param date
	 *            the new java timestamp
	 */
	@SuppressWarnings("unused")
	private void setJavaTimestamp(final Date date) {
		timestamp = new DateTime(date);
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the message to set
	 */
	@SuppressWarnings("unused")
	private void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * Sets the previous rcs rcs transaction.
	 * 
	 * @param previousRCSRcsTransaction
	 *            the previousRCSRcsTransaction to set
	 */
	@SuppressWarnings("unused")
	private void setPreviousRCSRcsTransaction(final RCSTransaction previousRCSRcsTransaction) {
		this.previousRCSRcsTransaction = previousRCSRcsTransaction;
	}
	
	/**
	 * Sets the revisions.
	 * 
	 * @param revisions
	 *            the revisions to set
	 */
	@SuppressWarnings("unused")
	private void setRevisions(final List<RCSRevision> revisions) {
		this.revisions = revisions;
	}
	
	/**
	 * Sets the timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp to set
	 */
	@SuppressWarnings("unused")
	private void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSTransaction [id=" + id + ", message=" + message + ", timestamp=" + timestamp + ", revisions="
		        + revisions + ", author=" + author + "]";
	}
	
}

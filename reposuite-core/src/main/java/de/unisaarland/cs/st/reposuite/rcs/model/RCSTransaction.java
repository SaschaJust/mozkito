/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class RCSTransaction implements Annotated, Comparable<RCSTransaction> {
	
	private String                  id;
	private String                  message;
	private DateTime                timestamp;
	private Collection<RCSRevision> revisions = new LinkedList<RCSRevision>();
	private Person                  author;
	private RCSTransaction          previousRCSRcsTransaction;
	
	/**
	 * @param id
	 * @param message
	 * @param timestamp
	 * @param author
	 * @param revisions
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
		this.previousRCSRcsTransaction = previousRcsTransaction;
		
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
		
		assert ((previousRcsTransaction == null) || (this.compareTo(previousRcsTransaction) >= 0));
	}
	
	/**
	 * @param revision
	 * @return
	 */
	@Transient
	protected boolean addRevision(final RCSRevision revision) {
		assert (revision != null);
		return this.revisions.add(revision);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final RCSTransaction transaction) {
		if (transaction == null) {
			return 1;
		} else if (this.equals(transaction)) {
			return 0;
		} else if (this.timestamp.equals(transaction.timestamp)) {
			RCSTransaction currentTransaction = this;
			while (currentTransaction.previousRCSRcsTransaction != null) {
				if (currentTransaction.previousRCSRcsTransaction.equals(transaction)) {
					return 1;
				} else if (currentTransaction.previousRCSRcsTransaction.timestamp.isBefore(transaction.timestamp)) {
					return -1;
				} else if (currentTransaction.previousRCSRcsTransaction.timestamp.isAfter(transaction.timestamp)) {
					if (RepoSuiteSettings.logError()) {
						Logger.error("Found previous transaction with larger timestamp then current: "
						        + this.toString() + " vs " + currentTransaction.previousRCSRcsTransaction.toString());
					}
					
					throw new RuntimeException();
				}
				
				currentTransaction = currentTransaction.previousRCSRcsTransaction;
			}
			
			return -1;
		} else {
			return this.timestamp.isAfter(transaction.timestamp) ? 1 : -1;
		}
	}
	
	/**
	 * @return the author
	 */
	public Person getAuthor() {
		return this.author;
	}
	
	/**
	 * @return the simple class name
	 */
	private String getHandle() {
		return RCSTransaction.class.getSimpleName();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * @return the previousRCSRcsTransaction
	 */
	public RCSTransaction getPreviousRCSRcsTransaction() {
		return this.previousRCSRcsTransaction;
	}
	
	/**
	 * @return the revisions
	 */
	public Collection<RCSRevision> getRevisions() {
		return this.revisions;
	}
	
	/**
	 * @return the timestamp
	 */
	public DateTime getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param author
	 *            the author to set
	 */
	@SuppressWarnings ("unused")
	private void setAuthor(final Person author) {
		this.author = author;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	@SuppressWarnings ("unused")
	private void setId(final String id) {
		this.id = id;
	}
	
	/**
	 * @param message
	 *            the message to set
	 */
	@SuppressWarnings ("unused")
	private void setMessage(final String message) {
		this.message = message;
	}
	
	/**
	 * @param previousRCSRcsTransaction
	 *            the previousRCSRcsTransaction to set
	 */
	@SuppressWarnings ("unused")
	private void setPreviousRCSRcsTransaction(final RCSTransaction previousRCSRcsTransaction) {
		this.previousRCSRcsTransaction = previousRCSRcsTransaction;
	}
	
	/**
	 * @param revisions
	 *            the revisions to set
	 */
	@SuppressWarnings ("unused")
	private void setRevisions(final List<RCSRevision> revisions) {
		this.revisions = revisions;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	@SuppressWarnings ("unused")
	private void setTimestamp(final DateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSTransaction [id=" + this.id + ", message=" + this.message + ", timestamp=" + this.timestamp
		        + ", revisions=" + this.revisions + ", author=" + this.author + "]";
	}
	
}

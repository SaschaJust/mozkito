/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class RCSRevision implements Annotated, Comparable<RCSRevision> {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RCSRevision.class.getSimpleName();
	}
	
	private RCSFile            changedFile;
	private ChangeType         changeType;
	private RCSTransaction     previousTransaction;
	private RCSTransaction     transaction;
	
	private RevisionPrimaryKey primaryKey;
	
	/**
	 * used by Hibernate to instantiate a {@link RCSRevision} object
	 */
	@SuppressWarnings ("unused")
	private RCSRevision() {
		
	}
	
	public RCSRevision(final RCSTransaction rcsTransaction, final RCSFile rcsFile, final ChangeType changeType,
	        final RCSTransaction previousRcsTransaction) {
		assert (rcsTransaction != null);
		assert (rcsFile != null);
		assert (changeType != null);
		
		this.transaction = rcsTransaction;
		this.changedFile = rcsFile;
		this.changeType = changeType;
		this.previousTransaction = previousRcsTransaction;
		this.transaction.addRevision(this);
		this.primaryKey = new RevisionPrimaryKey(this.changedFile, this.transaction);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	@Transient
	public int compareTo(final RCSRevision rcsRevision) {
		assert (rcsRevision != null);
		return this.transaction.compareTo(rcsRevision.transaction);
	}
	
	/**
	 * @return the changedFile
	 */
	@Transient
	public RCSFile getChangedFile() {
		return this.changedFile;
	}
	
	/**
	 * @return the changeType
	 */
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/**
	 * @return the previousTransaction
	 */
	@ManyToOne
	public RCSTransaction getPreviousTransaction() {
		return this.previousTransaction;
	}
	
	@EmbeddedId
	public RevisionPrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}
	
	/**
	 * @return the transaction
	 */
	@Transient
	public RCSTransaction getTransaction() {
		return this.transaction;
	}
	
	/**
	 * @param changeType
	 *            the changeType to set
	 */
	@SuppressWarnings ("unused")
	private void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @param previousTransaction
	 *            the previousTransaction to set
	 */
	@SuppressWarnings ("unused")
	private void setPreviousTransaction(final RCSTransaction previousTransaction) {
		this.previousTransaction = previousTransaction;
	}
	
	@SuppressWarnings ("unused")
	private void setPrimaryKey(final RevisionPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSRevision [transactionId=" + this.transaction.getId() + ", changedFile=" + this.changedFile
		        + ", changeType=" + this.changeType + ", previousTransactionId="
		        + (this.previousTransaction != null ? this.previousTransaction.getId() : "(null)") + "]";
	}
}

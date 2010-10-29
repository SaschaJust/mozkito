/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.Entity;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class RCSRevision implements Annotated, Comparable<RCSRevision> {
	
	private RCSTransaction transaction;
	private RCSFile        changedFile;
	private ChangeType     changeType;
	private RCSTransaction previousTransaction;
	
	public RCSRevision(RCSTransaction rcsTransaction, RCSFile rcsFile, ChangeType changeType,
	        RCSTransaction previousRcsTransaction) {
		assert (rcsTransaction != null);
		assert (rcsFile != null);
		assert (changeType != null);
		assert (previousRcsTransaction != null);
		
		this.transaction = rcsTransaction;
		this.changedFile = rcsFile;
		this.changeType = changeType;
		this.previousTransaction = previousRcsTransaction;
		this.transaction.addRevision(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RCSRevision rcsRevision) {
		assert (rcsRevision != null);
		return this.transaction.compareTo(rcsRevision.transaction);
	}
	
	/**
	 * @return the changedFile
	 */
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
	public RCSTransaction getPreviousTransaction() {
		return this.previousTransaction;
	}
	
	/**
	 * @return the transaction
	 */
	public RCSTransaction getTransaction() {
		return this.transaction;
	}
	
	/**
	 * @param changedFile
	 *            the changedFile to set
	 */
	@SuppressWarnings("unused")
	private void setChangedFile(RCSFile path) {
		this.changedFile = path;
	}
	
	/**
	 * @param changeType
	 *            the changeType to set
	 */
	@SuppressWarnings("unused")
	private void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @param previousTransaction
	 *            the previousTransaction to set
	 */
	@SuppressWarnings("unused")
	private void setPreviousTransaction(RCSTransaction previousTransaction) {
		this.previousTransaction = previousTransaction;
	}
	
	/**
	 * @param transaction
	 *            the transaction to set
	 */
	@SuppressWarnings("unused")
	private void setTransaction(RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSRevision [transactionId=" + this.transaction.getId() + ", changedFile=" + this.changedFile
		        + ", changeType=" + this.changeType + ", previousTransactionId="
		        + (this.previousTransaction != null ? this.previousTransaction.getId() : "(null)") + "]";
	}
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table(name = "rcsrevision")
@AssociationOverrides({
        @AssociationOverride(name = "primaryKey.changedFile", joinColumns = @JoinColumn(name = "changedFile_id")),
        @AssociationOverride(name = "primaryKey.transaction", joinColumns = @JoinColumn(name = "transaction_id")) })
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
	@SuppressWarnings("unused")
	private RCSRevision() {
		
	}
	
	public RCSRevision(final RCSTransaction rcsTransaction, final RCSFile rcsFile, final ChangeType changeType,
	        final RCSTransaction previousRcsTransaction) {
		assert (rcsTransaction != null);
		assert (rcsFile != null);
		assert (changeType != null);
		
		transaction = rcsTransaction;
		changedFile = rcsFile;
		this.changeType = changeType;
		previousTransaction = previousRcsTransaction;
		transaction.addRevision(this);
		primaryKey = new RevisionPrimaryKey(changedFile, transaction);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	@Transient
	public int compareTo(final RCSRevision rcsRevision) {
		assert (rcsRevision != null);
		return transaction.compareTo(rcsRevision.transaction);
	}
	
	/**
	 * @return the changedFile
	 */
	@Transient
	public RCSFile getChangedFile() {
		return changedFile;
	}
	
	/**
	 * @return the changeType
	 */
	public ChangeType getChangeType() {
		return changeType;
	}
	
	/**
	 * @return the previousTransaction
	 */
	@ManyToOne
	public RCSTransaction getPreviousTransaction() {
		return previousTransaction;
	}
	
	@EmbeddedId
	public RevisionPrimaryKey getPrimaryKey() {
		return primaryKey;
	}
	
	/**
	 * @return the transaction
	 */
	@Transient
	public RCSTransaction getTransaction() {
		return transaction;
	}
	
	/**
	 * @param changeType
	 *            the changeType to set
	 */
	@SuppressWarnings("unused")
	private void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @param previousTransaction
	 *            the previousTransaction to set
	 */
	@SuppressWarnings("unused")
	private void setPreviousTransaction(final RCSTransaction previousTransaction) {
		this.previousTransaction = previousTransaction;
	}
	
	@SuppressWarnings("unused")
	private void setPrimaryKey(final RevisionPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSRevision [transactionId=" + transaction.getId() + ", changedFile=" + changedFile + ", changeType="
		        + changeType + ", previousTransactionId="
		        + (previousTransaction != null ? previousTransaction.getId() : "(null)") + "]";
	}
}

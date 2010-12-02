/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "rcsrevision")
@AssociationOverrides ({
	@AssociationOverride (name = "primaryKey.changedFile", joinColumns = @JoinColumn (name = "changedFile_id")),
	@AssociationOverride (name = "primaryKey.transaction", joinColumns = @JoinColumn (name = "transaction_id")) })
	public class RCSRevision implements Annotated, Comparable<RCSRevision> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2149118675856446526L;
	
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
	protected RCSRevision() {
		
	}
	
	public RCSRevision(final RCSTransaction rcsTransaction, final RCSFile rcsFile, final ChangeType changeType,
			final RCSTransaction previousRcsTransaction) {
		Condition.notNull(rcsTransaction);
		Condition.notNull(rcsFile);
		Condition.notNull(changeType);
		
		setTransaction(rcsTransaction);
		rcsTransaction.addRevision(this);
		setChangedFile(rcsFile);
		setChangeType(changeType);
		setPreviousTransaction(previousRcsTransaction);
		getTransaction().addRevision(this);
		setPrimaryKey(new RevisionPrimaryKey(getChangedFile(), getTransaction()));
		
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
		Condition.notNull(rcsRevision);
		return getTransaction().compareTo(rcsRevision.getTransaction());
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
	@ManyToOne (cascade = CascadeType.ALL)
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
	
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param changedFile
	 *            the changedFile to set
	 */
	private void setChangedFile(final RCSFile changedFile) {
		this.changedFile = changedFile;
	}
	
	/**
	 * @param changeType
	 *            the changeType to set
	 */
	private void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @param previousTransaction
	 *            the previousTransaction to set
	 */
	private void setPreviousTransaction(final RCSTransaction previousTransaction) {
		this.previousTransaction = previousTransaction;
	}
	
	/**
	 * @param primaryKey
	 */
	private void setPrimaryKey(final RevisionPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * @param transaction
	 *            the transaction to set
	 */
	private void setTransaction(final RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSRevision [transactionId=" + getTransaction().getId() + ", changedFile=" + getChangedFile()
		+ ", changeType=" + getChangeType() + ", previousTransactionId="
		+ (getPreviousTransaction() != null ? getPreviousTransaction().getId() : "(null)") + "]";
	}
}

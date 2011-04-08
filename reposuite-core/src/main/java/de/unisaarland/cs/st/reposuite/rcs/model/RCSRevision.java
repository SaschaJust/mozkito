/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "rcsrevision",
        uniqueConstraints = @UniqueConstraint (columnNames = { "TRANSACTION_ID", "CHANGEDFILE_ID" }))
// @AssociationOverrides ({
// @AssociationOverride (name = "primaryKey.changedFile", joinColumns =
// @JoinColumn (name = "file_id")),
// @AssociationOverride (name = "primaryKey.transaction", joinColumns =
// @JoinColumn (name = "transaction_id")) })
// @IdClass (RevisionPrimaryKey.class)
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
	
	private ChangeType     changeType;
	
	private RCSTransaction transaction;
	
	private RCSFile        changedFile;
	
	private long           revisionId;
	
	/**
	 * used by PersistenceUtil to instantiate a {@link RCSRevision} object
	 */
	protected RCSRevision() {
		
	}
	
	@NoneNull
	public RCSRevision(final RCSTransaction rcsTransaction, final RCSFile rcsFile, final ChangeType changeType) {
		setTransaction(rcsTransaction);
		setChangedFile(rcsFile);
		setChangeType(changeType);
		
		boolean success = getTransaction().addRevision(this);
		Condition.check(success, "Revision could not be registered at transaction.");
		Condition.check(getTransaction().getRevisions().contains(this),
		                "Revision could not be registered at transaction.");
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
		
		Condition.notNull(getTransaction(), "Transaction may never be null after creation.");
		Condition.notNull(getChangedFile(), "Changed file may never be null after creation.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	@Transient
	public int compareTo(final RCSRevision rcsRevision) {
		Condition.notNull(rcsRevision, "Compare to (null) makes no sense.");
		return getTransaction().compareTo(rcsRevision.getTransaction());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RCSRevision)) {
			return false;
		}
		RCSRevision other = (RCSRevision) obj;
		if (this.getChangeType() != other.getChangeType()) {
			return false;
		}
		if (this.getChangedFile() == null) {
			if (other.getChangedFile() != null) {
				return false;
			}
		} else if (!this.getChangedFile().equals(other.getChangedFile())) {
			return false;
		}
		
		if (this.getTransaction() == null) {
			if (other.getTransaction() != null) {
				return false;
			}
		} else if (!this.getTransaction().equals(other.getTransaction())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the changedFile
	 */
	// @MapsId ("changedFile")
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@Column (nullable = false)
	public RCSFile getChangedFile() {
		return this.changedFile;
	}
	
	/**
	 * @return the changeType
	 */
	@Enumerated (EnumType.ORDINAL)
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/**
	 * @return the revisionId
	 */
	@Id
	@GeneratedValue
	public long getRevisionId() {
		return this.revisionId;
	}
	
	/**
	 * @return the transaction
	 */
	// @MapsId ("transaction")
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@Column (nullable = false)
	public RCSTransaction getTransaction() {
		return this.transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getChangeType() == null)
		                                                    ? 0
		                                                    : getChangeType().hashCode());
		result = prime * result + ((getChangedFile() == null)
		                                                     ? 0
		                                                     : getChangedFile().hashCode());
		// result = prime * result + ((getPrimaryKey() == null)
		// ? 0
		// : getPrimaryKey().hashCode());
		result = prime * result + ((getTransaction() == null)
		                                                     ? 0
		                                                     : getTransaction().hashCode());
		return result;
	}
	
	/**
	 * @param changedFile the changedFile to set
	 */
	public void setChangedFile(final RCSFile changedFile) {
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
	 * @param revisionId the revisionId to set
	 */
	public void setRevisionId(final long revisionId) {
		this.revisionId = revisionId;
	}
	
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(final RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSRevision [transactionId=" + getTransaction().getId() + ", changedFile=" + getChangedFile()
		        + ", changeType=" + getChangeType() + "]";
	}
}

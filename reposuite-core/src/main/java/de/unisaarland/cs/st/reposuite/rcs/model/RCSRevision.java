/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table (name = "rcsrevision")
@AssociationOverrides ({
	@AssociationOverride (name = "primaryKey.changedFile", joinColumns = @JoinColumn (name = "file_id")),
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
	
	private ChangeType         changeType;
	
	private RevisionPrimaryKey primaryKey;
	
	/**
	 * used by Hibernate to instantiate a {@link RCSRevision} object
	 */
	protected RCSRevision() {
		
	}
	
	@NoneNull
	public RCSRevision(final RCSTransaction rcsTransaction, final RCSFile rcsFile, final ChangeType changeType) {
		setPrimaryKey(new RevisionPrimaryKey(rcsFile, rcsTransaction));
		
		Condition.notNull(this.primaryKey, "Primary key may never be null after creation.");
		Condition.notNull(getTransaction(), "Transaction may never be null after creation.");
		Condition.notNull(getChangedFile(), "Changed file may never be null after creation.");
		
		getTransaction().addRevision(this);
		setChangeType(changeType);
		
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
		Condition.notNull(rcsRevision, "Compare to (null) makes no sense.");
		return getTransaction().compareTo(rcsRevision.getTransaction());
	}
	
	/**
	 * @return the changedFile
	 */
	@Transient
	public RCSFile getChangedFile() {
		return getPrimaryKey().getChangedFile();
	}
	
	/**
	 * @return the changeType
	 */
	@Enumerated (EnumType.ORDINAL)
	public ChangeType getChangeType() {
		return this.changeType;
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
		return getPrimaryKey().getTransaction();
	}
	
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return new LinkedList<Annotated>();
	}
	
	/**
	 * @param changeType
	 *            the changeType to set
	 */
	private void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @param primaryKey
	 */
	private void setPrimaryKey(final RevisionPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
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

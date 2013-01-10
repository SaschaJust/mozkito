/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.versions.model;

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
import javax.persistence.UniqueConstraint;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.Annotated;
import org.mozkito.versions.elements.ChangeType;

/**
 * The Class Revision.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "revision", uniqueConstraints = @UniqueConstraint (columnNames = { "TRANSACTION_ID", "CHANGEDFILE_ID" }))
public class Revision implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2149118675856446526L;
	
	/** The change type. */
	private ChangeType        changeType;
	
	/** The transaction. */
	private ChangeSet    changeSet;
	
	/** The changed file. */
	private Handle            changedFile;
	
	/** The revision id. */
	private long              revisionId;
	
	/**
	 * used by PersistenceUtil to instantiate a {@link Revision} object.
	 */
	protected Revision() {
		
	}
	
	/**
	 * Instantiates a new revision.
	 * 
	 * @param changeSet
	 *            the rcs transaction
	 * @param rcsFile
	 *            the rcs file
	 * @param changeType
	 *            the change type
	 */
	@NoneNull
	public Revision(final ChangeSet changeSet, final Handle rcsFile, final ChangeType changeType) {
		setTransaction(changeSet);
		setChangedFile(rcsFile);
		setChangeType(changeType);
		
		final boolean success = getTransaction().addRevision(this);
		Condition.check(success, "Revision could not be registered at transaction.");
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
		
		Condition.notNull(getTransaction(), "Transaction may never be null after creation.");
		Condition.notNull(getChangedFile(), "Changed file may never be null after creation.");
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
		if (!(obj instanceof Revision)) {
			return false;
		}
		final Revision other = (Revision) obj;
		if (getChangeType() != other.getChangeType()) {
			return false;
		}
		if (getChangedFile() == null) {
			if (other.getChangedFile() != null) {
				return false;
			}
		} else if (!getChangedFile().equals(other.getChangedFile())) {
			return false;
		}
		
		if (getTransaction() == null) {
			if (other.getTransaction() != null) {
				return false;
			}
		} else if (!getTransaction().equals(other.getTransaction())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the changed file.
	 * 
	 * @return the changedFile
	 */
	// @MapsId ("changedFile")
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@Column (nullable = false)
	public Handle getChangedFile() {
		return this.changedFile;
	}
	
	/**
	 * Gets the change type.
	 * 
	 * @return the changeType
	 */
	@Enumerated (EnumType.ORDINAL)
	public ChangeType getChangeType() {
		return this.changeType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	public final String getHandle() {
		return JavaUtils.getHandle(Revision.class);
	}
	
	/**
	 * Gets the revision id.
	 * 
	 * @return the revisionId
	 */
	@Id
	@GeneratedValue
	public long getRevisionId() {
		return this.revisionId;
	}
	
	/**
	 * Gets the transaction.
	 * 
	 * @return the transaction
	 */
	// @MapsId ("transaction")
	@ManyToOne (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@Column (nullable = false)
	public ChangeSet getTransaction() {
		return this.changeSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getChangeType() == null)
		                                                      ? 0
		                                                      : getChangeType().hashCode());
		result = (prime * result) + ((getChangedFile() == null)
		                                                       ? 0
		                                                       : getChangedFile().hashCode());
		// result = prime * result + ((getPrimaryKey() == null)
		// ? 0
		// : getPrimaryKey().hashCode());
		result = (prime * result) + ((getTransaction() == null)
		                                                       ? 0
		                                                       : getTransaction().hashCode());
		return result;
	}
	
	/**
	 * Sets the changed file.
	 * 
	 * @param changedFile
	 *            the changedFile to set
	 */
	public void setChangedFile(final Handle changedFile) {
		this.changedFile = changedFile;
	}
	
	/**
	 * Sets the change type.
	 * 
	 * @param changeType
	 *            the changeType to set
	 */
	protected void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * Sets the revision id.
	 * 
	 * @param revisionId
	 *            the revisionId to set
	 */
	public void setRevisionId(final long revisionId) {
		this.revisionId = revisionId;
	}
	
	/**
	 * Sets the transaction.
	 * 
	 * @param changeSet
	 *            the transaction to set
	 */
	public void setTransaction(final ChangeSet changeSet) {
		this.changeSet = changeSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Revision [transactionId=" + getTransaction().getId() + ", changedFile=" + getChangedFile()
		        + ", changeType=" + getChangeType() + "]";
	}
}

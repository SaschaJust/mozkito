/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "rcsrevision",
        uniqueConstraints = @UniqueConstraint (columnNames = { "TRANSACTION_ID", "CHANGEDFILE_ID" }))
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
	
	/**
	 * @param rcsTransaction
	 * @param rcsFile
	 * @param changeType
	 */
	@NoneNull
	public RCSRevision(final RCSTransaction rcsTransaction, final RCSFile rcsFile, final ChangeType changeType) {
		setTransaction(rcsTransaction);
		setChangedFile(rcsFile);
		setChangeType(changeType);
		
		boolean success = getTransaction().addRevision(this);
		Condition.check(success, "Revision could not be registered at transaction.");
		
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
	 * @param changedFile
	 *            the changedFile to set
	 */
	public void setChangedFile(final RCSFile changedFile) {
		this.changedFile = changedFile;
	}
	
	/**
	 * @param changeType
	 *            the changeType to set
	 */
	protected void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @param revisionId
	 *            the revisionId to set
	 */
	public void setRevisionId(final long revisionId) {
		this.revisionId = revisionId;
	}
	
	/**
	 * @param transaction
	 *            the transaction to set
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

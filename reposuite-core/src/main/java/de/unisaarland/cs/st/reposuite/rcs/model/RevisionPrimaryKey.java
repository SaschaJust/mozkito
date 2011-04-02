package de.unisaarland.cs.st.reposuite.rcs.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class RevisionPrimaryKey implements Annotated, Serializable {
	
	private static final long serialVersionUID = -6589083902512542247L;
	
	private String            transactionId;
	
	private long              changedFileId;
	
	public RevisionPrimaryKey() {
	}
	
	/**
	 * @param changedFile
	 * @param transaction
	 */
	public RevisionPrimaryKey(final RCSFile changedFile, final RCSTransaction transaction) {
		this.changedFileId = changedFile.getGeneratedId();
		this.transactionId = transaction.getId();
	}
	
	/**
	 * @param transaction
	 * @param changedFile
	 */
	public RevisionPrimaryKey(final RCSTransaction transaction, final RCSFile changedFile) {
		super();
		this.changedFileId = changedFile.getGeneratedId();
		this.transactionId = transaction.getId();
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
		if (!(obj instanceof RevisionPrimaryKey)) {
			return false;
		}
		RevisionPrimaryKey other = (RevisionPrimaryKey) obj;
		if (this.getChangedFileId() != other.getChangedFileId()) {
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
	
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Basic
	public long getChangedFileId() {
		return this.changedFileId;
	}
	
	// @ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Basic
	public String getTransaction() {
		return this.transactionId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.getChangedFileId() ^ (this.getChangedFileId() >>> 32));
		result = prime * result + ((this.getTransaction() == null)
		                                                          ? 0
		                                                          : this.getTransaction().hashCode());
		return result;
	}
	
	/**
	 * @param changedFileId
	 */
	public void setChangedFileId(final long changedFileId) {
		this.changedFileId = changedFileId;
	}
	
	/**
	 * @param transactionId
	 */
	public void setTransaction(final String transactionId) {
		this.transactionId = transactionId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("transactionId: ", getTransaction())
		                                                                .append("changedFileId: ", getChangedFileId())
		                                                                .toString();
	}
}

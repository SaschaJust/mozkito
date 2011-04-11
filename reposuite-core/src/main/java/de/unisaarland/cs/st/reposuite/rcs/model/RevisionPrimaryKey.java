package de.unisaarland.cs.st.reposuite.rcs.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class RevisionPrimaryKey implements Annotated, Serializable {
	
	private static final long serialVersionUID = -6589083902512542247L;
	
	private String            transaction;
	
	private long              changedFile;
	
	public RevisionPrimaryKey() {
	}
	
	/**
	 * @param transaction
	 * @param changedFile
	 */
	public RevisionPrimaryKey(final String transaction, final long changedFile) {
		this.transaction = transaction;
		this.changedFile = changedFile;
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
		if (this.changedFile != other.changedFile) {
			return false;
		}
		if (this.transaction == null) {
			if (other.transaction != null) {
				return false;
			}
		} else if (!this.transaction.equals(other.transaction)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return
	 */
	public long getChangedFile() {
		return this.changedFile;
	}
	
	/**
	 * @return
	 */
	public String getTransaction() {
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
		result = prime * result + (int) (this.changedFile ^ (this.changedFile >>> 32));
		result = prime * result + ((this.transaction == null)
		                                                     ? 0
		                                                     : this.transaction.hashCode());
		return result;
	}
	
	/**
	 * @param changedFile
	 */
	public void setChangedFile(final long changedFile) {
		this.changedFile = changedFile;
	}
	
	/**
	 * @param transaction
	 */
	public void setTransaction(final String transaction) {
		this.transaction = transaction;
	}
}

package de.unisaarland.cs.st.reposuite.rcs.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class RevisionPrimaryKey implements Annotated, Serializable {
	
	private static final long serialVersionUID = -6589083902512542247L;
	
	private RCSTransaction    transaction;
	
	private RCSFile           changedFile;
	
	protected RevisionPrimaryKey() {
	}
	
	public RevisionPrimaryKey(final RCSFile changedFile, final RCSTransaction transaction) {
		this.changedFile = changedFile;
		this.transaction = transaction;
	}
	
	public RevisionPrimaryKey(final RCSTransaction transaction, final RCSFile changedFile) {
		super();
		this.transaction = transaction;
		this.changedFile = changedFile;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else if ((o != null) && (o instanceof RevisionPrimaryKey) && (o.hashCode() == this.hashCode())) {
			RevisionPrimaryKey anotherKey = (RevisionPrimaryKey) o;
			return anotherKey.transaction.equals(this.transaction) && anotherKey.changedFile.equals(this.changedFile);
		} else {
			return false;
		}
	}
	
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public RCSFile getChangedFile() {
		return this.changedFile;
	}
	
	@Override
	@Transient
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
	
	@ManyToOne (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public RCSTransaction getTransaction() {
		return this.transaction;
	}
	
	@Override
	public int hashCode() {
		return this.transaction.hashCode() + 13 * this.changedFile.hashCode();
	}
	
	public void setChangedFile(final RCSFile changedFile) {
		this.changedFile = changedFile;
	}
	
	public void setTransaction(final RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("transaction", this.transaction)
		        .append("changedFile", this.changedFile).toString();
	}
}

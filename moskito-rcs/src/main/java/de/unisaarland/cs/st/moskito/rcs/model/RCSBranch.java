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
package de.unisaarland.cs.st.moskito.rcs.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.jdbc.Index;

import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.rcs.elements.PreviousTransactionIterator;

/**
 * The Class RCSBranch.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcsbranch")
public class RCSBranch implements Annotated {
	
	private static final long  serialVersionUID   = 5419737140470855522L;
	
	private String             name;
	private RCSTransaction     head               = null;
	private Set<String>        mergedIn           = new HashSet<String>();
	
	public static final String MASTER_BRANCH_NAME = "master";
	
	/**
	 * Instantiates a new rCS branch.
	 */
	protected RCSBranch() {
		
	}
	
	/**
	 * Instantiates a new rCS branch.
	 * 
	 * @param name
	 *            the name
	 */
	public RCSBranch(final String name) {
		setName(name);
	}
	
	/**
	 * Checks if any of the given transactions was committed into this branch.
	 * 
	 * @param tIds
	 *            the transaction ids to check for
	 * @return A sorted set of transactions committed into this branch
	 */
	@Transient
	public TreeSet<RCSTransaction> containsAnyTransaction(final Collection<String> tIds) {
		final TreeSet<RCSTransaction> result = new TreeSet<RCSTransaction>();
		for (final String id : tIds) {
			final RCSTransaction t = containsTransaction(id);
			if (t != null) {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Checks if the given transaction id was committed in this branch. Returns the RCSTransaction if found, otherwise
	 * <code>null</code>.
	 * 
	 * @param tId
	 *            the t id
	 * @return the transaction if found. Otherwise <code>null</code>
	 */
	@Transient
	public RCSTransaction containsTransaction(final String tId) {
		RCSTransaction current = getHead();
		while (current != null) {
			if (current.getId().equals(tId)) {
				return current;
			}
			current = current.getBranchParent();
		}
		return null;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final RCSBranch other = (RCSBranch) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	@Transient
	public String getHandle() {
		return RCSBranch.class.getSimpleName();
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	public RCSTransaction getHead() {
		return this.head;
	}
	
	/**
	 * @return the name of the branch this branch was merged in (if any)
	 * @deprecated this is not yet implement
	 */
	@ElementCollection
	@Deprecated
	public Set<String> getMergedIn() {
		return this.mergedIn;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Id
	@Basic
	@Index (name = "idx_name")
	public String getName() {
		return this.name;
	}
	
	/**
	 * Return the transactions within this branch in topological order.
	 * 
	 * @return the transactions
	 */
	@Transient
	public Iterable<RCSTransaction> getTransactions() {
		return new PreviousTransactionIterator(getHead());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getName() == null)
		                                                ? 0
		                                                : getName().hashCode());
		return result;
	}
	
	@Transient
	public boolean isMasterBranch() {
		return getName().equals(MASTER_BRANCH_NAME);
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean isOpen() {
		return getHead() == null;
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the end to set
	 */
	public void setHead(final RCSTransaction end) {
		this.head = end;
	}
	
	/**
	 * @param mergedIn
	 */
	public void setMergedIn(final Set<String> mergedIn) {
		this.mergedIn = mergedIn;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("RCSBranch [name=");
		sb.append(getName());
		sb.append(", parent=");
		sb.append(", head=");
		if (getHead() != null) {
			sb.append(getHead().getId());
		} else {
			sb.append("null");
		}
		sb.append(", mergedIn=");
		sb.append(getMergedIn());
		sb.append("]");
		return sb.toString();
	}
	
}

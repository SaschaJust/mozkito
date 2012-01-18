/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.rcs.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;

import org.apache.openjpa.persistence.jdbc.Index;

import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * The Class RCSBranch.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcsbranch")
public class RCSBranch implements Annotated, Comparable<RCSBranch> {
	
	private static final long  serialVersionUID = 5419737140470855522L;
	
	private long               generatedId;
	private String             name;
	private RCSBranch          parent           = null;
	private RCSTransaction     begin            = null;
	private RCSTransaction     end              = null;
	public static RCSBranch    MASTER           = new RCSBranch("master", true);
	private boolean            open             = false;
	private LinkedList<String> mergedIn         = new LinkedList<String>();
	
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
	 * Instantiates a new rCS branch.
	 * 
	 * @param name
	 *            might be null
	 * @param open
	 *            the open
	 */
	private RCSBranch(final String name, final boolean open) {
		setName(name);
		markOpen();
	}
	
	/**
	 * Instantiates a new rCS branch.
	 * 
	 * @param name
	 *            might be null
	 * @param parent
	 *            might be null
	 */
	public RCSBranch(final String name, final RCSBranch parent) {
		setName(name);
		setParent(parent);
	}
	
	/**
	 * @param mergedIn
	 */
	public void addMergedIn(final String mergedIn) {
		this.mergedIn.addFirst(mergedIn);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RCSBranch other) {
		RCSBranch p = getParent();
		if (equals(other)) {
			return 0;
		}
		if (other.equals(MASTER)) {
			return 1;
		} else if (equals(MASTER)) {
			return -1;
		}
		while ((p != null) && (!p.equals(MASTER))) {
			if (p.equals(other)) {
				return 1;
			}
			p = p.getParent();
		}
		RCSBranch c = other.getParent();
		while ((c != null) && (!c.equals(MASTER))) {
			if (c.equals(this)) {
				return -1;
			}
			c = c.getParent();
		}
		return 0;
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
	 * Checks if the given transaction id was committed in this branch. Returns
	 * the RCSTransaction if found, otherwise <code>null</code>.
	 * 
	 * @param tId
	 *            the t id
	 * @return the transaction if found. Otherwise <code>null</code>
	 */
	@Transient
	public RCSTransaction containsTransaction(final String tId) {
		if (getBegin().getId().equals(tId)) {
			return getBegin();
		}
		
		RCSTransaction current = getEnd();
		while (!current.equals(getBegin())) {
			if (current.getId().equals(tId)) {
				return current;
			}
			current = current.getParent(this);
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
		if (getBegin() == null) {
			if (other.getBegin() != null) {
				return false;
			}
		} else if (!getBegin().equals(other.getBegin())) {
			return false;
		}
		if (getEnd() == null) {
			if (other.getEnd() != null) {
				return false;
			}
		} else if (!getEnd().equals(other.getEnd())) {
			return false;
		}
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		if (getParent() == null) {
			if (other.getParent() != null) {
				return false;
			}
		} else if (!getParent().equals(other.getParent())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the begin.
	 * 
	 * @return the begin
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	public RCSTransaction getBegin() {
		return this.begin;
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	public RCSTransaction getEnd() {
		return this.end;
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@Index (name = "idx_branchid")
	@Column (name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.AUTO)
	protected long getGeneratedId() {
		return this.generatedId;
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
	 * @return the name of the branch this branch was merged in (if any)
	 */
	public List<String> getMergedIn() {
		return this.mergedIn;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Basic
	@Index (name = "idx_name")
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@OneToOne (fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	public RCSBranch getParent() {
		return this.parent;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getBegin() == null)
		                                                 ? 0
		                                                 : getBegin().hashCode());
		result = (prime * result) + ((getEnd() == null)
		                                               ? 0
		                                               : getEnd().hashCode());
		result = (prime * result) + ((getName() == null)
		                                                ? 0
		                                                : getName().hashCode());
		result = (prime * result) + ((getParent() == null)
		                                                  ? 0
		                                                  : getParent().hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean hasParent() {
		return getParent() != null;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean isOpen() {
		return this.open;
	}
	
	/**
	 * 
	 */
	@Transient
	public void markOpen() {
		addMergedIn(null);
		setOpen(true);
	}
	
	/**
	 * Sets the begin.
	 * 
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(final RCSTransaction begin) {
		this.begin = begin;
	}
	
	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the end to set
	 */
	public void setEnd(final RCSTransaction end) {
		this.end = end;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param mergedIn
	 */
	@ElementCollection
	@Deprecated
	public void setMergedIn(final LinkedList<String> mergedIn) {
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
	
	protected void setOpen(final boolean b) {
		this.open = b;
	}
	
	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the parent to set
	 */
	protected void setParent(final RCSBranch parent) {
		this.parent = parent;
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
		if (getParent() != null) {
			sb.append(getParent());
		} else {
			sb.append("null");
		}
		sb.append(", end=");
		if (getEnd() != null) {
			sb.append(getEnd().getId());
		} else {
			sb.append("null");
		}
		sb.append(", mergedIn=");
		sb.append(JavaUtils.collectionToString(getMergedIn()));
		sb.append("]");
		return sb.toString();
	}
	
}

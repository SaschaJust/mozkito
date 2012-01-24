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
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.kisa.Logger;

import org.apache.openjpa.persistence.jdbc.Index;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * The Class RCSBranch.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@Table (name = "rcsbranch")
public class RCSBranch implements Annotated, Comparable<RCSBranch> {
	
	private static final long      serialVersionUID   = 5419737140470855522L;
	
	private long                   generatedId;
	private String                 name;
	private RCSBranch              parent             = null;
	private RCSTransaction         begin              = null;
	private RCSTransaction         end                = null;
	
	private boolean                open               = false;
	private String                 mergedIn           = null;
	
	private static RCSBranch       MASTER_BRANCH      = null;
	private static PersistenceUtil MASTER_BRANCH_UTIL = null;
	private static String          MASTER_BRANCH_NAME = "master";
	
	private static RCSBranch createNewMasterBranch() {
		RCSBranch masterBranch = new RCSBranch(MASTER_BRANCH_NAME);
		masterBranch.setOpen(true);
		return masterBranch;
	}
	
	public static RCSBranch getMasterBranch() {
		PersistenceUtil persistenceUtil = null;
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e) {
			if (Logger.logError()) {
				Logger.error("Attempt to create RCSBranch.MASTER_BRANCH_UTIL failed. "
				        + "It seems that there is no database connection configured.");
			}
		}
		
		// If we could no load persistenceUtil
		if (persistenceUtil == null) {
			if (MASTER_BRANCH == null) {
				// There is no MASTER_BRACHAN. We cannot load it,
				// so we return new created MASTER_BRANCH
				if (Logger.logWarn()) {
					Logger.warn("There exists no database connection. "
					        + "Request to load persisted RCSBranch.MASTER_BRANCH would fail. "
					        + "Returning new RCSBRanch.MASTER_BRANCH.");
				}
				MASTER_BRANCH = createNewMasterBranch();
			}
		} else {
			// We could get a valid persistence util.
			// The existed no previous persistence util: try to load persisted
			// MASTER_BRANCH
			if ((MASTER_BRANCH_UTIL == null) || (!MASTER_BRANCH_UTIL.equals(persistenceUtil))) {
				
				if (MASTER_BRANCH_UTIL != null) {
					if (Logger.logDebug()) {
						Logger.debug("Previous RCSBRanch.MASTER_BRANCH was bound to different persistenceUtil. Reloading RCSBranch.MASTER_BRANCH.");
					}
				}
				
				MASTER_BRANCH_UTIL = persistenceUtil;
				// try to load MASTER_BRANCH or create new one.
				Criteria<RCSBranch> criteria = MASTER_BRANCH_UTIL.createCriteria(RCSBranch.class)
				                                                 .eq("name", MASTER_BRANCH_NAME);
				List<RCSBranch> loadedBranches = MASTER_BRANCH_UTIL.load(criteria);
				if (loadedBranches.isEmpty()) {
					// We could not load a persisted MASTER_BRANCH. So, create a
					// new one and return.
					if (Logger.logDebug()) {
						Logger.debug("Attempt to lead persisted RCSBranch.MASTER_BRANCH "
						        + "from existing database connection failed. " + "No persisted master branch found. "
						        + "Returning new RCSBranch.MASTER_BRANCH.");
					}
					MASTER_BRANCH = createNewMasterBranch();
				} else {
					MASTER_BRANCH = loadedBranches.get(0);
				}
			}
		}
		return MASTER_BRANCH;
	}
	
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
		this.setName(name);
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
		this.setName(name);
		this.setParent(parent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final RCSBranch other) {
		RCSBranch p = this.getParent();
		if (this.equals(other)) {
			return 0;
		}
		if (other.equals(getMasterBranch())) {
			return 1;
		} else if (this.equals(getMasterBranch())) {
			return -1;
		}
		while ((p != null) && (!p.equals(getMasterBranch()))) {
			if (p.equals(other)) {
				return 1;
			}
			p = p.getParent();
		}
		RCSBranch c = other.getParent();
		while ((c != null) && (!c.equals(getMasterBranch()))) {
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
		TreeSet<RCSTransaction> result = new TreeSet<RCSTransaction>();
		for (String id : tIds) {
			RCSTransaction t = this.containsTransaction(id);
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
		if (this.getBegin().getId().equals(tId)) {
			return this.getBegin();
		}
		
		RCSTransaction current = this.getEnd();
		while (!current.equals(this.getBegin())) {
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
		RCSBranch other = (RCSBranch) obj;
		if (this.getBegin() == null) {
			if (other.getBegin() != null) {
				return false;
			}
		} else if (!this.getBegin().equals(other.getBegin())) {
			return false;
		}
		if (this.getEnd() == null) {
			if (other.getEnd() != null) {
				return false;
			}
		} else if (!this.getEnd().equals(other.getEnd())) {
			return false;
		}
		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName())) {
			return false;
		}
		if (this.getParent() == null) {
			if (other.getParent() != null) {
				return false;
			}
		} else if (!this.getParent().equals(other.getParent())) {
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
	public String getMergedIn() {
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
		result = (prime * result) + ((this.getBegin() == null)
		                                                      ? 0
		                                                      : this.getBegin().hashCode());
		result = (prime * result) + ((this.getEnd() == null)
		                                                    ? 0
		                                                    : this.getEnd().hashCode());
		result = (prime * result) + ((this.getName() == null)
		                                                     ? 0
		                                                     : this.getName().hashCode());
		result = (prime * result) + ((this.getParent() == null)
		                                                       ? 0
		                                                       : this.getParent().hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean hasParent() {
		return this.getParent() != null;
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
		this.setMergedIn(null);
		this.setOpen(true);
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
	public void setMergedIn(final String mergedIn) {
		if (!this.isOpen()) {
			this.mergedIn = mergedIn;
		}
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
		StringBuilder sb = new StringBuilder();
		sb.append("RCSBranch [name=");
		sb.append(this.getName());
		sb.append(", parent=");
		if (this.getParent() != null) {
			sb.append(this.getParent());
		} else {
			sb.append("null");
		}
		sb.append(", end=");
		if (this.getEnd() != null) {
			sb.append(this.getEnd().getId());
		} else {
			sb.append("null");
		}
		sb.append(", mergedIn=");
		sb.append(this.getMergedIn());
		sb.append("]");
		return sb.toString();
	}
	
}

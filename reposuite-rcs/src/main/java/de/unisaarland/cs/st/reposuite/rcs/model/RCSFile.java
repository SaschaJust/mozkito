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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "rcsfile")
public class RCSFile implements Annotated, Serializable {
	
	/**
	 * 
	 */
	private static final long   serialVersionUID = 7232712367403624199L;
	private long                generatedId;
	
	private Map<String, String> changedNames     = new HashMap<String, String>();
	
	/**
	 * used by PersistenceUtil to create a {@link RCSFile} instance
	 */
	protected RCSFile() {
		
	}
	
	/**
	 * @param path
	 */
	public RCSFile(final String path, final RCSTransaction transaction) {
		getChangedNames().put(transaction.getId(), path);
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * Assign transaction.
	 * 
	 * @param transaction
	 *            the transaction
	 * @param pathName
	 *            the path name
	 */
	@Transient
	public void assignTransaction(final RCSTransaction transaction,
			final String pathName) {
		getChangedNames().put(transaction.getId(), pathName);
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		RCSFile other = (RCSFile) obj;
		if (getGeneratedId() != other.getGeneratedId()) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the changedNames
	 */
	@ElementCollection
	@JoinTable (name = "filenames", joinColumns = { @JoinColumn (name = "fileid", nullable = false) })
	public Map<String, String> getChangedNames() {
		return this.changedNames;
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the simple class name
	 */
	@Transient
	public String getHandle() {
		return RCSFile.class.getSimpleName();
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getLatestPath() {
		return getChangedNames().get(new TreeSet<String>(getChangedNames().keySet()).last());
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getPath(final RCSTransaction transaction) {
		RCSTransaction current = transaction;
		
		while ((current != null) && !getChangedNames().containsKey(current.getId())) {
			
			// if current transaction is a merge
			Set<RCSTransaction> currentParents = current.getParents();
			if (currentParents.size() > 1) {
				TreeSet<RCSTransaction> hits = new TreeSet<RCSTransaction>();
				// for each merged branch check if it contains any of the
				// transaction ids
				for (RCSTransaction p : currentParents) {
					RCSBranch parentBranch = p.getBranch();
					if ((!parentBranch.equals(current.getBranch())) && (!parentBranch.equals(RCSBranch.MASTER))) {
						hits.addAll(parentBranch.containsAnyTransaction(getChangedNames().keySet()));
					}
				}
				// if we found a branch that contains a transaction and got
				// merged here
				if (hits.size() > 0) {
					// mark it as hit and continue
					current = hits.last();
					continue;
				}
			}
			
			current = current.getParent(current.getBranch());
		}
		
		if (current != null) {
			return getChangedNames().get(current.getId());
		} else {
			if (Logger.logError()) {
				Logger.error("Could not determine path for RCSFile (id=" + getGeneratedId() + ") for transaction "
						+ transaction.getId());
			}
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (getGeneratedId() ^ (getGeneratedId() >>> 32));
		return result;
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean saved() {
		return getGeneratedId() != 0;
	}
	
	protected void setChangedNames(final Map<String, String> changedNames) {
		this.changedNames = changedNames;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSFile [id=" + getGeneratedId() + ", changedNames="
				+ JavaUtils.collectionToString(getChangedNames().values()) + "]";
	}
	
}

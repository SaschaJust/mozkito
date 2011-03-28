/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "rcsfile")
public class RCSFile implements Annotated {
	
	/**
	 * 
	 */
	private static final long         serialVersionUID = 7232712367403624199L;
	private long                      generatedId;
	private Map<String, String> changedNames     = new HashMap<String, String>();
	
	/**
	 * used by Hibernate to create a {@link RCSFile} instance
	 */
	protected RCSFile() {
		
	}
	
	/**
	 * @param path
	 */
	RCSFile(final String path, final RCSTransaction transaction) {
		this.changedNames.put(transaction.getId(), path);
		
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
	
	/**
	 * @return the changedNames
	 */
	@ElementCollection
	@JoinTable (name = "filenames", joinColumns = { @JoinColumn (name = "transaction_id", nullable = false) })
	public Map<String, String> getChangedNames() {
		return this.changedNames;
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@Index (name = "idx_fileid")
	@Column (name = "id")
	@GeneratedValue
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
		return this.changedNames.get(new TreeSet<String>(this.changedNames.keySet()).last());
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getPath(final RCSTransaction transaction) {
		RCSTransaction current = transaction;
		
		while ((current != null) && !this.changedNames.containsKey(current.getId())) {
			
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
			return this.changedNames.get(current.getId());
		} else {
			if (Logger.logError()) {
				Logger.error("Could not determine path for RCSFile (id=" + this.getGeneratedId() + ") for transaction "
				             + transaction.getId());
			}
			return null;
		}
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean saved() {
		return getGeneratedId() != 0;
	}
	
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	@SuppressWarnings ("unused")
	private void setChangedNames(final Map<String, String> changedNames) {
		this.changedNames = changedNames;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	@SuppressWarnings ("unused")
	private void setGeneratedId(final long generatedId) {
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

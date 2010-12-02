/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	private static final long           serialVersionUID = 7232712367403624199L;
	private long                        generatedId;
	private Map<RCSTransaction, String> changedNames     = new HashMap<RCSTransaction, String>();
	
	/**
	 * used by Hibernate to create a {@link RCSFile} instance
	 */
	protected RCSFile() {
		
	}
	
	/**
	 * @param path
	 */
	RCSFile(final String path, final RCSTransaction transaction) {
		this.changedNames.put(transaction, path);
		
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
	public void assignTransaction(final RCSTransaction transaction, final String pathName) {
		getChangedNames().put(transaction, pathName);
	}
	
	/**
	 * @return the changedNames
	 */
	@ElementCollection (fetch = FetchType.LAZY)
	private Map<RCSTransaction, String> getChangedNames() {
		return this.changedNames;
	}
	
	/**
	 * @return the generatedId
	 */
	@SuppressWarnings ("unused")
	@Id
	@GeneratedValue
	private long getGeneratedId() {
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
		return this.changedNames.get(new TreeSet<RCSTransaction>(this.changedNames.keySet()).last());
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getPath(final RCSTransaction transaction) {
		RCSTransaction current = transaction;
		while ((current != null) && !this.changedNames.containsKey(current)) {
			current = current.getPrevTransaction();
		}
		
		if (current != null) {
			return this.changedNames.get(current);
		} else {
			return null;
		}
	}
	
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	@SuppressWarnings ("unused")
	private void setChangedNames(final Map<RCSTransaction, String> changedNames) {
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
		return "RCSFile [changedNames=" + JavaUtils.collectionToString(getChangedNames().values()) + "]";
	}
	
}

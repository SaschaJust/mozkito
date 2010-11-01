/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class RCSFile implements Annotated {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RCSFile.class.getSimpleName();
	}
	
	private final Map<RCSTransaction, String> changedNames = new HashMap<RCSTransaction, String>();
	
	/**
	 * used by Hibernate to create a {@link RCSFile} instance
	 */
	@SuppressWarnings ("unused")
	private RCSFile() {
		
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
	 * @param transaction
	 * @param pathName
	 */
	@Transient
	public void assignTransaction(final RCSTransaction transaction, final String pathName) {
		this.changedNames.put(transaction, pathName);
	}
	
	/**
	 * @return the changedNames
	 */
	@SuppressWarnings ("unused")
	@ElementCollection
	private Map<RCSTransaction, String> getChangedNames() {
		return this.changedNames;
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
	public String getPath(final RCSTransaction transaction) {
		return this.changedNames.get(transaction);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSFile [changedNames=" + JavaUtils.collectionToString(this.changedNames.values()) + "]";
	}
	
}

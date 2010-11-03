/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

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
@Table(name = "rcsfile")
public class RCSFile implements Annotated {
	
	/**
	 * @return the simple class name
	 */
	public static String getHandle() {
		return RCSFile.class.getSimpleName();
	}
	
	private long                        generatedId;
	
	private Map<RCSTransaction, String> changedNames = new HashMap<RCSTransaction, String>();
	
	/**
	 * used by Hibernate to create a {@link RCSFile} instance
	 */
	@SuppressWarnings("unused")
	private RCSFile() {
		
	}
	
	/**
	 * @param path
	 */
	RCSFile(final String path, final RCSTransaction transaction) {
		changedNames.put(transaction, path);
		
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
		changedNames.put(transaction, pathName);
	}
	
	/**
	 * @return the changedNames
	 */
	@SuppressWarnings("unused")
	@ElementCollection(fetch = FetchType.LAZY)
	private Map<RCSTransaction, String> getChangedNames() {
		//FIXME Test if this annotation works.
		return changedNames;
	}
	
	/**
	 * @return the generatedId
	 */
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private long getGeneratedId() {
		return generatedId;
	}
	
	/**
	 * @return
	 */
	@Transient
	public String getLatestPath() {
		return changedNames.get(new TreeSet<RCSTransaction>(changedNames.keySet()).last());
	}
	
	/**
	 * @return
	 */
	public String getPath(final RCSTransaction transaction) {
		return changedNames.get(transaction);
	}
	
	@SuppressWarnings("unused")
	private void setChangedNames(final Map<RCSTransaction, String> changedNames) {
		this.changedNames = changedNames;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	@SuppressWarnings("unused")
	private void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RCSFile [changedNames=" + JavaUtils.collectionToString(changedNames.values()) + "]";
	}
	
}

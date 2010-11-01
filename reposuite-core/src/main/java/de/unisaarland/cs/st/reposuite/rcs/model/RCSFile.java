/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCSFile implements Annotated {
	
	private final Map<RCSTransaction, String> changedNames = new HashMap<RCSTransaction, String>();
	
	/**
	 * @param path
	 */
	RCSFile(final String path, final RCSTransaction transaction) {
		this.changedNames.put(transaction, path);
		
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	public void assignTransaction(final RCSTransaction transaction, final String pathName) {
		this.changedNames.put(transaction, pathName);
	}
	
	/**
	 * @return the simple class name
	 */
	private String getHandle() {
		return RCSFile.class.getSimpleName();
	}
	
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

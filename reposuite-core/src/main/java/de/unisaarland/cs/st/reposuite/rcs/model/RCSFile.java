/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
	
}

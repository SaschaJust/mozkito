/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.util.List;

import de.unisaarland.cs.st.reposuite.persistence.PersistenceManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class RCSFiles2Bugs {
	
	static {
		PersistenceManager.registerNativeQuery("postgres", "files2bugs", "SELECT ");
	}
	
	/**
	 * @return
	 */
	public static List<RCSFiles2Bugs> getBugCounts() {
		// PersistenceUtil util = PersistenceManager.getUtil();
		// util.executeNativeSelectQuery(PersistenceManager.getNativeQuery(util,
		// "files2bugs"));
		return null;
	}
}

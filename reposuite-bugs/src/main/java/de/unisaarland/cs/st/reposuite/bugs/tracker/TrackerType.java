/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum TrackerType {
	BUGZILLA, ISSUEZILLA, JIRA, SOURCEFORGE;
	
	public static String getHandle() {
		return TrackerType.class.getSimpleName();
	}
}

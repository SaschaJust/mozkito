/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.elements;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum Severity {
	UNKNOWN, ENHANCEMENT, TRIVIAL, MINOR, NORMAL, MAJOR, CRITICAL, BLOCKER;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

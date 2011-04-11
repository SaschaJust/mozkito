/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.elements;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum Resolution {
	UNKNOWN, UNRESOLVED, DUPLICATE, RESOLVED, INVALID, WONT_FIX, WORKS_FOR_ME;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

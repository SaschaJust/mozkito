/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.elements;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum Type {
	BUG, RFE, TASK, TEST, OTHER, UNKNOWN;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

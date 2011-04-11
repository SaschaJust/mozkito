/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.elements;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum Priority {
	UNKNOWN, VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

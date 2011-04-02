/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.elements;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum Status {
	UNKNOWN, UNCONFIRMED, NEW, ASSIGNED, IN_PROGRESS, FEEDBACK, REOPENED, REVIEWPENDING, VERIFIED, CLOSED;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

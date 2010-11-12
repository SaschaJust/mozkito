/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public enum Severity implements Annotated {
	UNKNOWN, ENHANCEMENT, TRIVIAL, MINOR, NORMAL, MAJOR, CRITICAL, BLOCKER;
	
	@Override
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
	
}

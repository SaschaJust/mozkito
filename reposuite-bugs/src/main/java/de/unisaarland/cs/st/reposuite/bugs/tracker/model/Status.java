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
public enum Status implements Annotated {
	UNKNOWN, UNCONFIRMED, NEW, ASSIGNED, IN_PROGRESS, REOPENED, RESOLVED, VERIFIED, CLOSED;
	
	@Override
	public Collection<Annotated> getSaveFirst() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;

import javax.persistence.Table;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Table (name = "annotated")
public enum Type implements Annotated {
	BUG, RFE, TASK, TEST, OTHER;
	
	@Override
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
	
}

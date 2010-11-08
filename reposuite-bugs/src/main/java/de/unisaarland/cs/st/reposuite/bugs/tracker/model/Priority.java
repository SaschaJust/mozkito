/**
 * 
 */
package de.unisaarland.cs.st.reposuite.bugs.tracker.model;

import java.util.Collection;

import javax.persistence.Entity;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public enum Priority implements Annotated {
	VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;
	
	@Override
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
	
}

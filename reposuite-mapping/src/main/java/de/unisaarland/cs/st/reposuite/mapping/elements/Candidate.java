/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.elements;

import net.ownhero.dev.ioda.Tuple;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Candidate {
	
	MappableEntity from;
	MappableEntity to;
	
	/**
	 * @param candidatePair
	 */
	public Candidate(final Tuple<? extends MappableEntity, ? extends MappableEntity> candidatePair) {
		this.from = candidatePair.getFirst();
		this.to = candidatePair.getSecond();
	}
}

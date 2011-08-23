package de.unisaarland.cs.st.reposuite.mapping.strategies;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoneWarriorStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps as soon as a single engine agrees on the mapping";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping)
	 */
	@Override
	public PersistentMapping map(final PersistentMapping mapping) {
		List<MappingEngineFeature> features = mapping.getScore().getFeatures();
		for (MappingEngineFeature feature : features) {
			if (Double.compare(feature.getConfidence(), 0.0d) > 0) {
				mapping.setValid(true);
				break;
			}
		}
		
		return mapping;
	}
	
}

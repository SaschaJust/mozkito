package de.unisaarland.cs.st.reposuite.mapping.strategies;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MajorityStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the mayority decision of the MappingEngines.";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public PersistentMapping map(final PersistentMapping mapping) {
		if (mapping.getValid() == null) {
			int pro = 0;
			int contra = 0;
			@SuppressWarnings("unused") int neutral = 0;
			
			List<MappingEngineFeature> features = mapping.getScore().getFeatures();
			for (MappingEngineFeature feature : features) {
				int compare = Double.compare(feature.getConfidence(), 0.0d);
				switch (compare) {
					case -1:
						contra += 1;
						break;
					case 0:
						neutral += 1;
						break;
					case 1:
						pro += 1;
						break;
				}
			}
			
			if (pro / (pro + contra) > 0.5) {
				mapping.setValid(true);
			}
		}
		
		return mapping;
	}
	
}

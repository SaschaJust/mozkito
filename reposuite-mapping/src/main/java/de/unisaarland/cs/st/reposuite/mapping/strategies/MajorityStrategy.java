/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.strategies;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MajorityStrategy extends MappingStrategy {
	
	public MajorityStrategy(final MappingSettings settings) {
		super(settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public RCSBugMapping map(final RCSBugMapping mapping) {
		if (mapping.getValid() == null) {
			int pro = 0;
			int contra = 0;
			int neutral = 0;
			
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
	
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments mappingArguments,
	                     final boolean isRequired) {
		// TODO Auto-generated method stub
		
	}
	
}

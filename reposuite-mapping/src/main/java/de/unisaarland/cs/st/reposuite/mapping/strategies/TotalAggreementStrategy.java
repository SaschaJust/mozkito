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
public class TotalAggreementStrategy extends MappingStrategy {
	
	/**
	 * @param settings
	 */
	public TotalAggreementStrategy(final MappingSettings settings) {
		super(settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps positive/negative iff all engines agree on that";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#init()
	 */
	@Override
	public void init() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping)
	 */
	@Override
	public RCSBugMapping map(final RCSBugMapping mapping) {
		if (mapping.getValid() == null) {
			int value = 0;
			
			List<MappingEngineFeature> features = mapping.getScore().getFeatures();
			for (MappingEngineFeature feature : features) {
				int cache = Double.compare(feature.getConfidence(), 0.0d);
				if (Math.abs(value - cache) > 1) {
					value = 0;
					break;
				} else {
					value = cache;
				}
			}
			
			if (value > 0) {
				mapping.setValid(true);
			} else if (value < 0) {
				mapping.setValid(false);
			}
		}
		
		return mapping;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#register
	 * (de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments mappingArguments,
	                     final boolean isRequired) {
		
	}
	
}

/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.strategies;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MajorityStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the mayority decision of the MappingEngines.";
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
	
}

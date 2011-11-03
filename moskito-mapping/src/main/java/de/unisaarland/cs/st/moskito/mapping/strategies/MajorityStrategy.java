/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.strategies;

import java.util.List;

import de.unisaarland.cs.st.moskito.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.moskito.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MajorityStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the mayority decision of the MappingEngines.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.moskito.mapping.model.RCSBugMapping,
	 * de.unisaarland.cs.st.moskito.mapping.model.MapScore)
	 */
	@Override
	public PersistentMapping map(final PersistentMapping mapping) {
		if (mapping.getValid() == null) {
			int pro = 0;
			int contra = 0;
			@SuppressWarnings ("unused")
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

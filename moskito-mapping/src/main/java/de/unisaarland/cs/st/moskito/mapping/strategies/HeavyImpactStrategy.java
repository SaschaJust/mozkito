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

import de.unisaarland.cs.st.moskito.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.moskito.mapping.model.PersistentMapping;

/**
 * Only consider the feature with the highest impact.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HeavyImpactStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the highest confidence given by a MappingEngine.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.moskito.mapping.model.RCSBugMapping)
	 */
	@Override
	public PersistentMapping map(final PersistentMapping mapping) {
		double maxabs = 0d;
		for (MappingEngineFeature feature : mapping.getScore().getFeatures()) {
			if (Math.abs(feature.getConfidence()) > Math.abs(maxabs)) {
				maxabs = feature.getConfidence();
			}
		}
		
		switch (Double.compare(maxabs, 0d)) {
			case -1:
				mapping.setValid(false);
				break;
			case 1:
				mapping.setValid(true);
				break;
		}
		return mapping;
	}
	
}

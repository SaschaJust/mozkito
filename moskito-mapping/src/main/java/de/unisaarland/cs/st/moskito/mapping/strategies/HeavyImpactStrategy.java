/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.strategies;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import de.unisaarland.cs.st.moskito.mapping.model.IMapping;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.model.MappingEngineFeature;

// TODO: Auto-generated Javadoc
/**
 * Only consider the feature with the highest impact.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HeavyImpactStrategy extends MappingStrategy {
	
	/**
	 * After parse.
	 */
	@Override
	public void afterParse() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy# getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the highest confidence given by a MappingEngine.";
	}
	
	/**
	 * Inits the settings.
	 *
	 * @param set the set
	 * @return true, if successful
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.moskito.mapping.model.RCSBugMapping)
	 */
	@Override
	public IMapping map(final Mapping mapping) {
		double maxabs = 0d;
		for (final MappingEngineFeature feature : mapping.getFeatures()) {
			if (Math.abs(feature.getConfidence()) > Math.abs(maxabs)) {
				maxabs = feature.getConfidence();
			}
		}
		
		switch (Double.compare(maxabs, 0d)) {
			case -1:
				mapping.addStrategy(getHandle(), false);
				break;
			case 1:
				mapping.addStrategy(getHandle(), true);
				break;
			default:
				mapping.addStrategy(getHandle(), null);
		}
		return mapping;
	}
	
}

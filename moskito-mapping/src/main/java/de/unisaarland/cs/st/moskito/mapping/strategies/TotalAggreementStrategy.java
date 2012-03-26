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

import java.util.Queue;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import de.unisaarland.cs.st.moskito.mapping.model.IMapping;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.model.MappingEngineFeature;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TotalAggreementStrategy extends MappingStrategy {
	
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
		return "Maps positive/negative iff all engines agree on that";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
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
		int value = 0;
		
		final Queue<MappingEngineFeature> features = mapping.getFeatures();
		for (final MappingEngineFeature feature : features) {
			final int cache = Double.compare(feature.getConfidence(), 0.0d);
			if (Math.abs(value - cache) > 1) {
				value = 0;
				break;
			} else {
				value = cache;
			}
		}
		
		if (value > 0) {
			mapping.addStrategy(getHandle(), true);
		} else if (value < 0) {
			mapping.addStrategy(getHandle(), false);
		} else {
			mapping.addStrategy(getHandle(), null);
		}
		
		return mapping;
	}
	
}

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

import de.unisaarland.cs.st.reposuite.mapping.model.MappingEngineFeature;
import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;

/**
 * Only consider the feature with the highest impact.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class HeavyImpactStrategy extends MappingStrategy {
	
	@Override
	public String getDescription() {
		return "Maps according to the highest confidence given by a MappingEngine.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping)
	 */
	@Override
	public RCSBugMapping map(final RCSBugMapping mapping) {
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

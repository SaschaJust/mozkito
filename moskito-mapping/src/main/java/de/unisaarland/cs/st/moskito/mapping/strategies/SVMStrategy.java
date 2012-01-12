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

import de.unisaarland.cs.st.moskito.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SVMStrategy extends MappingStrategy {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#
	 * getDescription()
	 */
	@Override
	public String getDescription() {
		return "Maps according to the trained model on known mappings with the given feature vectors from the MappingEngines.";
	}
	
	/**
	 * 
	 */
	@SuppressWarnings ("unused")
	private void loadModel() {
		// REMARK
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy#map
	 * (de.unisaarland.cs.st.moskito.mapping.model.RCSBugMapping,
	 * de.unisaarland.cs.st.moskito.mapping.model.MapScore)
	 */
	@Override
	public PersistentMapping map(final PersistentMapping mapping) {
		return mapping;
	}
	
}

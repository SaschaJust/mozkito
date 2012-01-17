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
package de.unisaarland.cs.st.moskito.mapping.settings;

import net.ownhero.dev.andama.model.AndamaChain;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingSettings extends TrackerSettings {
	
	/**
	 * @param isRequired
	 * @return
	 */
	public MappingArguments setMappingArgs(final AndamaChain chain,
	                                       final boolean isRequired) {
		final MappingArguments mappingArguments = new MappingArguments(chain, this, isRequired);
		return mappingArguments;
	}
	
}

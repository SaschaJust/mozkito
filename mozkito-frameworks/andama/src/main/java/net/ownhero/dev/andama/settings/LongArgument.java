/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import net.ownhero.dev.andama.exceptions.Shutdown;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class LongArgument extends AndamaArgument<Long> {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArguments
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public LongArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public Long getValue() {
		if (this.actualValue == null) {
			return null;
		}
		
		try {
			return new Long(this.actualValue);
		} catch (NumberFormatException e) {
			throw new Shutdown("Value given for argument `" + getName()
			        + "` could not be interpreted as a Long value. Abort!");
		}
	}
}

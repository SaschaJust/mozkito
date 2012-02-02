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
package net.ownhero.dev.andama.settings;

import net.ownhero.dev.kisa.Logger;

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
	public LongArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(argumentSet, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	protected final boolean init() {
		if (!isInitialized()) {
			synchronized (this) {
				if (!isInitialized()) {
					if (getStringValue() == null) {
						setCachedValue(null);
						return true;
					}
					
					try {
						setCachedValue(Long.valueOf(getStringValue()));
					} catch (final NumberFormatException e) {
						if (Logger.logError()) {
							Logger.error("Value given for argument `" + getName()
							        + "` could not be interpreted as a Long value. Abort!");
						}
						
						return false;
					}
					return true;
				}
			}
		}
		return true;
	}
}

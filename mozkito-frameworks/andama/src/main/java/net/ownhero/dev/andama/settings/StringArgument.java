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

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class StringArgument extends AndamaArgument<String> {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public StringArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(argumentSet, name, description, defaultValue, isRequired);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	protected boolean init() {
		if (!isInitialized()) {
			synchronized (this) {
				if (!isInitialized()) {
					setCachedValue(getStringValue());
					return true;
				}
			}
		}
		return true;
	}
}

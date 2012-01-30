/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package net.ownhero.dev.andama.settings;

public class LoggerOutputFileArgument extends OutputFileArgument {
	
	LoggerOutputFileArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final boolean overwrite) {
		super(settings, name, description, defaultValue, isRequired, overwrite);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.OutputFileArgument#init()
	 */
	@Override
	protected final boolean init() {
		setCachedValue(null);
		return true;
	}
	
}

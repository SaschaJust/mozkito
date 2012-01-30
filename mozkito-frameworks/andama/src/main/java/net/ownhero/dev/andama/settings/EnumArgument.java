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
package net.ownhero.dev.andama.settings;

import java.util.HashSet;

import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class EnumArgument extends AndamaArgument<String> {
	
	private final HashSet<String> possibleValues;
	
	/**
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 */
	public EnumArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final String[] possibleValues) {
		super(settings, name, description, defaultValue, isRequired);
		this.possibleValues = new HashSet<String>();
		for (final String s : possibleValues) {
			this.possibleValues.add(s);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	protected final boolean init() {
		if (this.stringValue == null) {
			setCachedValue(null);
			return true;
		}
		final String value = this.stringValue.toUpperCase();
		
		if (!this.possibleValues.contains(value)) {
			final StringBuilder ss = new StringBuilder();
			ss.append("Value `" + value + "` set for argument `");
			ss.append(getName());
			ss.append("` is invalid.");
			ss.append(System.getProperty("line.separator"));
			ss.append("Please choose one of the following possible values:");
			ss.append(System.getProperty("line.separator"));
			
			for (final String s : this.possibleValues) {
				ss.append("\t");
				ss.append(s);
				ss.append(System.getProperty("line.separator"));
			}
			
			if (Logger.logError()) {
				Logger.error(ss.toString());
			}
			
			return false;
		}
		
		setCachedValue(value);
		return true;
	}
	
}

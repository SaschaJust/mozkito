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

import java.util.HashSet;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ListArgument extends AndamaArgument {
	
	private final String delimiter;
	
	/**
	 * General Arguments as described in RepoSuiteArgument. The string value
	 * will be split using delimiter `,` to receive the list of values.
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public ListArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		this.delimiter = ",";
	}
	
	/**
	 * 
	 * General Arguments as described in RepoSuiteArgument
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @param delimiter
	 *            The string value will be split using this delimiter to receive
	 *            the list of values
	 * @throws DuplicateArgumentException
	 */
	public ListArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final String delimiter) {
		super(settings, name, description, defaultValue, isRequired);
		this.delimiter = delimiter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public HashSet<String> getValue() {
		if (this.stringValue == null) {
			return null;
		}
		HashSet<String> result = new HashSet<String>();
		for (String s : this.stringValue.split(this.delimiter)) {
			result.add(s.trim());
		}
		return result;
	}
}

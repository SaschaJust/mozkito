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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class AndamaArgumentSet<T> {
	
	private final HashMap<String, AndamaArgument<?>> arguments;
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 */
	public AndamaArgumentSet() {
		this.arguments = new HashMap<String, AndamaArgument<?>>();
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware that you have to set all arguments
	 * before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added. <code>false</code> otherwise.
	 */
	public boolean addArgument(final AndamaArgument<?> argument) {
		if (this.arguments.containsKey(argument.getName())) {
			return false;
		}
		
		this.arguments.put(argument.getName(), argument);
		
		return true;
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return
	 */
	public Map<String, AndamaArgument<?>> getArguments() {
		return this.arguments;
	}
	
	public abstract T getValue();
}

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
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.strategies;

import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingStrategy {
	
	private MappingSettings settings;
	private boolean         registered  = false;
	private boolean         initialized = false;
	
	/**
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * @return
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return
	 */
	public MappingSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * 
	 */
	public void init() {
		Condition.check(isRegistered(), "The engine has to be registered before it is initialized. Engine: %s", this
		        .getClass().getSimpleName());
		setInitialized(true);
	}
	
	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return this.registered;
	}
	
	/**
	 * @param mapping
	 * @return
	 */
	public abstract PersistentMapping map(PersistentMapping mapping);
	
	/**
	 * @param settings
	 * @param mappingArguments
	 * @param isRequired
	 */
	public void register(final MappingSettings settings, final MappingArguments mappingArguments,
	        final boolean isRequired) {
		setSettings(settings);
		setRegistered(true);
	}
	
	/**
	 * @param initialized
	 *            the initialized to set
	 */
	public void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered
	 *            the registered to set
	 */
	public void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings
	 */
	public void setSettings(final MappingSettings settings) {
		this.settings = settings;
	}
	
}

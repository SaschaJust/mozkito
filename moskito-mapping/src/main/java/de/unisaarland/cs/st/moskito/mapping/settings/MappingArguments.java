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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.registerable.Registered;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.selectors.MappingSelector;
import de.unisaarland.cs.st.moskito.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.mapping.training.MappingTrainer;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingArguments extends AndamaArgumentSet {
	
	private final Set<Registered> registereds = new HashSet<Registered>();
	
	/**
	 * @param isRequired
	 * @param mappingSettings
	 * 
	 */
	public MappingArguments(final AndamaChain chain, final MappingSettings settings, final boolean isRequired) {
		super();
		
		this.registereds.addAll(Registered.handleRegistered(chain, settings, this, "engines", MappingEngine.class,
		                                                    isRequired));
		this.registereds.addAll(Registered.handleRegistered(chain, settings, this, "filters", MappingFilter.class,
		                                                    isRequired));
		this.registereds.addAll(Registered.handleRegistered(chain, settings, this, "selectors", MappingSelector.class,
		                                                    isRequired));
		this.registereds.addAll(Registered.handleRegistered(chain, settings, this, "splitters", MappingSplitter.class,
		                                                    isRequired));
		this.registereds.addAll(Registered.handleRegistered(chain, settings, this, "strategies", MappingStrategy.class,
		                                                    isRequired));
		this.registereds.addAll(Registered.handleRegistered(chain, settings, this, "trainers", MappingTrainer.class,
		                                                    isRequired));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public MappingFinder getValue() {
		final MappingFinder finder = new MappingFinder();
		final Map<Class<? extends Registered>, Method> methodMap = new HashMap<Class<? extends Registered>, Method>();
		final Method[] methods = finder.getClass().getMethods();
		
		try {
			Registered.initRegistereds(this.registereds);
			
			for (final Registered registered : this.registereds) {
				if (methodMap.containsKey(registered.getClass())) {
					// method finder.addXXX already known
					methodMap.get(registered.getClass()).invoke(finder, registered);
				} else {
					// look up finder.addXXX method
					boolean found = false;
					Class<?> registeredSubClass = registered.getClass();
					
					while (!found && (registeredSubClass != Object.class)) {
						for (final Method method : methods) {
							if (method.getName().startsWith("add")) {
								if ((method.getParameterTypes().length == 1)
								        && (method.getParameterTypes()[0] == registeredSubClass)) {
									found = true;
									methodMap.put(registered.getClass(), method);
									method.invoke(finder, registered);
									break;
								}
							}
						}
						registeredSubClass = registeredSubClass.getSuperclass();
					}
					if (!found) {
						
						if (Logger.logError()) {
							Logger.error("Could not find 'add' method in " + MappingFinder.class.getSimpleName()
							        + " for type " + registered.getClass().getSimpleName());
						}
						throw new UnrecoverableError("Could not find 'add' method in "
						        + MappingFinder.class.getSimpleName() + " for type "
						        + registered.getClass().getSimpleName());
					}
				}
			}
		} catch (final Exception e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		return finder;
	}
	
}

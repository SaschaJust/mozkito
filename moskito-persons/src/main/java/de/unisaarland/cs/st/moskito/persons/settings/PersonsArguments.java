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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persons.settings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persons.engine.MergingEngine;
import de.unisaarland.cs.st.moskito.persons.processing.MergingProcessor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonsArguments extends ArgumentSet<MergingProcessor> {
	
	private final Set<MergingEngine> engines = new HashSet<MergingEngine>();
	
	/**
	 * @throws ArgumentRegistrationException
	 * 
	 */
	public PersonsArguments(final ArgumentSet<?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "bleh", requirement);
		try {
			final Package package1 = MergingEngine.class.getPackage();
			ClassFinder.getClassesExtendingClass(package1, MergingEngine.class, Modifier.ABSTRACT | Modifier.INTERFACE
			        | Modifier.PRIVATE);
			
			final Collection<MergingEngine> collection = ArgumentSet.provideDynamicArguments(this,
			                                                                                 MergingEngine.class,
			                                                                                 "A list of merging engines that shall be used.",
			                                                                                 Requirement.required,
			                                                                                 null, "persons", "merge",
			                                                                                 true);
			
			final String engines = System.getProperty("persons.engines");
			final Set<String> engineNames = new HashSet<String>();
			
			if (engines != null) {
				for (final String engineName : engines.split(",")) {
					engineNames.add(MergingEngine.class.getPackage().getName() + "." + engineName);
				}
				
			}
			
			for (final MergingEngine engine : collection) {
				if (engineNames.isEmpty() || engineNames.contains(engine.getClass().getSimpleName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new MergingEngine " + engine);
					}
					
					this.engines.add(engine);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available engine: " + engine.getClass().getSimpleName());
					}
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new UnrecoverableError(e);
		} catch (final ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (final WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		boolean ret = false;
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						final MergingProcessor finder = new MergingProcessor();
						
						for (final MergingEngine engine : this.engines) {
							finder.addEngine(engine);
						}
						
						setCachedValue(finder);
						ret = true;
					} else {
						ret = true;
					}
				}
			} else {
				ret = true;
			}
			
			return ret;
		} finally {
			if (ret) {
				Condition.check(isInitialized(), "If init() returns true, the %s has to be set to initialized.",
				                getHandle());
			}
		}
	}
	
}

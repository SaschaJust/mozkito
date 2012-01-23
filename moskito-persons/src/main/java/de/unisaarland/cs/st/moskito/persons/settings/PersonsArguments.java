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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persons.settings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persons.engine.MergingEngine;
import de.unisaarland.cs.st.moskito.persons.processing.MergingProcessor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonsArguments extends AndamaArgumentSet<MergingProcessor> {
	
	private final Set<MergingEngine> engines = new HashSet<MergingEngine>();
	
	/**
	 * 
	 */
	public PersonsArguments(final PersonsSettings settings, final boolean isRequired) {
		super();
		try {
			Package package1 = MergingEngine.class.getPackage();
			Collection<Class<? extends MergingEngine>> engineClasses = ClassFinder.getClassesExtendingClass(package1,
					MergingEngine.class,
					Modifier.ABSTRACT
					| Modifier.INTERFACE
					| Modifier.PRIVATE);
			
			addArgument(new ListArgument(settings, "persons.engines", "A list of merging engines that shall be used: "
					+ buildEngineList(engineClasses), "[all]", false));
			
			String engines = System.getProperty("persons.engines");
			Set<String> engineNames = new HashSet<String>();
			
			if (engines != null) {
				for (String engineName : engines.split(",")) {
					engineNames.add(MergingEngine.class.getPackage().getName() + "." + engineName);
				}
				
			}
			
			for (Class<? extends MergingEngine> klass : engineClasses) {
				if (engineNames.isEmpty() || engineNames.contains(klass.getCanonicalName())) {
					if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
						if (Logger.logInfo()) {
							Logger.info("Adding new MergingEngine " + klass.getCanonicalName());
						}
						
						MergingEngine engine = klass.newInstance();
						engine.register(settings, this, isRequired);
						this.engines.add(engine);
					}
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available engine: " + klass.getSimpleName());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new UnrecoverableError(e);
		} catch (InstantiationException e) {
			throw new UnrecoverableError(e);
		} catch (IllegalAccessException e) {
			throw new UnrecoverableError(e);
		} catch (ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		} catch (WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * @param engines
	 * @return
	 */
	private String buildEngineList(final Collection<Class<? extends MergingEngine>> engines) {
		StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		for (Class<? extends MergingEngine> klass : engines) {
			try {
				builder.append('\t').append("  ").append(klass.getSimpleName()).append(": ")
				.append(klass.newInstance().getDescription());
			} catch (InstantiationException e) {
				if (Logger.logWarn()) {
					Logger.warn(e.getMessage(), e);
				}
			} catch (IllegalAccessException e) {
				if (Logger.logWarn()) {
					Logger.warn(e.getMessage(), e);
				}
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public MergingProcessor getValue() {
		MergingProcessor finder = new MergingProcessor();
		
		for (MergingEngine engine : this.engines) {
			engine.init();
			finder.addEngine(engine);
		}
		
		return finder;
	}
	
}

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
package de.unisaarland.cs.st.moskito.infozilla.settings;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.arguments.SetArgument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.infozilla.filters.InfozillaFilter;
import de.unisaarland.cs.st.moskito.infozilla.filters.InfozillaFilterChain;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class InfozillaArguments extends ArgumentSet<InfozillaFilterChain> {
	
	private final Set<InfozillaFilter> filters = new HashSet<InfozillaFilter>();
	
	public InfozillaArguments(final ArgumentSet<Boolean> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "TODO bla bla", requirement);
		
		try {
			final Package package1 = InfozillaFilter.class.getPackage();
			final Collection<Class<? extends InfozillaFilter>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                                                InfozillaFilter.class,
			                                                                                                                Modifier.ABSTRACT
			                                                                                                                        | Modifier.INTERFACE
			                                                                                                                        | Modifier.PRIVATE);
			final Collection<InfozillaFilter> collection = ArgumentSet.provideDynamicArguments(this,
			                                                                                   InfozillaFilter.class,
			                                                                                   "XYZ", requirement,
			                                                                                   null, "infozilla",
			                                                                                   "filter", true);
			new SetArgument(this, "mapping.filters", "A list of mapping filters that shall be used.",
			                buildFilterList(classesExtendingClass), Requirement.optional);
			
			final String filters = System.getProperty("mapping.filters");
			final Set<String> filterNames = new HashSet<String>();
			
			if (filters != null) {
				for (final String filterName : filters.split(",")) {
					filterNames.add(InfozillaFilter.class.getPackage().getName() + "." + filterName);
				}
				
			}
			
			for (final InfozillaFilter filter : collection) {
				if (filterNames.isEmpty() || filterNames.contains(filter.getClass().getSimpleName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new InfozillaFilter " + filter.getClass().getSimpleName());
					}
					
					this.filters.add(filter);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available filter: " + filter.getClass().getSimpleName());
					}
				}
			}
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * @param filters
	 * @return
	 */
	private String buildFilterList(final Collection<Class<? extends InfozillaFilter>> filters) {
		final StringBuilder builder = new StringBuilder();
		for (final Class<? extends InfozillaFilter> klass : filters) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			builder.append(klass.getSimpleName());
		}
		return builder.toString();
	}
	
	@Override
	protected boolean init() {
		// TODO Auto-generated method stub
		return false;
	}
	
}

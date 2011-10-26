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
package de.unisaarland.cs.st.reposuite.infozilla.settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.infozilla.filters.InfozillaFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.InfozillaFilterChain;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class InfozillaArguments extends AndamaArgumentSet {
	
	private final Set<InfozillaFilter> filters = new HashSet<InfozillaFilter>();
	
	public InfozillaArguments(final InfozillaSettings settings, final boolean isRequired) {
		super();
		
		try {
			Package package1 = InfozillaFilter.class.getPackage();
			Collection<Class<? extends InfozillaFilter>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                                          InfozillaFilter.class,
			                                                                                                          Modifier.ABSTRACT
			                                                                                                                  | Modifier.INTERFACE
			                                                                                                                  | Modifier.PRIVATE);
			
			addArgument(new ListArgument(settings, "mapping.filters", "A list of mapping filters that shall be used.",
			                             buildFilterList(classesExtendingClass), false));
			
			String filters = System.getProperty("mapping.filters");
			Set<String> filterNames = new HashSet<String>();
			
			if (filters != null) {
				for (String filterName : filters.split(",")) {
					filterNames.add(InfozillaFilter.class.getPackage().getName() + "." + filterName);
				}
				
			}
			
			for (Class<?> klass : classesExtendingClass) {
				if (filterNames.isEmpty() || filterNames.contains(klass.getCanonicalName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new InfozillaFilter " + klass.getCanonicalName());
					}
					
					Constructor<?> constructor = klass.getConstructor(InfozillaSettings.class);
					InfozillaFilter filter = (InfozillaFilter) constructor.newInstance(settings);
					filter.register(settings, this, isRequired);
					this.filters.add(filter);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available filter: " + klass.getSimpleName());
					}
				}
			}
		} catch (Exception e) {
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
		StringBuilder builder = new StringBuilder();
		for (Class<? extends InfozillaFilter> klass : filters) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			builder.append(klass.getSimpleName());
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public InfozillaFilterChain getValue() {
		InfozillaFilterChain chain = new InfozillaFilterChain();
		
		for (InfozillaFilter filter : this.filters) {
			filter.init();
			chain.addFilter(filter);
		}
		
		return chain;
	}
	
}

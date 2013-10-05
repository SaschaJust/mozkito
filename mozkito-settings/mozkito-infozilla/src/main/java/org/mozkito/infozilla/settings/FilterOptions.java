/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.infozilla.settings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.utilities.loading.classpath.ClassFinder;
import org.mozkito.utilities.loading.classpath.exceptions.WrongClassSearchMethodException;

/**
 * The Class FilterOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class FilterOptions extends ArgumentSetOptions<Set<Filter<?>>, ArgumentSet<Set<Filter<?>>, FilterOptions>> {
	
	private SetArgument.Options enabledFiltersOption;
	
	/**
	 * Instantiates a new filter options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public FilterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "filters", "options for the active filters", requirements);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public Set<Filter<?>> init() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'init' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		PRECONDITIONS: {
			if (set == null) {
				throw new NullPointerException();
			}
		}
		
		final HashMap<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
		final HashSet<String> defaultSet = new HashSet<String>();
		
		try {
			// first off, find all implemented engines
			@SuppressWarnings ("rawtypes")
			final Collection<Class<? extends Filter>> collection = ClassFinder.getClassesExtendingClass(getClass().getPackage(),
			                                                                                            Filter.class,
			                                                                                            Modifier.ABSTRACT
			                                                                                                    | Modifier.INTERFACE
			                                                                                                    | Modifier.PRIVATE
			                                                                                                    | Modifier.PROTECTED);
			
			// first compute the default value for the engine enabler option, i.e., all implemented engines
			for (@SuppressWarnings ("rawtypes")
			final Class<? extends Filter> filterClass : collection) {
				defaultSet.add(filterClass.getSimpleName());
			}
			
			// now create the enabler for the engines
			this.enabledFiltersOption = new SetArgument.Options(set, "enabled", "description", defaultSet,
			                                                    Requirement.required);
		} catch (final ClassNotFoundException | WrongClassSearchMethodException | IOException
		        | IllegalArgumentException | SecurityException e) {
			throw new UnrecoverableError(e);
		}
		
		map.put(this.enabledFiltersOption.getName(), this.enabledFiltersOption);
		
		return map;
		
	}
	
}

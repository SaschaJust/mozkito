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
 *****************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.mapping.model.IMapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.ByPass;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;

/**
 * The Class ByPassFilter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ByPassFilter extends MappingFilter {
	
	public static final class Options extends ArgumentSetOptions<ByPassFilter, ArgumentSet<ByPassFilter, Options>> {
		
		private static final String TAG         = "byPass";
		private static final String DESCRIPTION = "...";
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ByPassFilter init() {
			// PRECONDITIONS
			
			try {
				return new ByPassFilter();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				return new HashMap<String, IOptions<?, ?>>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	public static final String DESCRIPTION = "Does not filter at all (by-passing).";
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#filter(de
	 * .unisaarland.cs.st.reposuite.mapping.model.PersistentMapping, java.util.Set)
	 */
	@Override
	public Set<? extends MappingFilter> filter(final IMapping mapping,
	                                           final Set<MappingFilter> triggeringFilters) {
		return triggeringFilters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#getDescription ()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.filters.MappingFilter#supported()
	 */
	@Override
	public Expression supported() {
		// PRECONDITIONS
		
		try {
			return new ByPass();
		} finally {
			// POSTCONDITIONS
		}
	}
}

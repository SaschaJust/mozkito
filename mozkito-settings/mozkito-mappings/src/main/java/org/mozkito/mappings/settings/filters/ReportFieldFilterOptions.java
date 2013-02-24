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

package org.mozkito.mappings.settings.filters;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.issues.elements.Type;
import org.mozkito.mappings.filters.Filter;
import org.mozkito.mappings.filters.ReportFieldFilter;
import org.mozkito.mappings.messages.Messages;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ReportFieldFilterOptions extends
        ArgumentSetOptions<ReportFieldFilter, ArgumentSet<ReportFieldFilter, ReportFieldFilterOptions>> {
	
	/** The type option. */
	private EnumArgument.Options<Type> typeOption;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public ReportFieldFilterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, Filter.TAG, ReportFieldFilter.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public ReportFieldFilter init() {
		// PRECONDITIONS
		
		try {
			final EnumArgument<Type> typeArgument = getSettings().getArgument(this.typeOption);
			return new ReportFieldFilter(typeArgument.getValue());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			
			this.typeOption = new EnumArgument.Options<Type>(argumentSet, "type", //$NON-NLS-1$
			                                                 Messages.getString("ReportFieldFilter.typeDescription"), //$NON-NLS-1$
			                                                 ReportFieldFilter.getDefaultType(), Requirement.required);
			map.put(this.typeOption.getName(), this.typeOption);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

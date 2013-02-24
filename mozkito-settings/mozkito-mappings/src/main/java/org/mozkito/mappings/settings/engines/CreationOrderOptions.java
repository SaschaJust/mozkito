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

package org.mozkito.mappings.settings.engines;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.engines.CreationOrderEngine;
import org.mozkito.mappings.messages.Messages;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class CreationOrderOptions extends
        ArgumentSetOptions<CreationOrderEngine, ArgumentSet<CreationOrderEngine, CreationOrderOptions>> {
	
	/** The confidence option. */
	private DoubleArgument.Options confidenceOption;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public CreationOrderOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, CreationOrderEngine.TAG, CreationOrderEngine.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public CreationOrderEngine init() {
		// PRECONDITIONS
		
		try {
			final DoubleArgument confidenceArgument = getSettings().getArgument(this.confidenceOption);
			return new CreationOrderEngine(confidenceArgument.getValue());
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
			this.confidenceOption = new DoubleArgument.Options(
			                                                   argumentSet,
			                                                   "confidence", //$NON-NLS-1$
			                                                   Messages.getString("AuthorEqualityEngine.confidenceDescription"), //$NON-NLS-1$
			                                                   CreationOrderEngine.DEFAULT_CONFIDENCE,
			                                                   Requirement.required);
			map.put(this.confidenceOption.getName(), this.confidenceOption);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

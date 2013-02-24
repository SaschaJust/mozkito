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
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.engines.RegexEngine;
import org.mozkito.mappings.messages.Messages;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class RegexOptions extends ArgumentSetOptions<RegexEngine, ArgumentSet<RegexEngine, RegexOptions>> {
	
	/** The config uri option. */
	private URIArgument.Options    configURIOption;
	
	/** The unpad option. */
	private StringArgument.Options unpadOption;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public RegexOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, RegexEngine.class.getSimpleName(), Messages.getString("RegexEngine.description"), //$NON-NLS-1$
		      requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public RegexEngine init() {
		// PRECONDITIONS
		
		try {
			final URIArgument configURIArgument = getSettings().getArgument(this.configURIOption);
			final RegexEngine engine = new RegexEngine(configURIArgument.getValue());
			final StringArgument unpadArgument = getSettings().getArgument(this.unpadOption);
			
			if (unpadArgument.getValue() != null) {
				engine.unpad = unpadArgument.getValue();
			}
			
			return engine;
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
			this.configURIOption = new URIArgument.Options(argumentSet, "config", //$NON-NLS-1$
			                                               Messages.getString("RegexEngine.configDescription"), //$NON-NLS-1$
			                                               null, Requirement.required);
			map.put(this.configURIOption.getName(), this.configURIOption);
			
			this.unpadOption = new StringArgument.Options(
			                                              argumentSet,
			                                              "unpad", Messages.getString("RegexEngine.unpadDescription"), null, Requirement.optional); //$NON-NLS-1$ //$NON-NLS-2$
			map.put(this.unpadOption.getName(), this.unpadOption);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

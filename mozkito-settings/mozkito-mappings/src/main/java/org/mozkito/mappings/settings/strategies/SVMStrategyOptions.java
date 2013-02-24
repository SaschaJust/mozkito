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

package org.mozkito.mappings.settings.strategies;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.strategies.SVMStrategy;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class SVMStrategyOptions extends ArgumentSetOptions<SVMStrategy, ArgumentSet<SVMStrategy, SVMStrategyOptions>> {
	
	/** The negative file option. */
	private InputFileArgument.Options negativeFileOption;
	
	/** The positive file option. */
	private InputFileArgument.Options positiveFileOption;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public SVMStrategyOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, SVMStrategy.TAG, SVMStrategy.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public SVMStrategy init() {
		// PRECONDITIONS
		
		try {
			final InputFileArgument positiveFileArgument = getSettings().getArgument(this.positiveFileOption);
			final InputFileArgument negativeFileArgument = getSettings().getArgument(this.negativeFileOption);
			return new SVMStrategy(positiveFileArgument.getValue(), negativeFileArgument.getValue());
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
		final Map<String, IOptions<?, ?>> map = new HashMap<>();
		
		this.positiveFileOption = new InputFileArgument.Options(
		                                                        argumentSet,
		                                                        "positiveSamples", Messages.getString("SVMStrategy.optionPositiveSamples"), null, //$NON-NLS-1$ //$NON-NLS-2$
		                                                        Requirement.required);
		map.put(this.positiveFileOption.getName(), this.positiveFileOption);
		this.negativeFileOption = new InputFileArgument.Options(
		                                                        argumentSet,
		                                                        "negativeSamples", Messages.getString("SVMStrategy.optionNevativeSamples"), null, //$NON-NLS-1$ //$NON-NLS-2$
		                                                        Requirement.required);
		map.put(this.negativeFileOption.getName(), this.negativeFileOption);
		
		return map;
	}
	
}

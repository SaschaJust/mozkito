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

package org.mozkito.mappings.settings.selectors;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.selectors.ReportRegexSelector;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ReportRegexSelectorOptions extends
        ArgumentSetOptions<ReportRegexSelector, ArgumentSet<ReportRegexSelector, ReportRegexSelectorOptions>> {
	
	/** The pattern option. */
	private StringArgument.Options                                patternOption;
	
	/** The tag option. */
	private net.ownhero.dev.hiari.settings.StringArgument.Options tagOption;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public ReportRegexSelectorOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, ReportRegexSelector.TAG, ReportRegexSelector.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public ReportRegexSelector init() {
		// PRECONDITIONS
		
		try {
			final StringArgument patternArgument = getSettings().getArgument(this.patternOption);
			final StringArgument tagArgument = getSettings().getArgument(this.tagOption);
			final String tag = tagArgument.getValue();
			final ReportRegexSelector selector = new ReportRegexSelector(tag, patternArgument.getValue());
			
			return selector;
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
			this.patternOption = new StringArgument.Options(argumentSet, "pattern", //$NON-NLS-1$
			                                                Messages.getString("ReportRegexSelector.optionPattern"), //$NON-NLS-1$
			                                                ReportRegexSelector.DEFAULT_PATTERN, Requirement.required);
			map.put(this.patternOption.getName(), this.patternOption);
			
			this.tagOption = new StringArgument.Options(argumentSet, "tag", //$NON-NLS-1$
			                                            Messages.getString("ReportRegexSelector.optionTag"), //$NON-NLS-1$
			                                            null, Requirement.optional);
			map.put(this.tagOption.getName(), this.tagOption);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

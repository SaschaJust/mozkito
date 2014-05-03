/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package net.ownhero.dev.andama.messages;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class EventsOptions extends ArgumentSetOptions<Boolean, ArgumentSet<Boolean, EventsOptions>> {
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param requirements
	 */
	public EventsOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "events", "Encapsulates settings for eventhandlers", requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public Boolean init() {
		// PRECONDITIONS
		
		try {
			return true;
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
			return new HashMap<String, IOptions<?, ?>>();
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

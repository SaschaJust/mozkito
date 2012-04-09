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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

/**
 * The Class MantisOptions.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ProxyOptions extends ArgumentSetOptions<ProxyConfig, ArgumentSet<ProxyConfig, ProxyOptions>> {
	
	private Options                                             hostOptions;
	private net.ownhero.dev.hiari.settings.LongArgument.Options portOptions;
	private Options                                             usernameOptions;
	private Options                                             passwordOptions;
	
	/**
	 * Instantiates a new mantis options.
	 * 
	 * @param trackerOptions
	 *            the tracker options
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public ProxyOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement) {
		super(argumentSet, "proxy", "Arguments to configure HTTP/S proxy usage.", requirement);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	@NoneNull
	public ProxyConfig init() {
		// PRECONDITIONS
		
		try {
			
			final StringArgument hostArgument = getSettings().getArgument(this.hostOptions);
			final LongArgument portArgument = getSettings().getArgument(this.portOptions);
			final StringArgument usernameArgument = getSettings().getArgument(this.usernameOptions);
			final StringArgument passwordArgument = getSettings().getArgument(this.passwordOptions);
			
			return new ProxyConfig(hostArgument.getValue(), portArgument.getValue().intValue(),
			                       usernameArgument.getValue(), passwordArgument.getValue());
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Req.
	 * 
	 * @param option
	 *            the option
	 * @param map
	 *            the map
	 */
	private final void req(final IOptions<?, ?> option,
	                       final Map<String, IOptions<?, ?>> map) {
		map.put(option.getName(), option);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	@NoneNull
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			this.hostOptions = new StringArgument.Options(set, "host", //$NON-NLS-1$
			                                              Messages.getString("ProxyOptions.proxyHost_description"), //$NON-NLS-1$
			                                              null, Requirement.optional);
			req(this.hostOptions, map);
			
			this.portOptions = new LongArgument.Options(
			                                            set,
			                                            "port", Messages.getString("ProxyOptions.proxyPort_description"), null, //$NON-NLS-1$ //$NON-NLS-2$
			                                            Requirement.iff(this.hostOptions));
			req(this.portOptions, map);
			
			this.usernameOptions = new StringArgument.Options(
			                                                  set,
			                                                  "username", //$NON-NLS-1$
			                                                  Messages.getString("ProxyOptions.proxyUser_description"), null, //$NON-NLS-1$
			                                                  Requirement.iff(this.hostOptions));
			
			req(this.usernameOptions, map);
			this.passwordOptions = new StringArgument.Options(
			                                                  set,
			                                                  "password", //$NON-NLS-1$
			                                                  Messages.getString("ProxyOptions.proxyPassword_description"), //$NON-NLS-1$
			                                                  null, Requirement.iff(this.hostOptions), true);
			
			req(this.passwordOptions, map);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.jira.JiraTracker;

/**
 * The Class JiraOptions.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class JiraOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, JiraOptions>> implements
        ITrackerOptions {
	
	/** The project key arg. */
	private Options     projectKeyOptions;
	private String      projectName;
	private JiraTracker tracker;
	
	/**
	 * Instantiates a new jira options.
	 * 
	 * @param trackerOptions
	 *            the tracker options
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public JiraOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "jira", "Necessary arguments to connect and parse jira reports.",
		      requirement);
	}
	
	/**
	 * Gets the project key.
	 * 
	 * @return the project key
	 */
	public StringArgument.Options getProjectKey() {
		return this.projectKeyOptions;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	@NoneNull
	public Tracker init() {
		// PRECONDITIONS
		
		try {
			
			this.projectName = getSettings().getArgument(getProjectKey()).getValue();
			this.tracker = new JiraTracker();
			return this.tracker;
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
			
			this.projectKeyOptions = new StringArgument.Options(
			                                                    set,
			                                                    "projectKey",
			                                                    "Project key that identifies the project's reports (e.g. 'XSTR' for XStream).",
			                                                    null, Requirement.required);
			req(this.projectKeyOptions, map);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.bugs.tracker.settings.ITrackerOptions#setup(java.net.URI, java.lang.String,
	 * java.lang.String, net.ownhero.dev.ioda.ProxyConfig)
	 */
	@Override
	public void setup(final URI trackerUri,
	                  final String trackerUser,
	                  final String trackerPassword,
	                  final ProxyConfig proxyConfig) {
		// PRECONDITIONS
		
		try {
			this.tracker.setup(trackerUri, trackerUser, trackerPassword, this.projectName, proxyConfig);
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
}

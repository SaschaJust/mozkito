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
package org.mozkito.issues.tracker.settings;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.jira.JiraTracker;

/**
 * The Class JiraOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class JiraOptions extends ArgumentSetOptions<Boolean, ArgumentSet<Boolean, JiraOptions>> implements
        ITrackerOptions {
	
	/** The project key arg. */
	private Options projectKeyOptions;
	
	/** The project name. */
	private String  projectName;
	
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
	public Boolean init() {
		// PRECONDITIONS
		
		try {
			
			this.projectName = getSettings().getArgument(getProjectKey()).getValue();
			return true;
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
	private void req(final IOptions<?, ?> option,
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
	 * @see org.mozkito.bugs.tracker.settings.ITrackerOptions#setup(java.net.URI, java.lang.String, java.lang.String,
	 * net.ownhero.dev.ioda.ProxyConfig)
	 */
	@Override
	public Tracker setup(final IssueTracker issueTracker,
	                     final URI trackerUri,
	                     final String trackerUser,
	                     final String trackerPassword) {
		// PRECONDITIONS
		
		try {
			getSettings().getArgumentSet(this).getValue();
			final JiraTracker tracker = new JiraTracker(issueTracker);
			tracker.setup(trackerUri, trackerUser, trackerPassword, this.projectName);
			return tracker;
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
}

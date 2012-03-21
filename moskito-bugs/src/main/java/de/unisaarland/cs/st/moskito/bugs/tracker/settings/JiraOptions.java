/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.jira.JiraTracker;

/**
 * The Class JiraOptions.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class JiraOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, JiraOptions>> {
	
	/** The tracker options. */
	private final TrackerOptions trackerOptions;
	
	/** The project key arg. */
	private Options              projectKeyArg;
	
	/**
	 * Instantiates a new jira options.
	 *
	 * @param trackerOptions the tracker options
	 * @param requirement the requirement
	 */
	@NoneNull
	public JiraOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "jira", "Necessary arguments to connect and parse jira reports.",
		      requirement);
		
		this.trackerOptions = trackerOptions;
		
	}
	
	/**
	 * Gets the project key.
	 *
	 * @return the project key
	 */
	public StringArgument.Options getProjectKey() {
		return this.projectKeyArg;
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
			
			final URIArgument trackerURIArgument = (URIArgument) getSettings().getArgument(this.trackerOptions.getTrackerURI()
			                                                                                                  .getTag());
			
			final StringArgument trackerUserArgument = (StringArgument) getSettings().getArgument(this.trackerOptions.getTrackerUser()
			                                                                                                         .getTag());
			final StringArgument trackerPasswordArgument = (StringArgument) getSettings().getArgument(this.trackerOptions.getTrackerPassword()
			                                                                                                             .getTag());
			
			final StringArgument projectNameArgument = (StringArgument) getSettings().getArgument(getProjectKey().getTag());
			
			final JiraTracker tracker = new JiraTracker();
			tracker.setup(trackerURIArgument.getValue(), trackerUserArgument.getValue(),
			              trackerPasswordArgument.getValue(), projectNameArgument.getValue());
			return tracker;
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Req.
	 *
	 * @param option the option
	 * @param map the map
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
			
			req(this.trackerOptions, map);
			
			this.projectKeyArg = new StringArgument.Options(
			                                                set,
			                                                "projectKey",
			                                                "Project key that identifies the project's reports (e.g. 'XSTR' for XStream).",
			                                                null, Requirement.required);
			
			req(this.projectKeyArg, map);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

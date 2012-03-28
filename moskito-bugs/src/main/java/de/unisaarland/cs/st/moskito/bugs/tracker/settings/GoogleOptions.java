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
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.google.GoogleTracker;

/**
 * The Class GoogleOptions.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class GoogleOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, GoogleOptions>> {
	
	/** The tracker options. */
	private final TrackerOptions trackerOptions;
	
	/** The project name arg. */
	private Options              projectNameArg;
	
	/**
	 * Instantiates a new google options.
	 * 
	 * @param trackerOptions
	 *            the tracker options
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public GoogleOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "google", "Necessary arguments to connect and parse google reports.",
		      requirement);
		
		this.trackerOptions = trackerOptions;
		
	}
	
	/**
	 * Gets the project name.
	 * 
	 * @return the project name
	 */
	public StringArgument.Options getProjectName() {
		return this.projectNameArg;
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
			
			final StringArgument trackerUserArgument = getSettings().getArgument(this.trackerOptions.getTrackerUser());
			final StringArgument trackerPasswordArgument = getSettings().getArgument(this.trackerOptions.getTrackerPassword());
			
			final StringArgument projectNameArgument = getSettings().getArgument(getProjectName());
			
			final GoogleTracker tracker = new GoogleTracker();
			tracker.setup(trackerUserArgument.getValue(), trackerPasswordArgument.getValue(),
			              projectNameArgument.getValue());
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
			
			req(this.trackerOptions, map);
			
			this.projectNameArg = new StringArgument.Options(
			                                                 set,
			                                                 "projectName",
			                                                 "Project name that identifies the project's tracker on Google project hosting (e.g. 'google-web-toolkit' for http://code.google.com/p/google-web-toolkit/issues/list).",
			                                                 null, Requirement.required);
			
			req(this.projectNameArg, map);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

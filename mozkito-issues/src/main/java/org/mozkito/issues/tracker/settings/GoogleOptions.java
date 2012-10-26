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
package org.mozkito.issues.tracker.settings;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.google.GoogleTracker;

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

/**
 * The Class GoogleOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GoogleOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, GoogleOptions>> implements
        ITrackerOptions {
	
	/** The project name arg. */
	private Options       projectNameArg;
	
	/** The project name. */
	private String        projectName;
	
	/** The tracker. */
	private GoogleTracker tracker;
	
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
			
			this.projectName = getSettings().getArgument(getProjectName()).getValue();
			
			this.tracker = new GoogleTracker();
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.settings.ITrackerOptions#setup(java.net.URI, java.lang.String,
	 * java.lang.String, net.ownhero.dev.ioda.ProxyConfig)
	 */
	@Override
	public void setup(final URI trackerUri,
	                  final String trackerUser,
	                  final String trackerPassword,
	                  final ProxyConfig proxyConfig) {
		// PRECONDITIONS
		
		try {
			this.tracker.setup(trackerUser, trackerPassword, this.projectName, proxyConfig);
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

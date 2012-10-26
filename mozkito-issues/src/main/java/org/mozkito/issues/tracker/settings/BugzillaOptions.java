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
import org.mozkito.issues.tracker.bugzilla.BugzillaTracker;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.URIArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class BugzillaOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class BugzillaOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, BugzillaOptions>> implements
        ITrackerOptions {
	
	/** The overview uri arg. */
	private URIArgument.Options    overviewURIArg;
	
	/** The bugzilla version arg. */
	private StringArgument.Options bugzillaVersionArg;
	
	/** The tracker uri options. */
	private Options                trackerURIOptions;
	
	/** The tracker. */
	private BugzillaTracker        tracker;
	
	/** The overview argument. */
	private URIArgument            overviewArgument;
	
	/** The bugzilla version argument. */
	private StringArgument         bugzillaVersionArgument;
	
	/**
	 * Instantiates a new bugzilla options.
	 * 
	 * @param trackerOptions
	 *            the tracker options
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public BugzillaOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "bugzilla",
		      "Necessary arguments to connect and parse bugzilla reports.", requirement);
		
	}
	
	/**
	 * Gets the bugzilla version.
	 * 
	 * @return the bugzilla version
	 */
	public StringArgument.Options getBugzillaVersion() {
		return this.bugzillaVersionArg;
	}
	
	/**
	 * Gets the overview uri.
	 * 
	 * @return the overview uri
	 */
	public URIArgument.Options getOverviewURI() {
		// PRECONDITIONS
		
		try {
			return this.overviewURIArg;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the tracker uri options.
	 *
	 * @return the tracker uri options
	 */
	public URIArgument.Options getTrackerURIOptions() {
		// PRECONDITIONS
		
		try {
			return this.trackerURIOptions;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.trackerURIOptions, "Field '%s' in '%s'.", "trackerURIArg",
			                  getClass().getSimpleName());
		}
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
			
			this.overviewArgument = getSettings().getArgument(getOverviewURI());
			this.bugzillaVersionArgument = getSettings().getArgument(getBugzillaVersion());
			this.tracker = new BugzillaTracker();
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
			
			this.overviewURIArg = new URIArgument.Options(
			                                              set,
			                                              "overviewURI",
			                                              "URI to extract bug report IDs from (e.g. https://bugzilla.mozilla.org/buglist.cgi?product=Rhino).",
			                                              null, Requirement.required);
			this.bugzillaVersionArg = new StringArgument.Options(set, "bugzillaVersion",
			                                                     "Version of the bugzilla tracker. (e.g. 4.0.4).",
			                                                     "4.0.4", Requirement.required);
			
			req(this.overviewURIArg, map);
			req(this.bugzillaVersionArg, map);
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
			this.tracker.setup(trackerUri, trackerUser, trackerPassword, this.overviewArgument.getValue(),
			                   this.bugzillaVersionArgument.getValue(), proxyConfig);
			
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

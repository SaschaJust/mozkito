/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.TrackerType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, TrackerOptions>> {
	
	private URIArgument.Options               trackerURI;
	private EnumArgument.Options<TrackerType> trackerType;
	private StringArgument.Options            trackerUser;
	private StringArgument.Options            trackerPassword;
	private final ISettings                   settings;
	
	public TrackerOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "tracker", "Tracker settings.", requirement);
		this.settings = argumentSet.getSettings();
	}
	
	/**
	 * @return the trackerPassword
	 */
	public final StringArgument.Options getTrackerPassword() {
		return this.trackerPassword;
	}
	
	/**
	 * @return the trackerType
	 */
	public final EnumArgument.Options<TrackerType> getTrackerType() {
		return this.trackerType;
	}
	
	/**
	 * @return the trackerFetchURI
	 */
	public final URIArgument.Options getTrackerURI() {
		return this.trackerURI;
	}
	
	/**
	 * @return the trackerUser
	 */
	public final StringArgument.Options getTrackerUser() {
		return this.trackerUser;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	public Tracker init(final Map<String, IArgument<?, ?>> dependencies) {
		// PRECONDITIONS
		//
		// try {
		// Class<? extends Tracker> trackerHandler = null;
		// try {
		//
		// trackerHandler = TrackerFactory.getTrackerHandler(this.trackerType.getValue());
		//
		// final Tracker tracker = trackerHandler.newInstance();
		//
		// tracker.setup(this.trackerURI.getValue(), this.trackerOverviewURI.getValue(),
		// this.trackerPattern.getValue(), this.trackerUser.getValue(),
		// this.trackerPassword.getValue(), this.trackerStart.getValue(),
		// this.trackerStop.getValue(), this.trackerCacheDir.getValue());
		// setCachedValue(tracker);
		// ret = true;
		// } catch (final UnregisteredTrackerTypeException e) {
		// throw new UnrecoverableError(e);
		// } catch (final InstantiationException e) {
		// throw new InstantiationError(e, trackerHandler, null, this.trackerURI.getValue(),
		// this.trackerOverviewURI.getValue(), this.trackerPattern.getValue(),
		// this.trackerUser.getValue(), this.trackerPassword.getValue(),
		// this.trackerStart.getValue(), this.trackerStop.getValue(),
		// this.trackerCacheDir.getValue());
		// } catch (final IllegalAccessException e) {
		// throw new UnrecoverableError(e);
		// } catch (final InvalidParameterException e) {
		// throw new UnrecoverableError(e);
		// }
		// } finally {
		// // POSTCONDITIONS
		// }
		// TODO yahoo
		return null;
	}
	
	private final void req(final IOptions<?, ?> option,
	                       final Map<String, IOptions<?, ?>> map) {
		map.put(option.getName(), option);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.trackerURI = new URIArgument.Options(
			                                          set,
			                                          "uri",
			                                          "Base URI of the tracker (to fetch reports, e.g. https://bugs.eclipse.org).",
			                                          null, Requirement.required);
			req(this.trackerURI, map);
			
			this.trackerType = new EnumArgument.Options<TrackerType>(set, "type",
			                                                         "The type of the bug tracker to analyze.", null,
			                                                         Requirement.required, TrackerType.values());
			req(this.trackerType, map);
			
			this.trackerUser = new StringArgument.Options(set, "user", "Username to access tracker", null,
			                                              Requirement.optional);
			
			req(this.trackerUser, map);
			this.trackerPassword = new StringArgument.Options(set, "password", "Password to access tracker", null,
			                                                  Requirement.optional);
			
			req(this.trackerPassword, map);
			
			req(new BugzillaOptions(set, Requirement.equals(this.trackerType, TrackerType.BUGZILLA)), map);
			// this.trackerOverviewURI = new URIArgument.Options(
			// set,
			// "overviewURI",
			// "URI pointing to the overview URL that contains the relevant bug IDs.",
			// null, Requirement.optional);
			// this.trackerPattern = new StringArgument.Options(
			// set,
			// "pattern",
			// "The filename pattern the bugs have to match to be accepted. Thus will be appended to the fetchURI and should contain the bug ID placeholder.",
			// null, Requirement.optional);
			//
			// this.trackerStart = new LongArgument.Options(set, "start", "BugID to start with", 1l,
			// Requirement.optional);
			// this.trackerStop = new LongArgument.Options(set, "stop", "BugID to stop at", null, Requirement.optional);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
}

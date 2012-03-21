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
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.BugzillaTracker;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BugzillaOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, BugzillaOptions>> {
	
	private final TrackerOptions   trackerOptions;
	private URIArgument.Options    overviewURIArg;
	private StringArgument.Options bugzillaVersionArg;
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param requirements
	 */
	@NoneNull
	public BugzillaOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "bugzilla",
		      "Necessary arguments to connect and parse bugzilla reports.", requirement);
		
		this.trackerOptions = trackerOptions;
		
	}
	
	public StringArgument.Options getBugzillaVersion() {
		return this.bugzillaVersionArg;
	}
	
	public URIArgument.Options getOverviewURI() {
		// PRECONDITIONS
		
		try {
			return this.overviewURIArg;
		} finally {
			// POSTCONDITIONS
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
			
			final URIArgument trackerURIArgument = (URIArgument) getSettings().getArgument(this.trackerOptions.getTrackerURI()
			                                                                                                  .getTag());
			
			final StringArgument trackerUserArgument = (StringArgument) getSettings().getArgument(this.trackerOptions.getTrackerUser()
			                                                                                                         .getTag());
			final StringArgument trackerPasswordArgument = (StringArgument) getSettings().getArgument(this.trackerOptions.getTrackerPassword()
			                                                                                                             .getTag());
			
			final URIArgument overviewArgument = (URIArgument) getSettings().getArgument(getOverviewURI().getTag());
			final StringArgument bugzillaVersionArgument = (StringArgument) getSettings().getArgument(getBugzillaVersion().getTag());
			
			final BugzillaTracker tracker = new BugzillaTracker();
			tracker.setup(trackerURIArgument.getValue(), trackerUserArgument.getValue(),
			              trackerPasswordArgument.getValue(), overviewArgument.getValue(),
			              bugzillaVersionArgument.getValue());
			return tracker;
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITIONS
		}
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
	@NoneNull
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			req(this.trackerOptions, map);
			
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
	
}

/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.net.URI;
import java.util.Map;

import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.andama.settings.MaskedStringArgument;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.andama.settings.URIArgument;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.TrackerFactory;
import de.unisaarland.cs.st.moskito.bugs.tracker.TrackerType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerArguments extends AndamaArgumentSet<Tracker> {
	
	protected TrackerArguments(final AndamaSettings settings, final boolean isRequired) {
		super();
		
		addArgument(new URIArgument(settings, "tracker.fetchURI",
				"Basis URI used to fecth the reports (must not contain the bug ID placeholder).",
				null, isRequired));
		addArgument(new URIArgument(settings, "tracker.overviewURI",
				"URI pointing to the overview URL that contains the relevant bug IDs.", null, false));
		addArgument(new StringArgument(
				settings,
				"tracker.pattern",
				"The filename pattern the bugs have to match to be accepted. Thus will be appended to the fetchURI and should contain the bug ID placeholder.",
				null, false));
		addArgument(new EnumArgument(
				settings,
				"tracker.type",
				"The type of the bug tracker to analyze (possible values are: BUGZILLA (default), ISSUEZILLA, JIRA, SOURCEFORGE, GOOGLE.",
				null, isRequired, JavaUtils.enumToArray(TrackerType.BUGZILLA)));
		addArgument(new MaskedStringArgument(settings, "tracker.user", "Username to access tracker", null, false));
		addArgument(new MaskedStringArgument(settings, "tracker.password", "Password to access tracker", null, false));
		addArgument(new LongArgument(settings, "tracker.start", "BugID to start with", "1", false));
		addArgument(new LongArgument(settings, "tracker.stop", "BugID to stop at", null, true));
		addArgument(new StringArgument(settings, "tracker.cachedir", "Cache directory to store raw data", null, false));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Tracker getValue() {
		Map<String, AndamaArgument<?>> arguments = getArguments();
		
		if (JavaUtils.AnyNull(arguments.get("tracker.fetchURI").getValue(), arguments.get("tracker.type").getValue())) {
			return null;
		}
		
		TrackerType trackerType = TrackerType.valueOf(arguments.get("tracker.type").getValue().toString().toUpperCase());
		try {
			Class<? extends Tracker> trackerHandler = TrackerFactory.getTrackerHandler(trackerType);
			Tracker tracker = trackerHandler.newInstance();
			
			URI fetchURIArg = (URI) (arguments.get("tracker.fetchURI") != null
					? arguments.get("tracker.fetchURI")
							.getValue()
							: null);
			URI overviewURIArg = (URI) (arguments.get("tracker.overviewURI") != null
					? arguments.get("tracker.overviewURI")
							.getValue()
							: null);
			
			String patternArg = (String) (arguments.get("tracker.pattern") != null
					? arguments.get("tracker.pattern")
							.getValue()
							: null);
			String usernameArg = (String) (arguments.get("tracker.username") != null
					? arguments.get("tracker.username")
							.getValue()
							: null);
			String passwordArg = (String) (arguments.get("tracker.password") != null
					? arguments.get("tracker.password")
							.getValue()
							: null);
			Long startArg = (Long) (arguments.get("tracker.start") != null
					? arguments.get("tracker.start").getValue()
							: null);
			Long stopArg = (Long) (arguments.get("tracker.stop") != null
					? arguments.get("tracker.stop").getValue()
							: null);
			String cacheDirArg = (String) (arguments.get("tracker.cachedir") != null
					? arguments.get("tracker.cachedir")
							.getValue()
							: null);
			
			tracker.setup(fetchURIArg, overviewURIArg, patternArg, usernameArg, passwordArg, startArg, stopArg,
					cacheDirArg);
			return tracker;
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
}

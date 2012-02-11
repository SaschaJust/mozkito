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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.InstantiationError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.arguments.EnumArgument;
import net.ownhero.dev.andama.settings.arguments.LongArgument;
import net.ownhero.dev.andama.settings.arguments.MaskedStringArgument;
import net.ownhero.dev.andama.settings.arguments.StringArgument;
import net.ownhero.dev.andama.settings.arguments.URIArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.exceptions.UnregisteredTrackerTypeException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.TrackerFactory;
import de.unisaarland.cs.st.moskito.bugs.tracker.TrackerType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TrackerArguments extends ArgumentSet<Tracker> {
	
	private final URIArgument               trackerFetchURI;
	private final URIArgument               trackerOverviewURI;
	private final StringArgument            trackerPattern;
	private final EnumArgument<TrackerType> trackerType;
	private final MaskedStringArgument      trackerUser;
	private final MaskedStringArgument      trackerPassword;
	private final LongArgument              trackerStart;
	private final LongArgument              trackerStop;
	
	private final StringArgument            trackerCacheDir;
	
	protected TrackerArguments(final ArgumentSet<?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "Tracker settings.", requirement);
		
		this.trackerFetchURI = new URIArgument(
		                                       argumentSet,
		                                       "tracker.fetchURI",
		                                       "Basis URI used to fecth the reports (must not contain the bug ID placeholder).",
		                                       null, requirement);
		addArgument(this.trackerFetchURI);
		this.trackerOverviewURI = new URIArgument(
		                                          argumentSet,
		                                          "tracker.overviewURI",
		                                          "URI pointing to the overview URL that contains the relevant bug IDs.",
		                                          null, new Optional());
		addArgument(this.trackerOverviewURI);
		this.trackerPattern = new StringArgument(
		                                         argumentSet,
		                                         "tracker.pattern",
		                                         "The filename pattern the bugs have to match to be accepted. Thus will be appended to the fetchURI and should contain the bug ID placeholder.",
		                                         null, new Optional());
		addArgument(this.trackerPattern);
		this.trackerType = new EnumArgument<TrackerType>(argumentSet, "tracker.type",
		                                                 "The type of the bug tracker to analyze.",
		                                                 TrackerType.BUGZILLA, requirement);
		addArgument(this.trackerType);
		this.trackerUser = new MaskedStringArgument(argumentSet, "tracker.user", "Username to access tracker", null,
		                                            new Optional());
		addArgument(this.trackerUser);
		this.trackerPassword = new MaskedStringArgument(argumentSet, "tracker.password", "Password to access tracker",
		                                                null, new Optional());
		addArgument(this.trackerPassword);
		this.trackerStart = new LongArgument(argumentSet, "tracker.start", "BugID to start with", "1", new Optional());
		addArgument(this.trackerStart);
		this.trackerStop = new LongArgument(argumentSet, "tracker.stop", "BugID to stop at", null, new Optional());
		addArgument(this.trackerStop);
		this.trackerCacheDir = new StringArgument(argumentSet, "tracker.cachedir", "Cache directory to store raw data",
		                                          null, new Optional());
		addArgument(this.trackerCacheDir);
	}
	
	/**
	 * @return the trackerCacheDir
	 */
	public final StringArgument getTrackerCacheDir() {
		return this.trackerCacheDir;
	}
	
	/**
	 * @return the trackerFetchURI
	 */
	public final URIArgument getTrackerFetchURI() {
		return this.trackerFetchURI;
	}
	
	/**
	 * @return the trackerOverviewURI
	 */
	public final URIArgument getTrackerOverviewURI() {
		return this.trackerOverviewURI;
	}
	
	/**
	 * @return the trackerPassword
	 */
	public final MaskedStringArgument getTrackerPassword() {
		return this.trackerPassword;
	}
	
	/**
	 * @return the trackerPattern
	 */
	public final StringArgument getTrackerPattern() {
		return this.trackerPattern;
	}
	
	/**
	 * @return the trackerStart
	 */
	public final LongArgument getTrackerStart() {
		return this.trackerStart;
	}
	
	/**
	 * @return the trackerStop
	 */
	public final LongArgument getTrackerStop() {
		return this.trackerStop;
	}
	
	/**
	 * @return the trackerType
	 */
	public final EnumArgument<TrackerType> getTrackerType() {
		return this.trackerType;
	}
	
	/**
	 * @return the trackerUser
	 */
	public final MaskedStringArgument getTrackerUser() {
		return this.trackerUser;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						Class<? extends Tracker> trackerHandler = null;
						try {
							
							trackerHandler = TrackerFactory.getTrackerHandler(this.trackerType.getValue());
							
							final Tracker tracker = trackerHandler.newInstance();
							
							tracker.setup(this.trackerFetchURI.getValue(), this.trackerOverviewURI.getValue(),
							              this.trackerPattern.getValue(), this.trackerUser.getValue(),
							              this.trackerPassword.getValue(), this.trackerStart.getValue(),
							              this.trackerStop.getValue(), this.trackerCacheDir.getValue());
							setCachedValue(tracker);
							ret = true;
						} catch (final UnregisteredTrackerTypeException e) {
							throw new UnrecoverableError(e);
						} catch (final InstantiationException e) {
							throw new InstantiationError(e, trackerHandler, null, this.trackerFetchURI.getValue(),
							                             this.trackerOverviewURI.getValue(),
							                             this.trackerPattern.getValue(), this.trackerUser.getValue(),
							                             this.trackerPassword.getValue(), this.trackerStart.getValue(),
							                             this.trackerStop.getValue(), this.trackerCacheDir.getValue());
						} catch (final IllegalAccessException e) {
							throw new UnrecoverableError(e);
						} catch (final InvalidParameterException e) {
							throw new UnrecoverableError(e);
						}
					} else {
						ret = true;
						setCachedValue(null);
					}
				}
			} else {
				ret = true;
				setCachedValue(null);
			}
			
			return ret;
		} finally {
			
		}
	}
}

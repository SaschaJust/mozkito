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
/**
 * 
 */
package org.mozkito.issues.tracker.settings;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
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

import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.TrackerType;

/**
 * The Class TrackerOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TrackerOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, TrackerOptions>> {
	
	/** The tracker type arg. */
	private EnumArgument.Options<TrackerType> trackerTypeArg;
	
	/** The tracker user arg. */
	private StringArgument.Options            trackerUserArg;
	
	/** The tracker password arg. */
	private StringArgument.Options            trackerPasswordArg;
	
	/** The bugzilla options. */
	private BugzillaOptions                   bugzillaOptions;
	
	/** The google options. */
	private GoogleOptions                     googleOptions;
	
	/** The jira options. */
	private JiraOptions                       jiraOptions;
	
	/** The mantis options. */
	private MantisOptions                     mantisOptions;
	
	/** The sourceforge options. */
	private SourceforgeOptions                sourceforgeOptions;
	
	/** The proxy options. */
	private ProxyOptions                      proxyOptions;
	
	/** The tracker uri options. */
	private Options                           trackerURIOptions;
	
	/** The use proxy options. */
	private BooleanArgument.Options           useProxyOptions;
	
	/**
	 * Instantiates a new tracker options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirement
	 *            the requirement
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	@NoneNull
	public TrackerOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement)
	        throws ArgumentRegistrationException {
		super(argumentSet, "tracker", "Tracker settings.", requirement); //$NON-NLS-1$ //$NON-NLS-2$
		argumentSet.getSettings();
	}
	
	/**
	 * Gets the proxy options.
	 * 
	 * @return the proxy options
	 */
	public final ProxyOptions getProxyOptions() {
		return this.proxyOptions;
	}
	
	/**
	 * Gets the tracker password.
	 * 
	 * @return the trackerPassword
	 */
	public final StringArgument.Options getTrackerPassword() {
		return this.trackerPasswordArg;
	}
	
	/**
	 * Gets the tracker type.
	 * 
	 * @return the trackerType
	 */
	public final EnumArgument.Options<TrackerType> getTrackerType() {
		return this.trackerTypeArg;
	}
	
	/**
	 * Gets the tracker user.
	 * 
	 * @return the trackerUser
	 */
	public final StringArgument.Options getTrackerUser() {
		return this.trackerUserArg;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	public Tracker init() {
		Tracker tracker = null;
		try {
			final EnumArgument<TrackerType> trackerTypeArgument = getSettings().getArgument(getTrackerType());
			
			final URI trackerUri = getSettings().getArgument(this.trackerURIOptions).getValue();
			final String trackerUser = getSettings().getArgument(getTrackerUser()).getValue();
			final String trackerPassword = getSettings().getArgument(getTrackerPassword()).getValue();
			final Boolean useProxy = getSettings().getArgument(this.useProxyOptions).getValue();
			ProxyConfig proxyConfig = null;
			if (useProxy) {
				proxyConfig = getSettings().getArgumentSet(this.proxyOptions).getValue();
			}
			
			switch (trackerTypeArgument.getValue()) {
				case BUGZILLA:
					tracker = getSettings().getArgumentSet(this.bugzillaOptions).getValue();
					this.bugzillaOptions.setup(trackerUri, trackerUser, trackerPassword, proxyConfig);
					break;
				case JIRA:
					tracker = getSettings().getArgumentSet(this.jiraOptions).getValue();
					this.jiraOptions.setup(trackerUri, trackerUser, trackerPassword, proxyConfig);
					break;
				case MANTIS:
					tracker = getSettings().getArgumentSet(this.mantisOptions).getValue();
					this.mantisOptions.setup(trackerUri, trackerUser, trackerPassword, proxyConfig);
					break;
				case SOURCEFORGE:
					tracker = getSettings().getArgumentSet(this.sourceforgeOptions).getValue();
					this.sourceforgeOptions.setup(trackerUri, trackerUser, trackerPassword, proxyConfig);
					break;
				case GOOGLE:
					tracker = getSettings().getArgumentSet(this.googleOptions).getValue();
					this.googleOptions.setup(trackerUri, trackerUser, trackerPassword, proxyConfig);
					break;
				default:
					throw new UnrecoverableError(String.format("Could not handle %s: %s", trackerTypeArgument.getTag(), //$NON-NLS-1$
					                                           trackerTypeArgument.getValue()));
			}
			return tracker;
		} finally {
			Condition.notNull(tracker, "Tracker must be initilaized.");
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
	@NoneNull
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
			
			this.trackerURIOptions = new URIArgument.Options(set, "uri", //$NON-NLS-1$
			                                                 Messages.getString("TrackerOptions.uri_description"), //$NON-NLS-1$
			                                                 null, Requirement.required);
			req(this.trackerURIOptions, map);
			
			this.trackerTypeArg = new EnumArgument.Options<TrackerType>(
			                                                            set,
			                                                            "type", //$NON-NLS-1$
			                                                            Messages.getString("TrackerOptions.type_description"), //$NON-NLS-1$
			                                                            null, Requirement.required,
			                                                            TrackerType.values());
			req(this.trackerTypeArg, map);
			
			this.trackerUserArg = new StringArgument.Options(
			                                                 set,
			                                                 "user", Messages.getString("TrackerOptions.user_description"), null, //$NON-NLS-1$ //$NON-NLS-2$
			                                                 Requirement.optional);
			req(this.trackerUserArg, map);
			this.trackerPasswordArg = new StringArgument.Options(
			                                                     set,
			                                                     "password", Messages.getString("TrackerOptions.password_description"), null, //$NON-NLS-1$ //$NON-NLS-2$
			                                                     Requirement.iff(this.trackerUserArg), true);
			req(this.trackerPasswordArg, map);
			
			this.useProxyOptions = new BooleanArgument.Options(set, "useProxy", "Activates proxy features.", null,
			                                                   Requirement.required);
			req(this.useProxyOptions, map);
			
			this.proxyOptions = new ProxyOptions(set, Requirement.equals(this.useProxyOptions, true));
			req(this.proxyOptions, map);
			
			// tracker alternatives
			this.bugzillaOptions = new BugzillaOptions(this, Requirement.equals(this.trackerTypeArg,
			                                                                    TrackerType.BUGZILLA));
			if (this.bugzillaOptions.required()) {
				req(this.bugzillaOptions, map);
			}
			
			this.googleOptions = new GoogleOptions(this, Requirement.equals(this.trackerTypeArg, TrackerType.GOOGLE));
			if (this.googleOptions.required()) {
				req(this.googleOptions, map);
			}
			
			this.jiraOptions = new JiraOptions(this, Requirement.equals(this.trackerTypeArg, TrackerType.JIRA));
			if (this.jiraOptions.required()) {
				req(this.jiraOptions, map);
			}
			
			this.mantisOptions = new MantisOptions(this, Requirement.equals(this.trackerTypeArg, TrackerType.MANTIS));
			if (this.mantisOptions.required()) {
				req(this.mantisOptions, map);
			}
			
			this.sourceforgeOptions = new SourceforgeOptions(this, Requirement.equals(this.trackerTypeArg,
			                                                                          TrackerType.SOURCEFORGE));
			if (this.sourceforgeOptions.required()) {
				req(this.sourceforgeOptions, map);
			}
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
}

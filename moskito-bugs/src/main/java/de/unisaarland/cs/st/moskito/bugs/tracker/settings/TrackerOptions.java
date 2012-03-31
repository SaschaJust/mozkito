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
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.TrackerType;

/**
 * The Class TrackerOptions.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TrackerOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, TrackerOptions>> {
	
	/** The tracker uri arg. */
	private URIArgument.Options               trackerURIArg;
	
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
	
	private ProxyOptions                      proxyOptions;
	
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
	 * Gets the tracker uri.
	 * 
	 * @return the trackerFetchURI
	 */
	public final URIArgument.Options getTrackerURI() {
		return this.trackerURIArg;
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
		
		try {
			final EnumArgument<TrackerType> trackerTypeArgument = getSettings().getArgument(getTrackerType());
			
			switch (trackerTypeArgument.getValue()) {
				case BUGZILLA:
					final ArgumentSet<Tracker, BugzillaOptions> bugzillaArgumentSet = ArgumentSetFactory.create(this.bugzillaOptions);
					return bugzillaArgumentSet.getValue();
				case JIRA:
					final ArgumentSet<Tracker, JiraOptions> jiraArgumentSet = ArgumentSetFactory.create(this.jiraOptions);
					return jiraArgumentSet.getValue();
				case MANTIS:
					final ArgumentSet<Tracker, MantisOptions> mantisArgumentSet = ArgumentSetFactory.create(this.mantisOptions);
					return mantisArgumentSet.getValue();
				case SOURCEFORGE:
					final ArgumentSet<Tracker, SourceforgeOptions> sourceforgeArgumentSet = ArgumentSetFactory.create(this.sourceforgeOptions);
					return sourceforgeArgumentSet.getValue();
				case GOOGLE:
					final ArgumentSet<Tracker, GoogleOptions> googleArgumentSet = ArgumentSetFactory.create(this.googleOptions);
					return googleArgumentSet.getValue();
				default:
					throw new UnrecoverableError(
					                             String.format("Could not handle %s: %s", trackerTypeArgument.getTag(), //$NON-NLS-1$
					                                           trackerTypeArgument.getValue()));
			}
		} catch (final SettingsParseError e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentRegistrationException e) {
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
			
			this.trackerURIArg = new URIArgument.Options(set, "uri", //$NON-NLS-1$
			                                             Messages.getString("TrackerOptions.uri_description"), //$NON-NLS-1$
			                                             null, Requirement.required);
			req(this.trackerURIArg, map);
			
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
			
			this.bugzillaOptions = new BugzillaOptions(this, Requirement.equals(this.trackerTypeArg,
			                                                                    TrackerType.BUGZILLA));
			req(this.bugzillaOptions, map);
			
			this.googleOptions = new GoogleOptions(this, Requirement.equals(this.trackerTypeArg, TrackerType.GOOGLE));
			req(this.googleOptions, map);
			
			this.jiraOptions = new JiraOptions(this, Requirement.equals(this.trackerTypeArg, TrackerType.JIRA));
			req(this.jiraOptions, map);
			
			this.mantisOptions = new MantisOptions(this, Requirement.equals(this.trackerTypeArg, TrackerType.MANTIS));
			req(this.jiraOptions, map);
			
			this.sourceforgeOptions = new SourceforgeOptions(this, Requirement.equals(this.trackerTypeArg,
			                                                                          TrackerType.SOURCEFORGE));
			req(this.jiraOptions, map);
			
			this.proxyOptions = new ProxyOptions(set, Requirement.optional);
			req(this.proxyOptions, map);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
}

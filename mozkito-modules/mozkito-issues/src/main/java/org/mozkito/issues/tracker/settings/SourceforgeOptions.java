/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.settings;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.tracker.Tracker;
import org.mozkito.issues.tracker.elements.Type;
import org.mozkito.issues.tracker.model.IssueTracker;
import org.mozkito.issues.tracker.sourceforge.SourceforgeTracker;

/**
 * The Class SourceforgeOptions.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SourceforgeOptions extends ArgumentSetOptions<Boolean, ArgumentSet<Boolean, SourceforgeOptions>> implements
        ITrackerOptions {
	
	/** The at id arg. */
	private LongArgument.Options       atIdArg;
	
	/** The group id arg. */
	private LongArgument.Options       groupIdArg;
	
	/** The bug type arg. */
	private EnumArgument.Options<Type> bugTypeArg;
	
	/** The group id argument. */
	private LongArgument               groupIdArgument;
	
	/** The at id argument. */
	private LongArgument               atIdArgument;
	
	/** The bug type argument. */
	private EnumArgument<Type>         bugTypeArgument;
	
	/**
	 * Instantiates a new sourceforge options.
	 * 
	 * @param trackerOptions
	 *            the tracker options
	 * @param requirement
	 *            the requirement
	 */
	@NoneNull
	public SourceforgeOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "sourceforge",
		      "Necessary arguments to connect and parse sourceforge reports.", requirement);
	}
	
	/**
	 * Gets the at id arg.
	 * 
	 * @return the at id arg
	 */
	public LongArgument.Options getAtIdArg() {
		// PRECONDITIONS
		
		try {
			return this.atIdArg;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the bug type arg.
	 * 
	 * @return the bug type arg
	 */
	public EnumArgument.Options<Type> getBugTypeArg() {
		// PRECONDITIONS
		
		try {
			return this.bugTypeArg;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the group id arg.
	 * 
	 * @return the group id arg
	 */
	public LongArgument.Options getGroupIdArg() {
		// PRECONDITIONS
		
		try {
			return this.groupIdArg;
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
	public Boolean init() {
		// PRECONDITIONS
		
		try {
			
			this.groupIdArgument = getSettings().getArgument(getGroupIdArg());
			this.atIdArgument = getSettings().getArgument(getAtIdArg());
			this.bugTypeArgument = getSettings().getArgument(getBugTypeArg());
			return true;
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
	private void req(final IOptions<?, ?> option,
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
			
			this.atIdArg = new LongArgument.Options(set, "atId", "Sourceforge project tracker's  atID.", null,
			                                        Requirement.required);
			this.groupIdArg = new LongArgument.Options(set, "groupId", "Sourceforge project's groupID.", null,
			                                           Requirement.required);
			
			this.bugTypeArg = new EnumArgument.Options<Type>(
			                                                 set,
			                                                 "bugType",
			                                                 "Specifies the bug type of reports contained within this Sourceforge tracker. Sourceforge has multiple trackers for multiple report types.",
			                                                 Type.BUG, Requirement.required);
			
			req(getGroupIdArg(), map);
			req(getAtIdArg(), map);
			req(getBugTypeArg(), map);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.bugs.tracker.settings.ITrackerOptions#setup(java.net.URI, java.lang.String, java.lang.String,
	 * net.ownhero.dev.ioda.ProxyConfig)
	 */
	@Override
	public Tracker setup(final IssueTracker issueTracker,
	                     final URI trackerUri,
	                     final String trackerUser,
	                     final String trackerPassword) {
		// PRECONDITIONS
		
		try {
			getSettings().getArgumentSet(this).getValue();
			final SourceforgeTracker tracker = new SourceforgeTracker(issueTracker);
			tracker.setup(trackerUri, trackerUser, trackerPassword, this.groupIdArgument.getValue(),
			              this.atIdArgument.getValue(), this.bugTypeArgument.getValue());
			return tracker;
		} catch (final InvalidParameterException e) {
			throw new UnrecoverableError(e);
			
		} finally {
			// POSTCONDITIONS
		}
	}
}

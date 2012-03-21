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
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge.SourceforgeTracker;

/**
 * The Class SourceforgeOptions.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class SourceforgeOptions extends ArgumentSetOptions<Tracker, ArgumentSet<Tracker, SourceforgeOptions>> {
	
	/** The tracker options. */
	private final TrackerOptions       trackerOptions;
	
	/** The at id arg. */
	private LongArgument.Options       atIdArg;
	
	/** The group id arg. */
	private LongArgument.Options       groupIdArg;
	
	/** The bug type arg. */
	private EnumArgument.Options<Type> bugTypeArg;
	
	/**
	 * Instantiates a new sourceforge options.
	 *
	 * @param trackerOptions the tracker options
	 * @param requirement the requirement
	 */
	@NoneNull
	public SourceforgeOptions(final TrackerOptions trackerOptions, final Requirement requirement) {
		super(trackerOptions.getArgumentSet(), "sourceforge",
		      "Necessary arguments to connect and parse sourceforge reports.", requirement);
		
		this.trackerOptions = trackerOptions;
		
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
	public Tracker init() {
		// PRECONDITIONS
		
		try {
			
			final URIArgument trackerURIArgument = (URIArgument) getSettings().getArgument(this.trackerOptions.getTrackerURI()
			                                                                                                  .getTag());
			
			final StringArgument trackerUserArgument = (StringArgument) getSettings().getArgument(this.trackerOptions.getTrackerUser()
			                                                                                                         .getTag());
			final StringArgument trackerPasswordArgument = (StringArgument) getSettings().getArgument(this.trackerOptions.getTrackerPassword()
			                                                                                                             .getTag());
			
			final LongArgument groupIdArgument = (LongArgument) getSettings().getArgument(getGroupIdArg().getTag());
			final LongArgument atIdArgument = (LongArgument) getSettings().getArgument(getAtIdArg().getTag());
			
			@SuppressWarnings ("unchecked")
			final EnumArgument<Type> bugTypeArgument = (EnumArgument<Type>) getSettings().getArgument(getBugTypeArg().getTag());
			
			final SourceforgeTracker tracker = new SourceforgeTracker();
			tracker.setup(trackerURIArgument.getValue(), trackerUserArgument.getValue(),
			              trackerPasswordArgument.getValue(), groupIdArgument.getValue(), atIdArgument.getValue(),
			              bugTypeArgument.getValue());
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
	 * @param option the option
	 * @param map the map
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
}

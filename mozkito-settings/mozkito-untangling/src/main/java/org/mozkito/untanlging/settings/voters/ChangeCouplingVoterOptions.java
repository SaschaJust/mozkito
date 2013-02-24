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

package org.mozkito.untanlging.settings.voters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.untangling.voters.ChangeCouplingVoter;

/**
 * The Class Options.
 */
public class ChangeCouplingVoterOptions
        extends
        ArgumentSetOptions<ChangeCouplingVoter.Factory, ArgumentSet<ChangeCouplingVoter.Factory, ChangeCouplingVoterOptions>> {
	
	/** The min support options. */
	private LongArgument.Options      minSupportOptions;
	
	/** The min confidence options. */
	private DoubleArgument.Options    minConfidenceOptions;
	
	/** The cache dir options. */
	private DirectoryArgument.Options cacheDirOptions;
	
	/** The database options. */
	private final DatabaseOptions     databaseOptions;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 * @param databaseOptions
	 *            the database options
	 */
	public ChangeCouplingVoterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final DatabaseOptions databaseOptions) {
		super(argumentSet, "changeCouplingVoter", "ChangeCouplingVoter options.", requirements);
		this.databaseOptions = databaseOptions;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public ChangeCouplingVoter.Factory init() {
		// PRECONDITIONS
		
		final Long minSupport = getSettings().getArgument(this.minSupportOptions).getValue();
		final Double minConfidence = getSettings().getArgument(this.minConfidenceOptions).getValue();
		final File cacheDir = getSettings().getArgument(this.cacheDirOptions).getValue();
		final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
		
		return new ChangeCouplingVoter.Factory(minSupport.intValue(), minConfidence.doubleValue(), persistenceUtil,
		                                       cacheDir);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		// PRECONDITIONS
		final Map<String, IOptions<?, ?>> map = new HashMap<>();
		map.put(this.databaseOptions.getName(), this.databaseOptions);
		this.minSupportOptions = new LongArgument.Options(
		                                                  argumentSet,
		                                                  "minSupport",
		                                                  "Set the minimum support for used change couplings to this value",
		                                                  3l, Requirement.required);
		map.put(this.minSupportOptions.getName(), this.minSupportOptions);
		
		this.minConfidenceOptions = new DoubleArgument.Options(
		                                                       argumentSet,
		                                                       "minConfidence",
		                                                       "Set minimum confidence for used change couplings to this value",
		                                                       0.7d, Requirement.required);
		map.put(this.minConfidenceOptions.getName(), this.minConfidenceOptions);
		
		this.cacheDirOptions = new DirectoryArgument.Options(
		                                                     argumentSet,
		                                                     "cacheDir",
		                                                     "Cache directory containing change coupling pre-computations using the naming converntion <changeSetId>.cc",
		                                                     null, Requirement.required, false);
		map.put(this.cacheDirOptions.getName(), this.cacheDirOptions);
		
		return map;
	}
}

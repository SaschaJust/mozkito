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

package org.mozkito.untanlging.settings.operators;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.untangling.blob.combine.ChangeCouplingCombineOperator;

/**
 * The Class Options.
 */
public class ChangeCouplingCombineOperatorOptions
        extends
        ArgumentSetOptions<ChangeCouplingCombineOperator, ArgumentSet<ChangeCouplingCombineOperator, ChangeCouplingCombineOperatorOptions>> {
	
	/** The database options. */
	private final DatabaseOptions  databaseOptions;
	
	/** The min support options. */
	private LongArgument.Options   minSupportOptions;
	
	/** The min confidence options. */
	private DoubleArgument.Options minConfidenceOptions;
	
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
	public ChangeCouplingCombineOperatorOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final DatabaseOptions databaseOptions) {
		super(argumentSet, "ccCombineOp", "ChangeCouplingCombineOperator options.", requirements);
		this.databaseOptions = databaseOptions;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public ChangeCouplingCombineOperator init() {
		// PRECONDITIONS
		final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
		final Double minConfidence = getSettings().getArgument(this.minConfidenceOptions).getValue();
		final Long minSupport = getSettings().getArgument(this.minSupportOptions).getValue();
		return new ChangeCouplingCombineOperator(minSupport.intValue(), minConfidence.doubleValue(), persistenceUtil);
	}
	
	/**
	 * {@inheritDoc}
	 * 
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
		                                                  "Minimum support for change couplings used to tangle change sets.",
		                                                  3l, Requirement.required);
		map.put(this.minSupportOptions.getName(), this.minSupportOptions);
		this.minConfidenceOptions = new DoubleArgument.Options(
		                                                       argumentSet,
		                                                       "minConfidence",
		                                                       "Minimum confidence for change couplings used to tangle change sets.",
		                                                       0.5, Requirement.required);
		map.put(this.minConfidenceOptions.getName(), this.minConfidenceOptions);
		return map;
	}
}

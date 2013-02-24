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
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.untangling.blob.combine.PackageDistanceCombineOperator;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class PackageDistanceCombineOperatorOptions
        extends
        ArgumentSetOptions<PackageDistanceCombineOperator, ArgumentSet<PackageDistanceCombineOperator, PackageDistanceCombineOperatorOptions>> {
	
	/** The max package distance options. */
	private LongArgument.Options maxPackageDistanceOptions;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public PackageDistanceCombineOperatorOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "ccCombineOp", "ChangeCouplingCombineOperator options.", requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public PackageDistanceCombineOperator init() {
		// PRECONDITIONS
		final Long maxPackageDistance = getSettings().getArgument(this.maxPackageDistanceOptions).getValue();
		return new PackageDistanceCombineOperator(maxPackageDistance);
		
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
		
		this.maxPackageDistanceOptions = new LongArgument.Options(
		                                                          argumentSet,
		                                                          "maxPackageDistance",
		                                                          "The maximal allowed distance between packages allowed when generating blobs using package distances.",
		                                                          0l, Requirement.required);
		map.put(this.maxPackageDistanceOptions.getName(), this.maxPackageDistanceOptions);
		return map;
	}
}

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
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory;
import org.mozkito.untangling.voters.TestImpactVoter;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.settings.RepositoryOptions;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TestImpactVoterOptions
        extends
        ArgumentSetOptions<TestImpactVoterOptions.Factory, ArgumentSet<TestImpactVoterOptions.Factory, TestImpactVoterOptions>> {
	
	/**
	 * The Class Factory.
	 */
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<TestImpactVoter> {
		
		/** The test coverage in. */
		private final File testCoverageIn;
		
		/**
		 * Instantiates a new factory.
		 * 
		 * @param testCoverageIn
		 *            the test coverage in
		 */
		protected Factory(final File testCoverageIn) {
			this.testCoverageIn = testCoverageIn;
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#createVoter(org.mozkito.versions.model
		 * .ChangeSet)
		 */
		@Override
		public TestImpactVoter createVoter(final ChangeSet changeset) {
			return new TestImpactVoter(this.testCoverageIn);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#getVoterName()
		 */
		@Override
		public String getVoterName() {
			// PRECONDITIONS
			
			try {
				return TestImpactVoter.class.getSimpleName();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The test impact file options. */
	private net.ownhero.dev.hiari.settings.InputFileArgument.Options testImpactFileOptions;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 * @param repositoryOptions
	 *            the repository options
	 */
	public TestImpactVoterOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
	        final RepositoryOptions repositoryOptions) {
		super(argumentSet, "testImpactVoter", "TestImpactVoter options.", requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public Factory init() {
		// PRECONDITIONS
		final File testCoverageIn = getSettings().getArgument(this.testImpactFileOptions).getValue();
		return new Factory(testCoverageIn);
		
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
		this.testImpactFileOptions = new InputFileArgument.Options(
		                                                           argumentSet,
		                                                           "testImpactIn",
		                                                           "File containing a serial version of a ImpactMatrix",
		                                                           null, Requirement.required);
		map.put(this.testImpactFileOptions.getName(), this.testImpactFileOptions);
		return map;
	}
	
}

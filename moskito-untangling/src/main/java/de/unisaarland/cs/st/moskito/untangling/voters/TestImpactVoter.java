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

package de.unisaarland.cs.st.moskito.untangling.voters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.changeimpact.ImpactMatrix;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.RepositoryOptions;

/**
 * The Class TestImpactVoter.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class TestImpactVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<TestImpactVoter> {
		
		private final File testCoverageIn;
		
		protected Factory(final File testCoverageIn) {
			this.testCoverageIn = testCoverageIn;
		}
		
		@Override
		public TestImpactVoter createVoter(final RCSTransaction transaction) {
			return new TestImpactVoter(this.testCoverageIn);
		}
		
	}
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<TestImpactVoter.Factory, ArgumentSet<TestImpactVoter.Factory, Options>> {
		
		private net.ownhero.dev.hiari.settings.InputFileArgument.Options testImpactFileOptions;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
		        final RepositoryOptions repositoryOptions) {
			super(argumentSet, "testImpactVoter", "TestImpactVoter options.", requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public TestImpactVoter.Factory init() {
			// PRECONDITIONS
			final File testCoverageIn = getSettings().getArgument(this.testImpactFileOptions).getValue();
			return new TestImpactVoter.Factory(testCoverageIn);
			
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
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
	
	/** The matrix. */
	private ImpactMatrix matrix = null;
	
	/**
	 * Instantiates a new test impact voter.
	 * 
	 * @param testCoverageIn
	 *            the test coverage in
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public TestImpactVoter(final File testCoverageIn) {
		try (final ObjectInputStream in = new ObjectInputStream(new FileInputStream(testCoverageIn));) {
			this.matrix = (ImpactMatrix) in.readObject();
		} catch (final IOException | ClassNotFoundException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor#getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor#getScore(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op0,
	                       final JavaChangeOperation op1) {
		
		final String name0 = op0.getChangedElementLocation().getElement().getFullQualifiedName();
		final String name1 = op1.getChangedElementLocation().getElement().getFullQualifiedName();
		
		final double occ0 = this.matrix.getOccurence(name0, name1);
		final double occ1 = this.matrix.getOccurence(name1, name0);
		final double sum0 = this.matrix.getSumChanged(name0);
		final double sum1 = this.matrix.getSumChanged(name1);
		
		if ((occ0 == 0d) || (occ1 == 0d) || (sum0 == 0d) || (sum1 == 0d)) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		return Math.max((occ0 / sum0), (occ1 / sum1));
	}
	
}

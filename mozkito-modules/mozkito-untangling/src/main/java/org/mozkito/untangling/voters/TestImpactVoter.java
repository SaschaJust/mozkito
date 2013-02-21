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

package org.mozkito.untangling.voters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.changeimpact.ImpactMatrix;
import org.mozkito.clustering.MultilevelClustering;
import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;

/**
 * The Class TestImpactVoter.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TestImpactVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The matrix. */
	private ImpactMatrix matrix = null;
	
	/**
	 * Instantiates a new test impact voter.
	 * 
	 * @param testCoverageIn
	 *            the test coverage in
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
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor#close()
	 */
	@Override
	public void close() {
		return;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor#getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor#getScore(java.lang.Object, java.lang.Object)
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

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

package de.unisaarland.cs.st.moskito.untangling.voters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import de.unisaarland.cs.st.moskito.changeimpact.ImpactMatrix;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class TestImpactVoter.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class TestImpactVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The matrix. */
	private ImpactMatrix matrix = null;
	
	/**
	 * Instantiates a new test impact voter.
	 *
	 * @param testCoverageIn the test coverage in
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public TestImpactVoter(final File testCoverageIn) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(testCoverageIn));
		matrix = (ImpactMatrix) in.readObject();
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor#getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor#getScore(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op0,
	                       final JavaChangeOperation op1) {
		
		String name0 = op0.getChangedElementLocation().getElement().getFullQualifiedName();
		String name1 = op1.getChangedElementLocation().getElement().getFullQualifiedName();
		
		double occ0 = matrix.getOccurence(name0, name1);
		double occ1 = matrix.getOccurence(name1, name0);
		double sum0 = matrix.getSumChanged(name0);
		double sum1 = matrix.getSumChanged(name1);
		
		if ((occ0 == 0d) || (occ1 == 0d) || (sum0 == 0d) || (sum1 == 0d)) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		return Math.max((occ0 / sum0), (occ1 / sum1));
	}
	
}

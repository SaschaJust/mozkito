package de.unisaarland.cs.st.reposuite.untangling.voters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import de.unisaarland.cs.st.reposuite.changeimpact.ImpactMatrix;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

public class TestImpactVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	private ImpactMatrix                        matrix              = null;
	
	public TestImpactVoter(final File testCoverageIn) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(testCoverageIn));
		matrix = (ImpactMatrix) in.readObject();
	}
	
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	@Override
	public double getScore(final JavaChangeOperation op0, final JavaChangeOperation op1) {
		
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

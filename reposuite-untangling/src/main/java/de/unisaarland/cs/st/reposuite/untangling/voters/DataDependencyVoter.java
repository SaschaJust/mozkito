package de.unisaarland.cs.st.reposuite.untangling.voters;

import java.io.File;

import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 */
public class DataDependencyVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	
	public DataDependencyVoter(final File eclipseDir, final String[] eclipseArguments, final RCSTransaction transaction) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor
	 * #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor
	 * #getScore(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		String filePath1 = op1.getChangedElementLocation().getFilePath();
		String filePath2 = op2.getChangedElementLocation().getFilePath();
		
		if (!filePath1.equals(filePath2)) {
			return 0;
		}
		
		// FIXME implement this
		return 1;
	}
}

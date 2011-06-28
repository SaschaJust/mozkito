package de.unisaarland.cs.st.reposuite.untangling.voters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

public class TestCoverageVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	private final HashMap<Integer, Set<String>> coverageDifferences = new HashMap<Integer, Set<String>>();
	
	public TestCoverageVoter(final File testCoverageIn) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(testCoverageIn));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split(",");
			int revId = Integer.valueOf(lineParts[0]);
			if (Integer.valueOf(lineParts[2]) > 0) {
				String diffString = lineParts[3].trim();
				diffString = diffString.substring(1, diffString.length() - 1);
				if (!coverageDifferences.containsKey(revId)) {
					coverageDifferences.put(revId, new HashSet<String>());
				}
				for (String s : diffString.split(",")) {
					String methodName = s.trim().replace("@", ".");
					coverageDifferences.get(revId).add(methodName);
				}
			}
		}
	}
	
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	@Override
	public double getScore(final JavaChangeOperation op1, final JavaChangeOperation op2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

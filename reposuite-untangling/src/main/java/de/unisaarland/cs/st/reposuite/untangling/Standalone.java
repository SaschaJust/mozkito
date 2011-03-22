package de.unisaarland.cs.st.reposuite.untangling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;

public class Standalone {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	@NoneNull
	public static Set<Set<JavaChangeOperation>> untangle(final Set<JavaChangeOperation> blob,
	                                                     int numClusters) {
		Set<Set<JavaChangeOperation>> result = new HashSet<Set<JavaChangeOperation>>();
		
		List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<JavaChangeOperation>>();
		MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor = null;
		
		
		
		MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(blob, scoreVisitors, collapseVisitor);
		
		return clustering.getPartitions(numClusters);
	}
	
}

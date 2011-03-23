package de.unisaarland.cs.st.reposuite.untangling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringCollapseVisitor;
import de.unisaarland.cs.st.reposuite.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

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
		@SuppressWarnings ("unused")
		Set<Set<JavaChangeOperation>> result = new HashSet<Set<JavaChangeOperation>>();
		
		List<MultilevelClusteringScoreVisitor<JavaChangeOperation>> scoreVisitors = new LinkedList<MultilevelClusteringScoreVisitor<JavaChangeOperation>>();
		MultilevelClusteringCollapseVisitor<JavaChangeOperation> collapseVisitor = null;
		
		
		
		MultilevelClustering<JavaChangeOperation> clustering = new MultilevelClustering<JavaChangeOperation>(blob, scoreVisitors, collapseVisitor);
		
		return clustering.getPartitions(numClusters);
	}
	
}

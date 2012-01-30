/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.voters;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 */
public class FileDistanceVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		String path1 = op1.getChangedPath();
		String path2 = op2.getChangedPath();
		
		if (path1.equals(path2)) {
			return 1;
		}
		
		// [0,0.5[
		List<String> path1Segments = Arrays.asList(path1.split("/"));
		List<String> path2Segments = Arrays.asList(path2.split("/"));
		int pathDistance = Math.max(CollectionUtils.subtract(path1Segments, path2Segments).size(),
		                            CollectionUtils.subtract(path2Segments, path1Segments).size());
		return 1 - (((double) pathDistance) / (Math.max(path1Segments.size(), path2Segments.size())));
		
	}
}

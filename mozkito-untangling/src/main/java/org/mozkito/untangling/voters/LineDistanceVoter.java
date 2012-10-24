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
package org.mozkito.untangling.voters;

import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaElementLocation.LineCover;


/**
 * The Class CallGraphHandler.
 * 
 * Works only for JavaMethodDefinitions so far.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class LineDistanceVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
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
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public double getScore(final JavaChangeOperation op1,
	                       final JavaChangeOperation op2) {
		
		final String path1 = op1.getChangedPath();
		final String path2 = op2.getChangedPath();
		
		if (!path1.equals(path2)) {
			return 0;
		}
		
		final JavaElementLocation location1 = op1.getChangedElementLocation();
		final JavaElementLocation location2 = op2.getChangedElementLocation();
		
		// check if one location is covered by the other one => 1
		if ((!location1.coversLine(location2.getStartLine()).equals(LineCover.FALSE))
		        || (!location2.coversLine(location1.getStartLine()).equals(LineCover.FALSE))) {
			return 1;
		}
		
		final double lineDistance = Math.abs(location1.getStartLine() - location2.getStartLine()) + 0.1;
		return 1 / lineDistance;
		
	}
}

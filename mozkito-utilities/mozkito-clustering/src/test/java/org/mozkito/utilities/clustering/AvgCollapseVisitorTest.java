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
package org.mozkito.utilities.clustering;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * The Class AvgCollapseVisitorTest.
 */
public class AvgCollapseVisitorTest {
	
	/**
	 * Test.
	 */
	@Test
	public void test() {
		// final ,
		// final Cluster<T> otherCluster,
		/*
		 * @formatter:off 
		 *   a b c 
		 * a   1 2 
		 * b 3   4 
		 * c 5 6
		 */
		final Map<String, Map<String, Double>> originalScoreMatrix = new HashMap<>();
		final Map<String, Double> tmp = new HashMap<>();
		tmp.put("b", 1d);
		tmp.put("c", 2d);
		originalScoreMatrix.put("a", tmp);
		final HashMap<String, Double> tmp2 = new HashMap<>();
		tmp2.put("a", 3d);
		tmp2.put("c", 4d);
		originalScoreMatrix.put("b", tmp2);
		final HashMap<String, Double> tmp3 = new HashMap<>();
		tmp3.put("a", 5d);
		tmp3.put("b", 6d);
		originalScoreMatrix.put("c", tmp3);
		
		final Cluster<String> a = new VirtualCluster<>("a");
		final Cluster<String> b = new VirtualCluster<>("b");
		final Cluster<String> c = new VirtualCluster<>("c");
		
		final AvgCollapseVisitor<String> visitor = new AvgCollapseVisitor<>();
		final double score = visitor.getScore(new Cluster<String>(b,c,1), a, originalScoreMatrix);
		assertEquals(4,score,0);
	}
}

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
 * The Class MinCollapseVisitorTest.
 */
public class MinCollapseVisitorTest {
	
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
		 * a   0 1 
		 * b 2   8 
		 * c 0 2
		 */
		final Map<String, Map<String, Double>> originalScoreMatrix = new HashMap<>();
		final Map<String, Double> tmp = new HashMap<>();
		tmp.put("b", 0d);
		tmp.put("c", 1d);
		originalScoreMatrix.put("a", tmp);
		final HashMap<String, Double> tmp2 = new HashMap<>();
		tmp2.put("a", 2d);
		tmp2.put("c", 8d);
		originalScoreMatrix.put("b", tmp2);
		final HashMap<String, Double> tmp3 = new HashMap<>();
		tmp3.put("a", 0d);
		tmp3.put("b", 2d);
		originalScoreMatrix.put("c", tmp3);
		
		final Cluster<String> a = new VirtualCluster<>("a");
		final Cluster<String> b = new VirtualCluster<>("b");
		final Cluster<String> c = new VirtualCluster<>("c");
		
		final MinCollapseVisitor<String> visitor = new MinCollapseVisitor<>();
		final double score = visitor.getScore(new Cluster<String>(b,c,1), a, originalScoreMatrix);
		assertEquals(0,score,0);
	}
}

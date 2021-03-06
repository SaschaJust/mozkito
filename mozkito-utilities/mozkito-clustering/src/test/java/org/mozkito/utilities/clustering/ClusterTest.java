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
package org.mozkito.utilities.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

/**
 * The Class ClusterTest.
 */
public class ClusterTest {
	
	/**
	 * Test get all elements.
	 */
	@Test
	public void testGetAllElements() {
		
		final Cluster<Integer> three = new VirtualCluster<Integer>(3);
		final Cluster<Integer> four = new VirtualCluster<Integer>(4);
		final Cluster<Integer> five = new VirtualCluster<Integer>(5);
		
		final Cluster<Integer> c1 = new BaseCluster<Integer>(1, 2, 0);
		final Cluster<Integer> c2 = new Cluster<Integer>(three, five, 0);
		final Cluster<Integer> c3 = new Cluster<Integer>(c1, four, 0);
		
		final Cluster<Integer> topCluster = new Cluster<Integer>(c3, c2, 0);
		
		final Set<Integer> allElements = topCluster.getAllElements();
		assertEquals(5, allElements.size());
		assertTrue(allElements.contains(1));
		assertTrue(allElements.contains(2));
		assertTrue(allElements.contains(3));
		assertTrue(allElements.contains(4));
		assertTrue(allElements.contains(5));
	}
	
}

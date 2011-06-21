/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;



public class ClusterTest {
	
	@Test
	public void testGetAllElements(){
		
		
		Cluster<Integer> three = new VirtualCluster<Integer>(3);
		Cluster<Integer> four = new VirtualCluster<Integer>(4);
		Cluster<Integer> five = new VirtualCluster<Integer>(5);
		
		Cluster<Integer> c1 = new BaseCluster<Integer>(1,2,0);
		Cluster<Integer> c2 = new Cluster<Integer>(three,five,0);
		Cluster<Integer> c3 = new Cluster<Integer>(c1,four,0);
		
		Cluster<Integer> topCluster = new Cluster<Integer>(c3, c2, 0);
		
		Set<Integer> allElements = topCluster.getAllElements();
		assertEquals(5, allElements.size());
		assertTrue(allElements.contains(1));
		assertTrue(allElements.contains(2));
		assertTrue(allElements.contains(3));
		assertTrue(allElements.contains(4));
		assertTrue(allElements.contains(5));
	}
	
}

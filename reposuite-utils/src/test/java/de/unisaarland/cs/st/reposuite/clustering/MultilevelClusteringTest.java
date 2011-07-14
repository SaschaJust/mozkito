/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.clustering.MultilevelClustering.ComparableTuple;

public class MultilevelClusteringTest {
	
	private class StringScoreVisitor implements MultilevelClusteringScoreVisitor<String>,
	MultilevelClusteringCollapseVisitor<String> {
		
		public int getLevenshteinDistance(final String s,
				final String t) {
			if ((s == null) || (t == null)) {
				throw new IllegalArgumentException("Strings must not be null");
			}
			
			/*
			 * The difference between this impl. and the previous is that,
			 * rather than creating and retaining a matrix of size s.length()+1
			 * by t.length()+1, we maintain two single-dimensional arrays of
			 * length s.length()+1. The first, d, is the 'current working'
			 * distance array that maintains the newest distance cost counts as
			 * we iterate through the characters of String s. Each time we
			 * increment the index of String t we are comparing, d is copied to
			 * p, the second int[]. Doing so allows us to retain the previous
			 * cost counts as required by the algorithm (taking the minimum of
			 * the cost count to the left, up one, and diagonally up and to the
			 * left of the current cost count being calculated). (Note that the
			 * arrays aren't really copied anymore, just switched...this is
			 * clearly much better than cloning an array or doing a
			 * System.arraycopy() each time through the outer loop.)
			 * Effectively, the difference between the two implementations is
			 * this one does not cause an out of memory condition when
			 * calculating the LD over two very large strings.
			 */
			
			int n = s.length(); // length of s
			int m = t.length(); // length of t
			
			if (n == 0) {
				return m;
			} else if (m == 0) {
				return n;
			}
			
			int p[] = new int[n + 1]; // 'previous' cost array, horizontally
			int d[] = new int[n + 1]; // cost array, horizontally
			int _d[]; // placeholder to assist in swapping p and d
			
			// indexes into strings s and t
			int i; // iterates through s
			int j; // iterates through t
			
			char t_j; // jth character of t
			
			int cost; // cost
			
			for (i = 0; i <= n; i++) {
				p[i] = i;
			}
			
			for (j = 1; j <= m; j++) {
				t_j = t.charAt(j - 1);
				d[0] = j;
				
				for (i = 1; i <= n; i++) {
					cost = s.charAt(i - 1) == t_j
							? 0
									: 1;
					// minimum of cell to the left+1, to the top+1, diagonally
					// left and up +cost
					d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
				}
				
				// copy current distance counts to 'previous row' distance
				// counts
				_d = p;
				p = d;
				d = _d;
			}
			
			// our last action in the above loop was to switch d and p, so p now
			// actually has the most recent cost counts
			return p[n];
		}
		
		@Override
		public double getMaxPossibleScore() {
			return 0;
		}
		
		@Override
		public double getScore(final Cluster<String> newCluster,
				final Cluster<String> otherCluster,
				final Map<String, Map<String, Double>> originalScoreMatrix) {
			double d = 1000d;
			for (String s : newCluster.getAllElements()) {
				for (String t : otherCluster.getAllElements()) {
					double tmp = getLevenshteinDistance(s, t);
					if (tmp < d) {
						d = tmp;
					}
				}
			}
			if (d == 0) {
				return 2;
			} else {
				return 1d / d;
			}
		}
		
		@Override
		public double getScore(final String t1,
				final String t2) {
			
			if (t1.equals(t2)) {
				return 2d;
			} else {
				double d = getLevenshteinDistance(t1, t2);
				return 1d / d;
			}
			
		}
	}
	
	private class TestScoreVisitor implements MultilevelClusteringScoreVisitor<Integer>,
	MultilevelClusteringCollapseVisitor<Integer> {
		
		private int counter = 0;
		
		@Override
		public double getMaxPossibleScore() {
			return 0;
		}
		
		@Override
		public double getScore(final Cluster<Integer> newCluster,
				final Cluster<Integer> otherCluster,
				final Map<Integer, Map<Integer, Double>> originalScoreMatrix) {
			return (++counter);
		}
		
		@Override
		public double getScore(final Integer t1,
				final Integer t2) {
			return (++counter);
		}
		
	}
	
	private TestScoreVisitor visitor;
	
	@Test
	public void comparatorTest() {
		ComparableTuple<Double, Integer> t1 = new ComparableTuple<Double, Integer>(1d, 1);
		ComparableTuple<Double, Integer> t2 = new ComparableTuple<Double, Integer>(2d, 0);
		
		assert (t1.compareTo(t2) > 0);
		
	}
	
	@Before
	public void setUp() {
		visitor = new TestScoreVisitor();
	}
	
	@Test
	public void simpleTest() {
		Integer[] nodes = { 1, 2, 3, 4, 5, 6 };
		List<MultilevelClusteringScoreVisitor<Integer>> l = new ArrayList<MultilevelClusteringScoreVisitor<Integer>>(1);
		l.add(visitor);
		SumAggregation<Integer> aggregator = new SumAggregation<Integer>(l);
		MultilevelClustering<Integer> mp = new MultilevelClustering<Integer>(nodes, aggregator, visitor);
		
		Set<Set<Integer>> partitions = mp.getPartitions(3);
		assertEquals(3, partitions.size());
		
		int oneCount = 0;
		int threeCount = 0;
		
		for (Set<Integer> set : partitions) {
			if (set.size() == 1) {
				++oneCount;
			} else if (set.size() == 4) {
				++threeCount;
			} else {
				fail();
			}
		}
		
		assertEquals(2, oneCount);
		assertEquals(1, threeCount);
		
	}
	
	@Test
	public void stringTest() {
		
		StringScoreVisitor visitor = new StringScoreVisitor();
		
		String[] nodes = { "hallo", "hubba", "wurstsalat", "halli", "hubbo", "hullo", "habbo" };
		List<MultilevelClusteringScoreVisitor<String>> l = new ArrayList<MultilevelClusteringScoreVisitor<String>>(1);
		l.add(visitor);
		SumAggregation<String> aggregator = new SumAggregation<String>(l);
		MultilevelClustering<String> mp = new MultilevelClustering<String>(nodes, aggregator, visitor);
		
		Set<Set<String>> clusters = mp.getPartitions(3);
		assertEquals(3, clusters.size());
		
		Set<String> s1 = new HashSet<String>();
		s1.add("hallo");
		s1.add("halli");
		s1.add("hullo");
		
		Set<String> s2 = new HashSet<String>();
		s2.add("hubba");
		s2.add("hubbo");
		s2.add("habbo");
		
		Set<String> s3 = new HashSet<String>();
		s3.add("wurstsalat");
		
		assertTrue(clusters.contains(s1));
		assertTrue(clusters.contains(s2));
		assertTrue(clusters.contains(s3));
		
	}
	
	@After
	public void tearDown() {
		
	}
	
}

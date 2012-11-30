package org.mozkito.clustering;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * The Class MaxCollapseVisitorTest.
 */
public class MaxCollapseVisitorTest {
	
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
		
		final MaxCollapseVisitor<String> visitor = new MaxCollapseVisitor<>();
		final double score = visitor.getScore(new Cluster<String>(b,c,1), a, originalScoreMatrix);
		assertEquals(2,score,0);
	}
}

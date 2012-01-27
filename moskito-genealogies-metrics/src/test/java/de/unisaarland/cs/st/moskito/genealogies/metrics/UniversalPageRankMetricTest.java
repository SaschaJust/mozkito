package de.unisaarland.cs.st.moskito.genealogies.metrics;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalPageRankMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTestPageRankMetric;

public class UniversalPageRankMetricTest extends TestEnvironment {
	
	@Test
	public void test() {
		setup();
		final UniversalTestPageRankMetric metric = new UniversalTestPageRankMetric(genealogy);
		final Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		final Iterator<String> itemIter = genealogy.vertexSet().iterator();
		while (itemIter.hasNext()) {
			final String item = itemIter.next();
			metricValues.addAll(metric.handle(item, !itemIter.hasNext()));
		}
		
		assertEquals(14, metricValues.size());
		
		for (final GenealogyMetricValue mValue : metricValues) {
			assertEquals(UniversalPageRankMetric.getPagerank(), mValue.getMetricId());
			if (mValue.getNodeId().equals("1")) {
				assertEquals(.1, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("2")) {
				assertEquals(0.1064, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("3")) {
				assertEquals(0.1064, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("4")) {
				assertEquals(0.3512, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("5")) {
				assertEquals(0.3011, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("6")) {
				assertEquals(0.3312, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("7")) {
				assertEquals(0.2022, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("8")) {
				assertEquals(0.3409, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("9")) {
				assertEquals(0.2491, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("10")) {
				assertEquals(0.2491, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("11")) {
				assertEquals(0.4776, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("12")) {
				assertEquals(0.3242, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("13")) {
				assertEquals(0.3242, mValue.getValue(), 0.0001);
			} else if (mValue.getNodeId().equals("14")) {
				assertEquals(0.8983, mValue.getValue(), 0.0001);
			}
		}
		
		genealogy.close();
	}
	
}

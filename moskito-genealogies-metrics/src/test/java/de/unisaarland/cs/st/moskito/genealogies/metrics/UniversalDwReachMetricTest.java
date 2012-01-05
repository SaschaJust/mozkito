package de.unisaarland.cs.st.moskito.genealogies.metrics;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTestDwReachMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;

public class UniversalDwReachMetricTest extends TestEnvironment {
	
	@Test
	public void test() {
		setup();
		UniversalTestDwReachMetric metric = new UniversalTestDwReachMetric(genealogy);
		Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		Iterator<String> itemIter = genealogy.vertexSet();
		while (itemIter.hasNext()) {
			String item = itemIter.next();
			GenealogyNode<String> node = new GenealogyNode<String>(item, item);
			metricValues.addAll(metric.handle(node));
		}
		
		assertEquals(14, metricValues.size());
		
		for (GenealogyMetricValue mValue : metricValues) {
			assertEquals(UniversalDwReachMetric.dwReach, mValue.getMetricId());
			if (mValue.getNodeId().equals("1")) {
				assertEquals(0, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("2")) {
				assertEquals(1, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("3")) {
				assertEquals(1, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("4")) {
				assertEquals(4, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("5")) {
				assertEquals(3.3, mValue.getValue(), 0.1);
			} else if (mValue.getNodeId().equals("6")) {
				assertEquals(3.5, mValue.getValue(), 0.1);
			} else if (mValue.getNodeId().equals("7")) {
				assertEquals(2, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("8")) {
				assertEquals(3.83, mValue.getValue(), 0.01);
			} else if (mValue.getNodeId().equals("9")) {
				assertEquals(3, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("10")) {
				assertEquals(3, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("11")) {
				assertEquals(4.5, mValue.getValue(), 0);
			} else if (mValue.getNodeId().equals("12")) {
				assertEquals(2.916, mValue.getValue(), 0.001);
			} else if (mValue.getNodeId().equals("13")) {
				assertEquals(2.916, mValue.getValue(), 0.001);
			} else if (mValue.getNodeId().equals("14")) {
				assertEquals(6.25, mValue.getValue(), 0);
			}
		}
		
		genealogy.close();
	}
	
}

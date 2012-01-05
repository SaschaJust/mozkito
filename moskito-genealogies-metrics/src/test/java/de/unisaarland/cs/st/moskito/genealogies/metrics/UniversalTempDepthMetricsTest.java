package de.unisaarland.cs.st.moskito.genealogies.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTempDepthMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTestTempDepthMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;

public class UniversalTempDepthMetricsTest extends TestEnvironment {
	
	@Test
	public void test() {
		setup();
		UniversalTestTempDepthMetric metric = new UniversalTestTempDepthMetric(genealogy);
		Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		Iterator<String> itemIter = genealogy.vertexSet();
		while (itemIter.hasNext()) {
			String item = itemIter.next();
			GenealogyNode<String> node = new GenealogyNode<String>(item, item);
			metricValues.addAll(metric.handle(node));
		}
		
		assertEquals(140, metricValues.size());
		
		for (GenealogyMetricValue mValue : metricValues) {
			if (mValue.getMetricId().equals(UniversalTempDepthMetrics.maxTempDepth1)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(1, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.maxTempDepth2)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(3, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(3, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(1, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.maxTempDepth5)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(3, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(2, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.maxTempDepth10)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(5, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.maxTempDepth14)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(7, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.numTempResponses1)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(1, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.numTempResponses2)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(3, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(2, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.numTempResponses5)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(4, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(3, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(3, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(5, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.numTempResponses10)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(8, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(9, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.numTempResponses14)) {
				if (mValue.getNodeId().equals("1")) {
					assertEquals(0, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("2")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("3")) {
					assertEquals(1, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("4")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("5")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("6")) {
					assertEquals(5, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("7")) {
					assertEquals(2, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("8")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("9")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("10")) {
					assertEquals(6, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("11")) {
					assertEquals(8, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("12")) {
					assertEquals(7, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("13")) {
					assertEquals(7, mValue.getValue(), 0);
				} else if (mValue.getNodeId().equals("14")) {
					assertEquals(12, mValue.getValue(), 0);
				}
			} else {
				fail();
			}
		}
		
		genealogy.close();
	}
	
}

/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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


package de.unisaarland.cs.st.moskito.genealogies.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTempDepthMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTestTempDepthMetric;

public class UniversalTempDepthMetricsTest extends TestEnvironment {
	
	@Test
	public void test() {
		setup();
		final UniversalTestTempDepthMetric metric = new UniversalTestTempDepthMetric(genealogy);
		final Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		for (final String item : genealogy.vertexSet()) {
			metricValues.addAll(metric.handle(item));
		}
		
		assertEquals(140, metricValues.size());
		
		for (final GenealogyMetricValue mValue : metricValues) {
			if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth1())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth2())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth5())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth10())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth14())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses1())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses2())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses5())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses10())) {
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
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses14())) {
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

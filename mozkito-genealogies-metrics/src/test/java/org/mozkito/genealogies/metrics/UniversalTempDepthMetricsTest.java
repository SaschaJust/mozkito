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

package org.mozkito.genealogies.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mozkito.genealogies.metrics.layer.universal.UniversalTempDepthMetrics;
import org.mozkito.genealogies.metrics.layer.universal.UniversalTestTempDepthMetric;

/**
 * The Class UniversalTempDepthMetricsTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalTempDepthMetricsTest extends TestEnvironment {
	
	/**
	 * Test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws IOException {
		setup();
		final UniversalTestTempDepthMetric metric = new UniversalTestTempDepthMetric(TestEnvironment.genealogy);
		final Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		for (final String item : TestEnvironment.genealogy.vertexSet()) {
			metricValues.addAll(metric.handle(item));
		}
		
		assertEquals(140, metricValues.size());
		
		for (final GenealogyMetricValue mValue : metricValues) {
			if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth1())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth2())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(3, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(3, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth5())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(3, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth10())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getMaxtempdepth14())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(7, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses1())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses2())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(3, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses5())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(4, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(3, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(3, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses10())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(8, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(9, mValue.getValue(), 0);
				}
			} else if (mValue.getMetricId().equals(UniversalTempDepthMetrics.getNumtempresponses14())) {
				if ("1".equals(mValue.getNodeId())) {
					assertEquals(0, mValue.getValue(), 0);
				} else if ("2".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("3".equals(mValue.getNodeId())) {
					assertEquals(1, mValue.getValue(), 0);
				} else if ("4".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("5".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("6".equals(mValue.getNodeId())) {
					assertEquals(5, mValue.getValue(), 0);
				} else if ("7".equals(mValue.getNodeId())) {
					assertEquals(2, mValue.getValue(), 0);
				} else if ("8".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("9".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("10".equals(mValue.getNodeId())) {
					assertEquals(6, mValue.getValue(), 0);
				} else if ("11".equals(mValue.getNodeId())) {
					assertEquals(8, mValue.getValue(), 0);
				} else if ("12".equals(mValue.getNodeId())) {
					assertEquals(7, mValue.getValue(), 0);
				} else if ("13".equals(mValue.getNodeId())) {
					assertEquals(7, mValue.getValue(), 0);
				} else if ("14".equals(mValue.getNodeId())) {
					assertEquals(12, mValue.getValue(), 0);
				}
			} else {
				fail();
			}
		}
		
		TestEnvironment.genealogy.close();
	}
	
}

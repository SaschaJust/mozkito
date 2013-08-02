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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mozkito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import org.mozkito.genealogies.metrics.layer.universal.UniversalTestDwReachMetric;

/**
 * The Class UniversalDwReachMetricTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalDwReachMetricTest extends TestEnvironment {
	
	/**
	 * Test.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void test() throws IOException {
		setup();
		final UniversalTestDwReachMetric metric = new UniversalTestDwReachMetric(TestEnvironment.genealogy);
		final Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		for (final String item : TestEnvironment.genealogy.vertexSet()) {
			metricValues.addAll(metric.handle(item));
		}
		
		assertEquals(14, metricValues.size());
		
		for (final GenealogyMetricValue mValue : metricValues) {
			assertEquals(UniversalDwReachMetric.getDwreach(), mValue.getMetricId());
			if ("1".equals(mValue.getNodeId())) {
				assertEquals(0, mValue.getValue(), 0);
			} else if ("2".equals(mValue.getNodeId())) {
				assertEquals(1, mValue.getValue(), 0);
			} else if ("3".equals(mValue.getNodeId())) {
				assertEquals(1, mValue.getValue(), 0);
			} else if ("4".equals(mValue.getNodeId())) {
				assertEquals(4, mValue.getValue(), 0);
			} else if ("5".equals(mValue.getNodeId())) {
				assertEquals(3.3, mValue.getValue(), 0.1);
			} else if ("6".equals(mValue.getNodeId())) {
				assertEquals(3.5, mValue.getValue(), 0.1);
			} else if ("7".equals(mValue.getNodeId())) {
				assertEquals(2, mValue.getValue(), 0);
			} else if ("8".equals(mValue.getNodeId())) {
				assertEquals(3.83, mValue.getValue(), 0.01);
			} else if ("9".equals(mValue.getNodeId())) {
				assertEquals(3, mValue.getValue(), 0);
			} else if ("10".equals(mValue.getNodeId())) {
				assertEquals(3, mValue.getValue(), 0);
			} else if ("11".equals(mValue.getNodeId())) {
				assertEquals(4.5, mValue.getValue(), 0);
			} else if ("12".equals(mValue.getNodeId())) {
				assertEquals(2.916, mValue.getValue(), 0.001);
			} else if ("13".equals(mValue.getNodeId())) {
				assertEquals(2.916, mValue.getValue(), 0.001);
			} else if ("14".equals(mValue.getNodeId())) {
				assertEquals(6.25, mValue.getValue(), 0);
			}
		}
		
		TestEnvironment.genealogy.close();
	}
	
}

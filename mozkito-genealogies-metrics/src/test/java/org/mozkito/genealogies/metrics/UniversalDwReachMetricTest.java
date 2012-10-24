/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.mozkito.genealogies.metrics;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import org.mozkito.genealogies.metrics.layer.universal.UniversalTestDwReachMetric;


/**
 * The Class UniversalDwReachMetricTest.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalDwReachMetricTest extends TestEnvironment {
	
	/**
	 * Test.
	 */
	@Test
	public void test() {
		setup();
		final UniversalTestDwReachMetric metric = new UniversalTestDwReachMetric(genealogy);
		final Collection<GenealogyMetricValue> metricValues = new LinkedList<GenealogyMetricValue>();
		
		for (final String item : genealogy.vertexSet()) {
			metricValues.addAll(metric.handle(item));
		}
		
		assertEquals(14, metricValues.size());
		
		for (final GenealogyMetricValue mValue : metricValues) {
			assertEquals(UniversalDwReachMetric.getDwreach(), mValue.getMetricId());
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

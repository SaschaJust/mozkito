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

package org.mozkito.genealogies.metrics.layer.universal;

import java.util.Collection;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.metrics.DayTimeDiff;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalTestTempDepthMetric.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalTestTempDepthMetric implements DayTimeDiff<String> {
	
	/** The universal metric. */
	private final UniversalTempDepthMetrics<String> universalMetric;
	
	/**
	 * Instantiates a new universal test temp depth metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalTestTempDepthMetric(final ChangeGenealogy<String> genealogy) {
		this.universalMetric = new UniversalTempDepthMetrics<String>(genealogy, this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.DayTimeDiff#daysDiff(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int daysDiff(final String t1,
	                    final String t2) {
		final int i1 = Integer.valueOf(t1).intValue();
		final int i2 = Integer.valueOf(t2).intValue();
		return Math.abs(i1 - i2);
	}
	
	/**
	 * Handle.
	 * 
	 * @param item
	 *            the item
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final String item) {
		return this.universalMetric.handle(item);
	}
	
}

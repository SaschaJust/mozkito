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

import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalTestPageRankMetric.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class UniversalTestPageRankMetric {
	
	/** The universal metric. */
	private final UniversalPageRankMetric<String> universalMetric;
	
	/**
	 * Instantiates a new universal test page rank metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalTestPageRankMetric(final ChangeGenealogy<String> genealogy) {
		this.universalMetric = new UniversalPageRankMetric<String>(genealogy);
	}
	
	/**
	 * Handle.
	 * 
	 * @param item
	 *            the item
	 * @param last
	 *            the last
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final String item,
	                                               final boolean last) {
		return this.universalMetric.handle(item, last);
	}
	
}

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

package de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.universal;

import java.util.Collection;

import de.unisaarland.cs.st.mozkito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.universal.UniversalPageRankMetric;

/**
 * The Class UniversalTestPageRankMetric.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalTestPageRankMetric {
	
	/** The universal metric. */
	private UniversalPageRankMetric<String> universalMetric;
	
	/**
	 * Instantiates a new universal test page rank metric.
	 *
	 * @param genealogy the genealogy
	 */
	public UniversalTestPageRankMetric(ChangeGenealogy<String> genealogy) {
		this.universalMetric = new UniversalPageRankMetric<String>(genealogy);
	}
	
	/**
	 * Handle.
	 *
	 * @param item the item
	 * @param last the last
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(String item,
	                                               boolean last) {
		return this.universalMetric.handle(item, last);
	}
	
}

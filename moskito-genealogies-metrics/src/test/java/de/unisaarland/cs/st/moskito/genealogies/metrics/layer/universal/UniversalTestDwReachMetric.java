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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.Comparator;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalTestDwReachMetric.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalTestDwReachMetric {
	
	/** The universal metric. */
	private UniversalDwReachMetric<String> universalMetric;
	
	/**
	 * Instantiates a new universal test dw reach metric.
	 *
	 * @param genealogy the genealogy
	 */
	public UniversalTestDwReachMetric(ChangeGenealogy<String> genealogy) {
		universalMetric = new UniversalDwReachMetric<String>(genealogy, new Comparator<String>() {
			
			@Override
			public int compare(String o1,
			                   String o2) {
				return -1;
			}
		});
	}
	
	/**
	 * Handle.
	 *
	 * @param item the item
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(String item) {
		return universalMetric.handle(item);
	}
	
}

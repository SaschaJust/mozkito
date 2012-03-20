/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalTestPageRankMetric {
	
	private UniversalPageRankMetric<String> universalMetric;
	
	public UniversalTestPageRankMetric(ChangeGenealogy<String> genealogy) {
		universalMetric = new UniversalPageRankMetric<String>(genealogy);
	}
	
	public Collection<GenealogyMetricValue> handle(String item,
	                                               boolean last) {
		return universalMetric.handle(item, last);
	}
	
}

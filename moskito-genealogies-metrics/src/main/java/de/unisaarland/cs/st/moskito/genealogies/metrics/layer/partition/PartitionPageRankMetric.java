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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalPageRankMetric;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class PartitionPageRankMetric.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionPageRankMetric extends GenealogyPartitionMetric {
	
	/** The universal metric. */
	private UniversalPageRankMetric<Collection<JavaChangeOperation>> universalMetric;
	
	/**
	 * Instantiates a new partition page rank metric.
	 *
	 * @param genealogy the genealogy
	 */
	public PartitionPageRankMetric(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalPageRankMetric<Collection<JavaChangeOperation>>(genealogy);
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalPageRankMetric.getMetricNames();
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return this.universalMetric.handle(item.getNode(), item.isLast());
	}
	
}

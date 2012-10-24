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
 *******************************************************************************/

package de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import de.unisaarland.cs.st.mozkito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.universal.UniversalSiblingMetrics;

/**
 * The Class PartitionSiblingMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionSiblingMetrics extends GenealogyPartitionMetric {
	
	/** The universal metric. */
	private final UniversalSiblingMetrics<ChangeGenealogyLayerNode> universalMetric;
	
	/**
	 * Instantiates a new partition sibling metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionSiblingMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalSiblingMetrics<ChangeGenealogyLayerNode>(genealogy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalSiblingMetrics.getMetricNames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		return this.universalMetric.handle(item.getNode());
	}
	
}

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

package org.mozkito.genealogies.metrics.layer.partition;

import java.util.Collection;

import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.genealogies.layer.PartitionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyPartitionNode;
import org.mozkito.genealogies.metrics.layer.universal.UniversalChildrenMetrics;

/**
 * The Class PartitionChildrenMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionChildrenMetrics extends GenealogyPartitionMetric {
	
	/** The universal metric. */
	private final UniversalChildrenMetrics<ChangeGenealogyLayerNode> universalMetric;
	
	/**
	 * Instantiates a new partition children metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionChildrenMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalChildrenMetrics<ChangeGenealogyLayerNode>(genealogy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalChildrenMetrics.getMetricNames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		return this.universalMetric.handle(item.getNode());
	}
	
}

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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogyLayerNode;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTempParentsMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;

/**
 * The Class PartitionTempParentsMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionTempParentsMetrics extends GenealogyPartitionMetric implements
        DayTimeDiff<ChangeGenealogyLayerNode> {
	
	/** The universal metric. */
	private final UniversalTempParentsMetrics<ChangeGenealogyLayerNode> universalMetric;
	
	/**
	 * Instantiates a new partition temp parents metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionTempParentsMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalTempParentsMetrics<ChangeGenealogyLayerNode>(genealogy, this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff#daysDiff(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int daysDiff(final ChangeGenealogyLayerNode t1,
	                    final ChangeGenealogyLayerNode t2) {
		return DaysBetweenUtils.getDaysBetween(t1, t2);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalTempParentsMetrics.getMetricNames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		return this.universalMetric.handle(item.getNode());
	}
	
}

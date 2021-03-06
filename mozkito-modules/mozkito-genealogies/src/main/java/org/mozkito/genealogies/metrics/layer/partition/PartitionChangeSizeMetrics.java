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

package org.mozkito.genealogies.metrics.layer.partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.genealogies.layer.PartitionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyPartitionNode;

/**
 * The Class PartitionChangeSizeMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionChangeSizeMetrics extends GenealogyPartitionMetric {
	
	/** The Constant changeSize. */
	private static final String CHANGE_SIZE            = "changeSize";
	
	/** The Constant avgDepChangeSize. */
	private static final String AVG_DEP_CHANGE_SIZE    = "avgDepChangeSize";
	
	/** The Constant maxDepChangeSize. */
	private static final String MAX_DEP_CHANGE_SIZE    = "maxDepChangeSize";
	
	/** The Constant sumDepChangeSize. */
	private static final String SUM_DEP_CHANGE_SIZE    = "sumDepChangeSize";
	
	/** The Constant avgParentChangeSize. */
	private static final String AVG_PARENT_CHANGE_SIZE = "avgParentChangeSize";
	
	/** The Constant maxParentChangeSize. */
	private static final String MAX_PARENT_CHANGE_SIZE = "maxParentChangeSize";
	
	/** The Constant sumParentChangeSize. */
	private static final String SUM_PARENT_CHANGE_SIZE = "sumParentChangeSize";
	
	/**
	 * Instantiates a new partition change size metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionChangeSizeMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final List<String> metricNames = new ArrayList<String>(7);
		metricNames.add(PartitionChangeSizeMetrics.CHANGE_SIZE);
		metricNames.add(PartitionChangeSizeMetrics.AVG_DEP_CHANGE_SIZE);
		metricNames.add(PartitionChangeSizeMetrics.MAX_DEP_CHANGE_SIZE);
		metricNames.add(PartitionChangeSizeMetrics.SUM_DEP_CHANGE_SIZE);
		metricNames.add(PartitionChangeSizeMetrics.AVG_PARENT_CHANGE_SIZE);
		metricNames.add(PartitionChangeSizeMetrics.MAX_PARENT_CHANGE_SIZE);
		metricNames.add(PartitionChangeSizeMetrics.SUM_PARENT_CHANGE_SIZE);
		return metricNames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		final ChangeGenealogyLayerNode partition = item.getNode();
		final String nodeId = partition.getNodeId();
		
		final DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		final DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.CHANGE_SIZE, nodeId, partition.size()));
		
		for (final ChangeGenealogyLayerNode dependant : this.genealogy.getAllDependents(partition)) {
			dependantStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.AVG_DEP_CHANGE_SIZE, nodeId,
		                                          dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.MAX_DEP_CHANGE_SIZE, nodeId,
		                                          dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.SUM_DEP_CHANGE_SIZE, nodeId,
		                                          dependantStats.getSum()));
		
		for (final ChangeGenealogyLayerNode dependant : this.genealogy.getAllParents(partition)) {
			parentStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.AVG_PARENT_CHANGE_SIZE, nodeId,
		                                          parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.MAX_PARENT_CHANGE_SIZE, nodeId,
		                                          parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(PartitionChangeSizeMetrics.SUM_PARENT_CHANGE_SIZE, nodeId,
		                                          parentStats.getSum()));
		
		return metricValues;
	}
	
}

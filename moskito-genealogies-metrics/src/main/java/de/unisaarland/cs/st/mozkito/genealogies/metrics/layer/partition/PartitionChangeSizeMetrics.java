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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import de.unisaarland.cs.st.mozkito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyPartitionNode;

/**
 * The Class PartitionChangeSizeMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionChangeSizeMetrics extends GenealogyPartitionMetric {
	
	/** The Constant changeSize. */
	private static final String changeSize          = "changeSize";
	
	/** The Constant avgDepChangeSize. */
	private static final String avgDepChangeSize    = "avgDepChangeSize";
	
	/** The Constant maxDepChangeSize. */
	private static final String maxDepChangeSize    = "maxDepChangeSize";
	
	/** The Constant sumDepChangeSize. */
	private static final String sumDepChangeSize    = "sumDepChangeSize";
	
	/** The Constant avgParentChangeSize. */
	private static final String avgParentChangeSize = "avgParentChangeSize";
	
	/** The Constant maxParentChangeSize. */
	private static final String maxParentChangeSize = "maxParentChangeSize";
	
	/** The Constant sumParentChangeSize. */
	private static final String sumParentChangeSize = "sumParentChangeSize";
	
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
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final List<String> metricNames = new ArrayList<String>(7);
		metricNames.add(changeSize);
		metricNames.add(avgDepChangeSize);
		metricNames.add(maxDepChangeSize);
		metricNames.add(sumDepChangeSize);
		metricNames.add(avgParentChangeSize);
		metricNames.add(maxParentChangeSize);
		metricNames.add(sumParentChangeSize);
		return metricNames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		final ChangeGenealogyLayerNode partition = item.getNode();
		final String nodeId = partition.getNodeId();
		
		final DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		final DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		metricValues.add(new GenealogyMetricValue(changeSize, nodeId, partition.size()));
		
		for (final ChangeGenealogyLayerNode dependant : this.genealogy.getAllDependants(partition)) {
			dependantStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgDepChangeSize, nodeId, dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxDepChangeSize, nodeId, dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumDepChangeSize, nodeId, dependantStats.getSum()));
		
		for (final ChangeGenealogyLayerNode dependant : this.genealogy.getAllParents(partition)) {
			parentStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgParentChangeSize, nodeId, parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentChangeSize, nodeId, parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumParentChangeSize, nodeId, parentStats.getSum()));
		
		return metricValues;
	}
	
}

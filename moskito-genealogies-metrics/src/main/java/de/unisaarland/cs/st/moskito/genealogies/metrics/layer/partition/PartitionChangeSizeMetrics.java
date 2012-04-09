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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

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
	 * @param genealogy the genealogy
	 */
	public PartitionChangeSizeMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>(7);
		metricNames.add(changeSize);
		metricNames.add(avgDepChangeSize);
		metricNames.add(maxDepChangeSize);
		metricNames.add(sumDepChangeSize);
		metricNames.add(avgParentChangeSize);
		metricNames.add(maxParentChangeSize);
		metricNames.add(sumParentChangeSize);
		return metricNames;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		Collection<JavaChangeOperation> partition = item.getNode();
		String nodeId = genealogy.getNodeId(partition);
		
		DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		metricValues.add(new GenealogyMetricValue(changeSize, nodeId, partition.size()));
		
		for (Collection<JavaChangeOperation> dependant : genealogy.getAllDependants(partition)) {
			dependantStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgDepChangeSize, nodeId, dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxDepChangeSize, nodeId, dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumDepChangeSize, nodeId, dependantStats.getSum()));
		
		for (Collection<JavaChangeOperation> dependant : genealogy.getAllParents(partition)) {
			parentStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgParentChangeSize, nodeId, parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentChangeSize, nodeId, parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumParentChangeSize, nodeId, parentStats.getSum()));
		
		return metricValues;
	}
	
}

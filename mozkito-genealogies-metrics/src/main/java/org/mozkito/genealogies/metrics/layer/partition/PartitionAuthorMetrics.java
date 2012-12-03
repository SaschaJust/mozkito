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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.genealogies.layer.PartitionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyPartitionNode;

/**
 * The Class PartitionAuthorMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionAuthorMetrics extends GenealogyPartitionMetric {
	
	/** The Constant numDepAuthors. */
	private static final String NUM_DEP_AUTHORS    = "NumDepAuthors";
	
	/** The Constant numParentAuthors. */
	private static final String NUM_PARENT_AUTHORS = "NumParentAuthors";
	
	/**
	 * Instantiates a new partition author metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionAuthorMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(PartitionAuthorMetrics.NUM_DEP_AUTHORS);
		metricNames.add(PartitionAuthorMetrics.NUM_PARENT_AUTHORS);
		return metricNames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		final ChangeGenealogyLayerNode pNode = item.getNode();
		final String nodeId = pNode.getNodeId();
		
		final Set<Long> depAuthors = new HashSet<Long>();
		for (final ChangeGenealogyLayerNode dependant : this.genealogy.getAllDependants(pNode)) {
			for (final JavaChangeOperation tmpOp : dependant) {
				depAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(PartitionAuthorMetrics.NUM_DEP_AUTHORS, nodeId, depAuthors.size()));
		
		final Set<Long> parentAuthors = new HashSet<Long>();
		for (final ChangeGenealogyLayerNode parent : this.genealogy.getAllParents(pNode)) {
			for (final JavaChangeOperation tmpOp : parent) {
				parentAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(PartitionAuthorMetrics.NUM_PARENT_AUTHORS, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}

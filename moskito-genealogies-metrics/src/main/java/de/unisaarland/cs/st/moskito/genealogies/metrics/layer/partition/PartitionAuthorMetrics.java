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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.layer.ChangeGenealogyLayerNode;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class PartitionAuthorMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionAuthorMetrics extends GenealogyPartitionMetric {
	
	/** The Constant numDepAuthors. */
	private static final String numDepAuthors    = "NumDepAuthors";
	
	/** The Constant numParentAuthors. */
	private static final String numParentAuthors = "NumParentAuthors";
	
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
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(numDepAuthors);
		metricNames.add(numParentAuthors);
		return metricNames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
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
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		final Set<Long> parentAuthors = new HashSet<Long>();
		for (final ChangeGenealogyLayerNode parent : this.genealogy.getAllParents(pNode)) {
			for (final JavaChangeOperation tmpOp : parent) {
				parentAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}

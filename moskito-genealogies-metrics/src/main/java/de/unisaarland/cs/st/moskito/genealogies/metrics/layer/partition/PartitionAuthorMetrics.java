/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionAuthorMetrics extends GenealogyPartitionMetric {
	
	private static final String    numDepAuthors    = "changeSize";
	private static final String    numParentAuthors = "avgDepChangeSize";
	
	public PartitionAuthorMetrics(PartitionChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(numDepAuthors);
		metricNames.add(numParentAuthors);
		return metricNames;
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		Collection<JavaChangeOperation> transaction = item.getNode();
		String nodeId = genealogy.getNodeId(transaction);
		
		Set<Long> depAuthors = new HashSet<Long>();
		for (Collection<JavaChangeOperation> dependant : genealogy.getAllDependants(transaction)) {
			for (JavaChangeOperation tmpOp : dependant) {
				depAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		Set<Long> parentAuthors = new HashSet<Long>();
		for (Collection<JavaChangeOperation> parent : genealogy.getAllParents(transaction)) {
			for (JavaChangeOperation tmpOp : parent) {
				parentAuthors.add(tmpOp.getRevision().getTransaction().getPersons().getGeneratedId());
			}
		}
		
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}

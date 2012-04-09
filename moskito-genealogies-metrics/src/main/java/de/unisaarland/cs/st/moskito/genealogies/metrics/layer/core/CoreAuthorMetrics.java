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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class CoreAuthorMetrics.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreAuthorMetrics extends GenealogyCoreMetric {
	
	/** The Constant numDepAuthors. */
	private static final String numDepAuthors    = "changeSize";
	
	/** The Constant numParentAuthors. */
	private static final String numParentAuthors = "avgDepChangeSize";
	
	/**
	 * Instantiates a new core author metrics.
	 *
	 * @param genealogy the genealogy
	 */
	public CoreAuthorMetrics(CoreChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(numDepAuthors);
		metricNames.add(numParentAuthors);
		return metricNames;
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		JavaChangeOperation operation = item.getNode();
		String nodeId = genealogy.getNodeId(operation);
		
		Set<Long> depAuthors = new HashSet<Long>();
		for (JavaChangeOperation dependant : genealogy.getAllDependants(operation)) {
			depAuthors.add(dependant.getRevision().getTransaction().getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		Set<Long> parentAuthors = new HashSet<Long>();
		for (JavaChangeOperation parent : genealogy.getAllParents(operation)) {
			parentAuthors.add(parent.getRevision().getTransaction().getPersons().getGeneratedId());
		}
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}

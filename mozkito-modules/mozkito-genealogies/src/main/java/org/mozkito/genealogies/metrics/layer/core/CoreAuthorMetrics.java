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

package org.mozkito.genealogies.metrics.layer.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyCoreNode;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class CoreAuthorMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CoreAuthorMetrics extends GenealogyCoreMetric {
	
	/** The Constant numDepAuthors. */
	private static final String NUM_DEP_AUTHORS    = "changeSize";
	
	/** The Constant numParentAuthors. */
	private static final String NUM_PARENT_AUTHORS = "avgDepChangeSize";
	
	/**
	 * Instantiates a new core author metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public CoreAuthorMetrics(final CoreChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(CoreAuthorMetrics.NUM_DEP_AUTHORS);
		metricNames.add(CoreAuthorMetrics.NUM_PARENT_AUTHORS);
		return metricNames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyCoreNode item) {
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		final JavaChangeOperation operation = item.getNode();
		final String nodeId = this.genealogy.getNodeId(operation);
		
		final Set<Long> depAuthors = new HashSet<Long>();
		for (final JavaChangeOperation dependant : this.genealogy.getAllDependents(operation)) {
			depAuthors.add(dependant.getRevision().getChangeSet().getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(CoreAuthorMetrics.NUM_DEP_AUTHORS, nodeId, depAuthors.size()));
		
		final Set<Long> parentAuthors = new HashSet<Long>();
		for (final JavaChangeOperation parent : this.genealogy.getAllParents(operation)) {
			parentAuthors.add(parent.getRevision().getChangeSet().getPersons().getGeneratedId());
		}
		metricValues.add(new GenealogyMetricValue(CoreAuthorMetrics.NUM_PARENT_AUTHORS, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}

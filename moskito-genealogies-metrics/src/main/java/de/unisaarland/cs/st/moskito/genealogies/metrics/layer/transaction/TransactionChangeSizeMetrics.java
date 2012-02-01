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
 ******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionChangeSizeMetrics extends GenealogyTransactionMetric {
	
	private static final String   changeSize          = "changeSize";
	private static final String   avgDepChangeSize    = "avgDepChangeSize";
	private static final String   maxDepChangeSize    = "maxDepChangeSize";
	private static final String   sumDepChangeSize    = "sumDepChangeSize";
	private static final String   avgParentChangeSize = "avgParentChangeSize";
	private static final String   maxParentChangeSize = "maxParentChangeSize";
	private static final String   sumParentChangeSize = "sumParentChangeSize";
	private final PersistenceUtil persistenceUtil;
	
	public TransactionChangeSizeMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		this.persistenceUtil = genealogy.getCore().getPersistenceUtil();
	}
	
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
	
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getCanonicalName() + " handles node " + item.getNodeId());
		}
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		final RCSTransaction transaction = item.getNode();
		final String nodeId = this.genealogy.getNodeId(transaction);
		
		final DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		final DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		metricValues.add(new GenealogyMetricValue(changeSize, nodeId,
		                                          PPAPersistenceUtil.getChangeOperation(this.persistenceUtil,
		                                                                                transaction).size()));
		
		for (final RCSTransaction dependant : this.genealogy.getAllDependants(transaction)) {
			dependantStats.addValue(PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, dependant).size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgDepChangeSize, nodeId,
		                                          (dependantStats.getN() < 0)
		                                                                     ? 0
		                                                                     : dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxDepChangeSize, nodeId,
		                                          (dependantStats.getN() < 0)
		                                                                     ? 0
		                                                                     : dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumDepChangeSize, nodeId,
		                                          (dependantStats.getN() < 0)
		                                                                     ? 0
		                                                                     : dependantStats.getSum()));
		
		for (final RCSTransaction parent : this.genealogy.getAllParents(transaction)) {
			parentStats.addValue(PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, parent).size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgParentChangeSize, nodeId,
		                                          (parentStats.getN() < 0)
		                                                                  ? 0
		                                                                  : parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentChangeSize, nodeId,
		                                          (parentStats.getN() < 0)
		                                                                  ? 0
		                                                                  : parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumParentChangeSize, nodeId,
		                                          (parentStats.getN() < 0)
		                                                                  ? 0
		                                                                  : parentStats.getSum()));
		
		return metricValues;
	}
	
}

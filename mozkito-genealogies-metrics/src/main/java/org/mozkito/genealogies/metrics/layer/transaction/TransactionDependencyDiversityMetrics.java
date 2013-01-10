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
package org.mozkito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyTransactionNode;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class TransactionDependencyDiversityMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TransactionDependencyDiversityMetrics extends GenealogyTransactionMetric {
	
	/** The Constant avgParentsFileDiversityName. */
	private static final String AVG_PARENTS_FILE_DIVERSITY_NAME    = "AvgParentsFileDiversity";
	
	/** The Constant maxParentsFileDiversityName. */
	private static final String MAX_PARENTS_FILE_DIVERSITY_NAME    = "MaxParentsFileDiversity";
	
	/** The Constant minParentsFileDiversityName. */
	private static final String MIN_PARENTS_FILE_DIVERSITY_NAME    = "MinParentsFileDiversity";
	
	/** The Constant avgDependantsFileDiversityName. */
	private static final String AVG_DEPENDANTS_FILE_DIVERSITY_NAME = "AvgDependantsFileDiversity";
	
	/** The Constant maxDependantsFileDiversityName. */
	private static final String MAX_DEPENDANTS_FILE_DIVERSITY_NAME = "MaxDependantsFileDiversity";
	
	/** The Constant minDependantsFileDiversityName. */
	private static final String MIN_DEPENDANTS_FILE_DIVERSITY_NAME = "MinDependantsFileDiversity";
	
	/**
	 * Instantiates a new transaction dependency diversity metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public TransactionDependencyDiversityMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		genealogy.getCore().getPersistenceUtil();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final Set<String> result = new HashSet<String>();
		result.add(TransactionDependencyDiversityMetrics.AVG_PARENTS_FILE_DIVERSITY_NAME);
		result.add(TransactionDependencyDiversityMetrics.MAX_PARENTS_FILE_DIVERSITY_NAME);
		result.add(TransactionDependencyDiversityMetrics.MIN_PARENTS_FILE_DIVERSITY_NAME);
		result.add(TransactionDependencyDiversityMetrics.AVG_DEPENDANTS_FILE_DIVERSITY_NAME);
		result.add(TransactionDependencyDiversityMetrics.MAX_DEPENDANTS_FILE_DIVERSITY_NAME);
		result.add(TransactionDependencyDiversityMetrics.MIN_DEPENDANTS_FILE_DIVERSITY_NAME);
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		final DescriptiveStatistics parentStat = new DescriptiveStatistics();
		final DescriptiveStatistics dependantStat = new DescriptiveStatistics();
		
		final Collection<Handle> changedFiles = item.getNode().getChangedFiles();
		
		for (final ChangeSet parent : this.genealogy.getAllParents(item.getNode())) {
			final int intersectionSize = CollectionUtils.intersection(changedFiles, parent.getChangedFiles()).size();
			parentStat.addValue(1d - ((double) intersectionSize / (double) changedFiles.size()));
		}
		
		for (final ChangeSet dependant : this.genealogy.getAllDependants(item.getNode())) {
			final int intersectionSize = CollectionUtils.intersection(changedFiles, dependant.getChangedFiles()).size();
			dependantStat.addValue(1d - ((double) intersectionSize / (double) changedFiles.size()));
		}
		
		final String nodeId = item.getNodeId();
		
		final Collection<GenealogyMetricValue> result = new HashSet<GenealogyMetricValue>();
		
		result.add(new GenealogyMetricValue(TransactionDependencyDiversityMetrics.AVG_PARENTS_FILE_DIVERSITY_NAME, nodeId,
		                                    (parentStat.getN() > 0)
		                                                           ? parentStat.getMean()
		                                                           : -1));
		result.add(new GenealogyMetricValue(TransactionDependencyDiversityMetrics.MAX_PARENTS_FILE_DIVERSITY_NAME, nodeId,
		                                    (parentStat.getN() > 0)
		                                                           ? parentStat.getMax()
		                                                           : -1));
		result.add(new GenealogyMetricValue(TransactionDependencyDiversityMetrics.MIN_PARENTS_FILE_DIVERSITY_NAME, nodeId,
		                                    (parentStat.getN() > 0)
		                                                           ? parentStat.getMin()
		                                                           : -1));
		
		result.add(new GenealogyMetricValue(TransactionDependencyDiversityMetrics.AVG_DEPENDANTS_FILE_DIVERSITY_NAME,
		                                    nodeId, (dependantStat.getN() > 0)
		                                                                      ? dependantStat.getMean()
		                                                                      : -1));
		result.add(new GenealogyMetricValue(TransactionDependencyDiversityMetrics.MAX_DEPENDANTS_FILE_DIVERSITY_NAME,
		                                    nodeId, (dependantStat.getN() > 0)
		                                                                      ? dependantStat.getMax()
		                                                                      : -1));
		result.add(new GenealogyMetricValue(TransactionDependencyDiversityMetrics.MIN_DEPENDANTS_FILE_DIVERSITY_NAME,
		                                    nodeId, (dependantStat.getN() > 0)
		                                                                      ? dependantStat.getMin()
		                                                                      : -1));
		
		return result;
	}
}

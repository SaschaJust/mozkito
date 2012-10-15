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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;

/**
 * The Class TransactionDependencyDiversityMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionDependencyDiversityMetrics extends GenealogyPartitionMetric {
	
	/** The Constant avgParentsFileDiversityName. */
	private final static String avgParentsFileDiversityName    = "AvgParentsFileDiversity";
	
	/** The Constant maxParentsFileDiversityName. */
	private final static String maxParentsFileDiversityName    = "MaxParentsFileDiversity";
	
	/** The Constant minParentsFileDiversityName. */
	private final static String minParentsFileDiversityName    = "MinParentsFileDiversity";
	
	/** The Constant avgDependantsFileDiversityName. */
	private final static String avgDependantsFileDiversityName = "AvgDependantsFileDiversity";
	
	/** The Constant maxDependantsFileDiversityName. */
	private final static String maxDependantsFileDiversityName = "MaxDependantsFileDiversity";
	
	/** The Constant minDependantsFileDiversityName. */
	private final static String minDependantsFileDiversityName = "MinDependantsFileDiversity";
	
	/**
	 * Instantiates a new transaction dependency diversity metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionDependencyDiversityMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
		genealogy.getCore().getPersistenceUtil();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final Set<String> result = new HashSet<String>();
		result.add(avgParentsFileDiversityName);
		result.add(maxParentsFileDiversityName);
		result.add(minParentsFileDiversityName);
		result.add(avgDependantsFileDiversityName);
		result.add(maxDependantsFileDiversityName);
		result.add(minDependantsFileDiversityName);
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		final DescriptiveStatistics parentStat = new DescriptiveStatistics();
		final DescriptiveStatistics dependantStat = new DescriptiveStatistics();
		
		final Set<RCSFile> changedFiles = new HashSet<RCSFile>();
		for (final JavaChangeOperation op : item.getNode()) {
			changedFiles.add(op.getRevision().getChangedFile());
		}
		
		for (final Collection<JavaChangeOperation> parent : this.genealogy.getAllParents(item.getNode())) {
			final Set<RCSFile> parentChangedFiles = new HashSet<RCSFile>();
			for (final JavaChangeOperation op : parent) {
				parentChangedFiles.add(op.getRevision().getChangedFile());
			}
			
			final int intersectionSize = CollectionUtils.intersection(changedFiles, parentChangedFiles).size();
			parentStat.addValue(1d - ((double) intersectionSize / (double) changedFiles.size()));
		}
		
		for (final Collection<JavaChangeOperation> dependant : this.genealogy.getAllDependants(item.getNode())) {
			final Set<RCSFile> dependentChangedFiles = new HashSet<RCSFile>();
			for (final JavaChangeOperation op : dependant) {
				dependentChangedFiles.add(op.getRevision().getChangedFile());
			}
			
			final int intersectionSize = CollectionUtils.intersection(changedFiles, dependentChangedFiles).size();
			dependantStat.addValue(1d - ((double) intersectionSize / (double) changedFiles.size()));
		}
		
		final String nodeId = item.getNodeId();
		
		final Collection<GenealogyMetricValue> result = new HashSet<GenealogyMetricValue>();
		
		result.add(new GenealogyMetricValue(avgParentsFileDiversityName, nodeId,
		                                    (parentStat.getN() > 0)
		                                                           ? parentStat.getMean()
		                                                           : -1));
		result.add(new GenealogyMetricValue(maxParentsFileDiversityName, nodeId,
		                                    (parentStat.getN() > 0)
		                                                           ? parentStat.getMax()
		                                                           : -1));
		result.add(new GenealogyMetricValue(minParentsFileDiversityName, nodeId,
		                                    (parentStat.getN() > 0)
		                                                           ? parentStat.getMin()
		                                                           : -1));
		
		result.add(new GenealogyMetricValue(avgDependantsFileDiversityName, nodeId,
		                                    (dependantStat.getN() > 0)
		                                                              ? dependantStat.getMean()
		                                                              : -1));
		result.add(new GenealogyMetricValue(maxDependantsFileDiversityName, nodeId,
		                                    (dependantStat.getN() > 0)
		                                                              ? dependantStat.getMax()
		                                                              : -1));
		result.add(new GenealogyMetricValue(minDependantsFileDiversityName, nodeId,
		                                    (dependantStat.getN() > 0)
		                                                              ? dependantStat.getMin()
		                                                              : -1));
		
		return result;
	}
}

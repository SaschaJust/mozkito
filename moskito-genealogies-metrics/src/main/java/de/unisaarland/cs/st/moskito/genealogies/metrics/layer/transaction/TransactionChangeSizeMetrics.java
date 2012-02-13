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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionChangeSizeMetrics extends GenealogyTransactionMetric {
	
	private static final String   changeSize                    = "numChangeOperations";
	
	private static final String   numAddOperations              = "numAddOperations";
	private static final String   numDelOperations              = "numDelOperations";
	private static final String   numAddedMethDefs              = "numAddedMethDefs";
	private static final String   numDeletedMethDefs            = "numDeletedMethDefs";
	
	private static final String   numAddedClassDefs             = "numAddedClassDefs";
	private static final String   numDeletedClassDefs           = "numDeletedClassDefs";
	
	private static final String   numAddedCalls                 = "numAddedCalls";
	private static final String   numDeletedCalls               = "numDeletedCalls";
	
	private static final String   avgDepChangeSize              = "avgDepChangeSize";
	private static final String   maxDepChangeSize              = "maxDepChangeSize";
	private static final String   sumDepChangeSize              = "sumDepChangeSize";
	private static final String   avgParentChangeSize           = "avgParentChangeSize";
	private static final String   maxParentChangeSize           = "maxParentChangeSize";
	private static final String   sumParentChangeSize           = "sumParentChangeSize";
	
	private static final String   numChangedFiles               = "numChangedFiles";
	private static final String   effectiveNumOperations        = "effectiveNumOperations";
	private static final String   effectiveNumMethDefOperations = "effectiveNumMethDefOperations";
	private static final String   effectiveNumCallOperations    = "effectiveNumCallOperations";
	private static final String   changedBlocks                 = "numChangedLineBlocks";
	
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
		
		metricNames.add(numChangedFiles);
		metricNames.add(effectiveNumOperations);
		metricNames.add(effectiveNumMethDefOperations);
		metricNames.add(effectiveNumCallOperations);
		
		metricNames.add(changedBlocks);
		
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
		
		final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil,
		                                                                                               transaction);
		
		metricValues.add(new GenealogyMetricValue(changeSize, nodeId, changeOperations.size()));
		
		int numAddOperations = 0;
		int numDelOperations = 0;
		int numAddedMethDefs = 0;
		int numAddedClassDefs = 0;
		int numDeletedMethDefs = 0;
		int numDeletedClassDefs = 0;
		int numAddedCalls = 0;
		int numDeletedCalls = 0;
		
		final Set<String> changedFiles = new HashSet<String>();
		final Map<String, Set<Integer>> changedLines = new HashMap<String, Set<Integer>>();
		
		for (final JavaChangeOperation op : changeOperations) {
			
			final JavaElement element = op.getChangedElementLocation().getElement();
			
			changedFiles.add(op.getChangedPath());
			
			if (!changedLines.containsKey(op.getChangedPath())) {
				changedLines.put(op.getChangedPath(), new TreeSet<Integer>());
			}
			
			final JavaElementLocation changedElementLocation = op.getChangedElementLocation();
			for (int i = changedElementLocation.getStartLine(); i <= changedElementLocation.getEndLine(); ++i) {
				changedLines.get(op.getChangedPath()).add(i);
			}
			
			if (op.getChangeType().equals(ChangeType.Added)) {
				++numAddOperations;
				if (element instanceof JavaMethodDefinition) {
					++numAddedMethDefs;
				} else if (element instanceof JavaMethodCall) {
					++numAddedCalls;
				} else if (element instanceof JavaMethodCall) {
					++numAddedClassDefs;
				}
			} else if (op.getChangeType().equals(ChangeType.Deleted)) {
				++numDelOperations;
				if (element instanceof JavaMethodDefinition) {
					++numDeletedMethDefs;
				} else if (element instanceof JavaMethodCall) {
					++numDeletedCalls;
				} else if (element instanceof JavaMethodCall) {
					++numDeletedClassDefs;
				}
			}
		}
		
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numAddOperations, nodeId,
		                                          numAddOperations));
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numDelOperations, nodeId,
		                                          numDelOperations));
		
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numAddedMethDefs, nodeId,
		                                          numAddedMethDefs));
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numAddedClassDefs, nodeId,
		                                          numAddedClassDefs));
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numAddedCalls, nodeId, numAddedCalls));
		
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numDeletedMethDefs, nodeId,
		                                          numDeletedMethDefs));
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numDeletedClassDefs, nodeId,
		                                          numDeletedClassDefs));
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numDeletedCalls, nodeId, numDeletedCalls));
		
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.numChangedFiles, nodeId,
		                                          changedFiles.size()));
		
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.effectiveNumOperations, nodeId,
		                                          numAddOperations - numDelOperations));
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.effectiveNumMethDefOperations, nodeId,
		                                          numAddedMethDefs - numDeletedMethDefs));
		
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.effectiveNumCallOperations, nodeId,
		                                          numAddedCalls - numDeletedCalls));
		
		int numChangedLineBlocks = 0;
		for (final String filePath : changedLines.keySet()) {
			++numChangedLineBlocks;
			int lastLine = -1;
			for (final int line : changedLines.get(filePath)) {
				if ((lastLine != -1) && ((lastLine + 1) < line)) {
					++numChangedLineBlocks;
				}
				lastLine = line;
			}
		}
		metricValues.add(new GenealogyMetricValue(TransactionChangeSizeMetrics.changedBlocks, nodeId,
		                                          numChangedLineBlocks));
		
		for (final RCSTransaction dependant : this.genealogy.getAllDependants(transaction)) {
			dependantStats.addValue(PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, dependant).size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgDepChangeSize, nodeId,
		                                          (dependantStats.getN() < 1)
		                                                                     ? 0
		                                                                     : dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxDepChangeSize, nodeId,
		                                          (dependantStats.getN() < 1)
		                                                                     ? 0
		                                                                     : dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumDepChangeSize, nodeId,
		                                          (dependantStats.getN() < 1)
		                                                                     ? 0
		                                                                     : dependantStats.getSum()));
		
		for (final RCSTransaction parent : this.genealogy.getAllParents(transaction)) {
			parentStats.addValue(PPAPersistenceUtil.getChangeOperation(this.persistenceUtil, parent).size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgParentChangeSize, nodeId,
		                                          (parentStats.getN() < 1)
		                                                                  ? 0
		                                                                  : parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentChangeSize, nodeId,
		                                          (parentStats.getN() < 1)
		                                                                  ? 0
		                                                                  : parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumParentChangeSize, nodeId,
		                                          (parentStats.getN() < 1)
		                                                                  ? 0
		                                                                  : parentStats.getSum()));
		
		return metricValues;
	}
}

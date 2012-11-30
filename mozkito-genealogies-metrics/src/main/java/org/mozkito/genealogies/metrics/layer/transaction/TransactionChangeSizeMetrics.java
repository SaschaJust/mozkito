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

package org.mozkito.genealogies.metrics.layer.transaction;

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
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaElementLocation;
import org.mozkito.codeanalysis.model.JavaMethodCall;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyTransactionNode;
import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.RCSTransaction;


/**
 * The Class TransactionChangeSizeMetrics.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TransactionChangeSizeMetrics extends GenealogyTransactionMetric {
	
	/** The Constant changeSize. */
	private static final String   changeSize                    = "numChangeOperations";
	
	/** The Constant numAddOperations. */
	private static final String   numAddOperations              = "numAddOperations";
	
	/** The Constant numDelOperations. */
	private static final String   numDelOperations              = "numDelOperations";
	
	/** The Constant numAddedMethDefs. */
	private static final String   numAddedMethDefs              = "numAddedMethDefs";
	
	/** The Constant numDeletedMethDefs. */
	private static final String   numDeletedMethDefs            = "numDeletedMethDefs";
	
	/** The Constant numAddedClassDefs. */
	private static final String   numAddedClassDefs             = "numAddedClassDefs";
	
	/** The Constant numDeletedClassDefs. */
	private static final String   numDeletedClassDefs           = "numDeletedClassDefs";
	
	/** The Constant numAddedCalls. */
	private static final String   numAddedCalls                 = "numAddedCalls";
	
	/** The Constant numDeletedCalls. */
	private static final String   numDeletedCalls               = "numDeletedCalls";
	
	/** The Constant avgDepChangeSize. */
	private static final String   avgDepChangeSize              = "avgDepChangeSize";
	
	/** The Constant maxDepChangeSize. */
	private static final String   maxDepChangeSize              = "maxDepChangeSize";
	
	/** The Constant sumDepChangeSize. */
	private static final String   sumDepChangeSize              = "sumDepChangeSize";
	
	/** The Constant avgParentChangeSize. */
	private static final String   avgParentChangeSize           = "avgParentChangeSize";
	
	/** The Constant maxParentChangeSize. */
	private static final String   maxParentChangeSize           = "maxParentChangeSize";
	
	/** The Constant sumParentChangeSize. */
	private static final String   sumParentChangeSize           = "sumParentChangeSize";
	
	/** The Constant numChangedFiles. */
	private static final String   numChangedFiles               = "numChangedFiles";
	
	/** The Constant effectiveNumOperations. */
	private static final String   effectiveNumOperations        = "effectiveNumOperations";
	
	/** The Constant effectiveNumMethDefOperations. */
	private static final String   effectiveNumMethDefOperations = "effectiveNumMethDefOperations";
	
	/** The Constant effectiveNumCallOperations. */
	private static final String   effectiveNumCallOperations    = "effectiveNumCallOperations";
	
	/** The Constant changedBlocks. */
	private static final String   changedBlocks                 = "numChangedLineBlocks";
	
	/** The Constant numAffectedPackages. */
	private static final String   numAffectedPackages           = "numAffectedPackages";
	
	/** The persistence util. */
	private final PersistenceUtil persistenceUtil;
	
	/**
	 * Instantiates a new transaction change size metrics.
	 *
	 * @param genealogy the genealogy
	 */
	public TransactionChangeSizeMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		this.persistenceUtil = genealogy.getCore().getPersistenceUtil();
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
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
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getCanonicalName() + " handles node " + item.getNodeId());
		}
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		final RCSTransaction rCSTransaction = item.getNode();
		final String nodeId = this.genealogy.getNodeId(rCSTransaction);
		
		final DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		final DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil,
		                                                                                               rCSTransaction);
		
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
		
		final Set<String> packageNames = new HashSet<String>();
		
		for (final JavaChangeOperation op : changeOperations) {
			
			final String filePath = op.getChangedPath();
			if ((!filePath.endsWith(".java")) | (filePath.toLowerCase().contains("test"))) {
				continue;
			}
			
			final JavaElement element = op.getChangedElementLocation().getElement();
			
			final String elementName = element.getFullQualifiedName();
			packageNames.add(elementName.substring(0, elementName.lastIndexOf(".")));
			
			changedFiles.add(filePath);
			
			if (!changedLines.containsKey(filePath)) {
				changedLines.put(filePath, new TreeSet<Integer>());
			}
			
			final JavaElementLocation changedElementLocation = op.getChangedElementLocation();
			for (int i = changedElementLocation.getStartLine(); i <= changedElementLocation.getEndLine(); ++i) {
				changedLines.get(filePath).add(i);
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
		
		for (final RCSTransaction dependant : this.genealogy.getAllDependants(rCSTransaction)) {
			// ignore test and non java files.
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
		
		for (final RCSTransaction parent : this.genealogy.getAllParents(rCSTransaction)) {
			// ignore test and non java files.
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
		
		metricValues.add(new GenealogyMetricValue(numAffectedPackages, nodeId, packageNames.size()));
		
		return metricValues;
	}
}

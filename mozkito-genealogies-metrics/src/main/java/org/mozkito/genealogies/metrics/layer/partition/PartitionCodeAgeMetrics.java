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
package org.mozkito.genealogies.metrics.layer.partition;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Days;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.genealogies.layer.PartitionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyPartitionNode;
import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class TransactionCodeAgeMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartitionCodeAgeMetrics extends GenealogyPartitionMetric {
	
	/** The persistence util. */
	private final PersistenceUtil persistenceUtil;
	
	/** The Constant avgLastModifiedName. */
	private final static String   avgLastModifiedName    = "AvgLastModified";
	
	/** The Constant minLastModifiedName. */
	private final static String   minLastModifiedName    = "MinLastModified";
	
	/** The Constant avgAgeName. */
	private final static String   avgAgeName             = "AvgAge";
	
	/** The Constant minAgeName. */
	private final static String   minAgeName             = "MinAge";
	
	/** The Constant maxAgeName. */
	private final static String   maxAgeName             = "MaxAge";
	
	/** The Constant avgNumChangesLastMonth. */
	private final static String   avgNumChangesLastMonth = "AvgNumChangesLastMonth";
	
	/** The Constant maxNumChangesLastMonth. */
	private final static String   maxNumChangesLastMonth = "MaxNumChangesLastMonth";
	
	/**
	 * Instantiates a new transaction code age metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public PartitionCodeAgeMetrics(final PartitionChangeGenealogy genealogy) {
		super(genealogy);
		this.persistenceUtil = genealogy.getCore().getPersistenceUtil();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final Set<String> result = new HashSet<String>();
		result.add(PartitionCodeAgeMetrics.avgLastModifiedName);
		result.add(PartitionCodeAgeMetrics.minLastModifiedName);
		result.add(PartitionCodeAgeMetrics.avgAgeName);
		result.add(PartitionCodeAgeMetrics.minAgeName);
		result.add(PartitionCodeAgeMetrics.maxAgeName);
		result.add(PartitionCodeAgeMetrics.avgNumChangesLastMonth);
		result.add(PartitionCodeAgeMetrics.maxNumChangesLastMonth);
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyPartitionNode item) {
		final ChangeGenealogyLayerNode pNode = item.getNode();
		final DescriptiveStatistics lastModifiedStats = new DescriptiveStatistics();
		final DescriptiveStatistics ageStats = new DescriptiveStatistics();
		final DescriptiveStatistics numChangesStats = new DescriptiveStatistics();
		
		for (final JavaChangeOperation op : pNode) {
			
			final JavaElement element = op.getChangedElementLocation().getElement();
			
			final DateTime before = op.getRevision().getTransaction().getTimestamp();
			
			final List<RCSTransaction> pastTransactions = PPAPersistenceUtil.getTransactionsChangingElement(this.persistenceUtil,
			                                                                                                element);
			
			if (!pastTransactions.isEmpty()) {
				numChangesStats.addValue(pastTransactions.size());
				
				final RCSTransaction lastModified = pastTransactions.get(pastTransactions.size() - 1);
				lastModifiedStats.addValue(Math.abs(Days.daysBetween(lastModified.getTimestamp(), before).getDays()));
			}
			// final Transaction firstModified =
			// PPAPersistenceUtil.getFirstTransactionsChangingElement(this.persistenceUtil,
			// element);
			final DateTime firstModified = PPAPersistenceUtil.getFirstTimestampChangingElement(this.persistenceUtil,
			                                                                                   element);
			if (firstModified == null) {
				if (Logger.logWarn()) {
					Logger.warn("Could not determine timestamp for transaction firat modifying javaelement "
					        + element.getGeneratedId());
				}
				continue;
			}
			ageStats.addValue(Math.abs(Days.daysBetween(firstModified, before).getDays()));
		}
		
		final Collection<GenealogyMetricValue> result = new HashSet<GenealogyMetricValue>();
		final String nodeId = item.getNodeId();
		
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.avgLastModifiedName, nodeId,
		                                    lastModifiedStats.getN() > 0
		                                                                ? lastModifiedStats.getMean()
		                                                                : 0));
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.minLastModifiedName, nodeId,
		                                    lastModifiedStats.getN() > 0
		                                                                ? lastModifiedStats.getMin()
		                                                                : 0));
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.avgAgeName, nodeId,
		                                    ageStats.getN() > 0
		                                                       ? ageStats.getMean()
		                                                       : 0));
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.minAgeName, nodeId,
		                                    ageStats.getN() > 0
		                                                       ? ageStats.getMin()
		                                                       : 0));
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.maxAgeName, nodeId,
		                                    ageStats.getN() > 0
		                                                       ? ageStats.getMax()
		                                                       : 0));
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.avgNumChangesLastMonth, nodeId,
		                                    numChangesStats.getN() > 0
		                                                              ? numChangesStats.getMean()
		                                                              : 0));
		result.add(new GenealogyMetricValue(PartitionCodeAgeMetrics.maxNumChangesLastMonth, nodeId,
		                                    numChangesStats.getN() > 0
		                                                              ? numChangesStats.getMax()
		                                                              : 0));
		
		return result;
	}
}

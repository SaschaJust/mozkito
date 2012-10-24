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
package de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.partition;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaElement;
import de.unisaarland.cs.st.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import de.unisaarland.cs.st.mozkito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.mozkito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

/**
 * The Class TransactionCodeAgeMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
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
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		final Set<String> result = new HashSet<String>();
		result.add(avgLastModifiedName);
		result.add(minLastModifiedName);
		result.add(avgAgeName);
		result.add(minAgeName);
		result.add(maxAgeName);
		result.add(avgNumChangesLastMonth);
		result.add(maxNumChangesLastMonth);
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
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
			// final RCSTransaction firstModified =
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
		
		result.add(new GenealogyMetricValue(avgLastModifiedName, nodeId,
		                                    lastModifiedStats.getN() > 0
		                                                                ? lastModifiedStats.getMean()
		                                                                : 0));
		result.add(new GenealogyMetricValue(minLastModifiedName, nodeId,
		                                    lastModifiedStats.getN() > 0
		                                                                ? lastModifiedStats.getMin()
		                                                                : 0));
		result.add(new GenealogyMetricValue(avgAgeName, nodeId, ageStats.getN() > 0
		                                                                           ? ageStats.getMean()
		                                                                           : 0));
		result.add(new GenealogyMetricValue(minAgeName, nodeId, ageStats.getN() > 0
		                                                                           ? ageStats.getMin()
		                                                                           : 0));
		result.add(new GenealogyMetricValue(maxAgeName, nodeId, ageStats.getN() > 0
		                                                                           ? ageStats.getMax()
		                                                                           : 0));
		result.add(new GenealogyMetricValue(avgNumChangesLastMonth, nodeId,
		                                    numChangesStats.getN() > 0
		                                                              ? numChangesStats.getMean()
		                                                              : 0));
		result.add(new GenealogyMetricValue(maxNumChangesLastMonth, nodeId,
		                                    numChangesStats.getN() > 0
		                                                              ? numChangesStats.getMax()
		                                                              : 0));
		
		return result;
	}
}

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionCodeAgeMetrics extends GenealogyTransactionMetric {
	
	private final PersistenceUtil persistenceUtil;
	
	private final static String   avgLastModifiedName    = "AvgLastModified";
	private final static String   minLastModifiedName    = "MinLastModified";
	
	private final static String   avgAgeName             = "AvgAge";
	private final static String   minAgeName             = "MinAge";
	private final static String   maxAgeName             = "MaxAge";
	
	private final static String   avgNumChangesLastMonth = "AvgNumChangesLastMonth";
	private final static String   maxNumChangesLastMonth = "MaxNumChangesLastMonth";
	
	public TransactionCodeAgeMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		this.persistenceUtil = genealogy.getCore().getPersistenceUtil();
	}
	
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
	
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		final RCSTransaction transaction = item.getNode();
		final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(this.persistenceUtil,
		                                                                                               transaction);
		final DescriptiveStatistics lastModifiedStats = new DescriptiveStatistics();
		final DescriptiveStatistics ageStats = new DescriptiveStatistics();
		final DescriptiveStatistics numChangesStats = new DescriptiveStatistics();
		
		for (final JavaChangeOperation op : changeOperations) {
			
			final JavaElement element = op.getChangedElementLocation().getElement();
			
			final DateTime before = op.getRevision().getTransaction().getTimestamp();
			final DateTime after = before.minusDays(30);
			
			final List<RCSTransaction> pastTransactions = PPAPersistenceUtil.getTransactionsChangingElement(this.persistenceUtil,
			                                                                                                element,
			                                                                                                before,
			                                                                                                after);
			
			if (!pastTransactions.isEmpty()) {
				numChangesStats.addValue(pastTransactions.size());
				
				final RCSTransaction lastModified = pastTransactions.get(pastTransactions.size() - 1);
				lastModifiedStats.addValue(DaysBetweenUtils.getDaysBetween(lastModified, transaction));
			}
			final RCSTransaction firstModified = PPAPersistenceUtil.getFirstTransactionsChangingElement(this.persistenceUtil,
			                                                                                            element);
			if (!firstModified.equals(transaction)) {
				ageStats.addValue(DaysBetweenUtils.getDaysBetween(transaction, firstModified));
			}
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

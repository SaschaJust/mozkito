package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
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
			
			final Criteria<JavaElementLocation> pastLocationsCriteria = this.persistenceUtil.createCriteria(JavaElementLocation.class)
			                                                                                .eq("element", element);
			final List<JavaElementLocation> pastLocations = this.persistenceUtil.load(pastLocationsCriteria);
			
			final Criteria<JavaChangeOperation> pastOperationCriteria = this.persistenceUtil.createCriteria(JavaChangeOperation.class)
			                                                                                .in("changedElementLocation",
			                                                                                    pastLocations);
			final List<JavaChangeOperation> pastOperations = this.persistenceUtil.load(pastOperationCriteria);
			
			final TreeSet<RCSTransaction> pastTransactions = new TreeSet<RCSTransaction>();
			
			for (final JavaChangeOperation pastOperation : pastOperations) {
				final RCSTransaction pastTransaction = pastOperation.getRevision().getTransaction();
				if (pastTransaction.compareTo(transaction) < 0) {
					pastTransactions.add(transaction);
				}
			}
			
			final RCSTransaction firstModified = pastTransactions.first();
			ageStats.addValue(DaysBetweenUtils.getDaysBetween(firstModified, transaction));
			
			final RCSTransaction lastModified = pastTransactions.last();
			lastModifiedStats.addValue(DaysBetweenUtils.getDaysBetween(lastModified, transaction));
			
			int numPastChanges = 0;
			for (final RCSTransaction pastTransaction : pastTransactions) {
				final int daysbetween = DaysBetweenUtils.getDaysBetween(pastTransaction, transaction);
				if (daysbetween > 30) {
					break;
				}
				++numPastChanges;
			}
			numChangesStats.addValue(numPastChanges);
		}
		
		final Collection<GenealogyMetricValue> result = new HashSet<GenealogyMetricValue>();
		final String nodeId = item.getNodeId();
		
		result.add(new GenealogyMetricValue(avgLastModifiedName, nodeId, lastModifiedStats.getMean()));
		result.add(new GenealogyMetricValue(minLastModifiedName, nodeId, lastModifiedStats.getMin()));
		result.add(new GenealogyMetricValue(avgAgeName, nodeId, ageStats.getMean()));
		result.add(new GenealogyMetricValue(minAgeName, nodeId, ageStats.getMin()));
		result.add(new GenealogyMetricValue(maxAgeName, nodeId, ageStats.getMax()));
		result.add(new GenealogyMetricValue(avgNumChangesLastMonth, nodeId, lastModifiedStats.getMean()));
		result.add(new GenealogyMetricValue(maxNumChangesLastMonth, nodeId, lastModifiedStats.getMax()));
		
		return result;
	}
}

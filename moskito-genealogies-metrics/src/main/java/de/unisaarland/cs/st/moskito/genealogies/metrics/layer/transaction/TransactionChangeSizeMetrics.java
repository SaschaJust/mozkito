package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionChangeSizeMetrics extends GenealogyTransactionMetric{
	
	public static String changeSize = "changeSize";
	public static String avgDepChangeSize = "avgDepChangeSize";
	public static String maxDepChangeSize = "maxDepChangeSize";
	public static String sumDepChangeSize = "sumDepChangeSize";
	public static String avgParentChangeSize = "avgParentChangeSize";
	public static String maxParentChangeSize = "maxParentChangeSize";
	public static String sumParentChangeSize = "sumParentChangeSize";
	private PersistenceUtil persistenceUtil;
	
	public TransactionChangeSizeMetrics(TransactionChangeGenealogy genealogy, PersistenceUtil peristenceUtil) {
		super(genealogy);
		this.persistenceUtil = peristenceUtil;
	}
	
	@Override
	public Collection<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>(7);
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
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		RCSTransaction transaction = item.getNode();
		String nodeId = genealogy.getNodeId(transaction);
		
		DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		metricValues.add(new GenealogyMetricValue(changeSize, nodeId, PPAPersistenceUtil.getChangeOperation(
				persistenceUtil, transaction).size()));
		
		for (RCSTransaction dependant : genealogy.getAllDependants(transaction)) {
			dependantStats.addValue(PPAPersistenceUtil.getChangeOperation(persistenceUtil, dependant).size());
		}
		
		
		metricValues.add(new GenealogyMetricValue(avgDepChangeSize, nodeId, dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxDepChangeSize, nodeId, dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumDepChangeSize, nodeId, dependantStats.getSum()));
		
		for (RCSTransaction parent : genealogy.getAllParents(transaction)) {
			dependantStats.addValue(PPAPersistenceUtil.getChangeOperation(persistenceUtil, parent).size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgParentChangeSize, nodeId, parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentChangeSize, nodeId, parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumParentChangeSize, nodeId, parentStats.getSum()));
		
		return metricValues;
	}
	
}

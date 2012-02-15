package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionDependencyDiversityMetrics extends GenealogyTransactionMetric {
	
	private final static String avgParentsFileDiversityName    = "AvgParentsFileDiversity";
	private final static String maxParentsFileDiversityName    = "MaxParentsFileDiversity";
	private final static String minParentsFileDiversityName    = "MinParentsFileDiversity";
	
	private final static String avgDependantsFileDiversityName = "AvgDependantsFileDiversity";
	private final static String maxDependantsFileDiversityName = "MaxDependantsFileDiversity";
	private final static String minDependantsFileDiversityName = "MinDependantsFileDiversity";
	
	public TransactionDependencyDiversityMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		genealogy.getCore().getPersistenceUtil();
	}
	
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
	
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		final DescriptiveStatistics parentStat = new DescriptiveStatistics();
		final DescriptiveStatistics dependantStat = new DescriptiveStatistics();
		
		final Collection<RCSFile> changedFiles = item.getNode().getChangedFiles();
		
		for (final RCSTransaction parent : this.genealogy.getAllParents(item.getNode())) {
			final int intersectionSize = CollectionUtils.intersection(changedFiles, parent.getChangedFiles()).size();
			parentStat.addValue(1d - ((double) intersectionSize / (double) changedFiles.size()));
		}
		
		for (final RCSTransaction dependant : this.genealogy.getAllDependants(item.getNode())) {
			final int intersectionSize = CollectionUtils.intersection(changedFiles, dependant.getChangedFiles()).size();
			dependantStat.addValue(1d - ((double) intersectionSize / (double) changedFiles.size()));
		}
		
		final String nodeId = item.getNodeId();
		
		final Collection<GenealogyMetricValue> result = new HashSet<GenealogyMetricValue>();
		
		result.add(new GenealogyMetricValue(avgParentsFileDiversityName, nodeId, parentStat.getMean()));
		result.add(new GenealogyMetricValue(maxParentsFileDiversityName, nodeId, parentStat.getMax()));
		result.add(new GenealogyMetricValue(minParentsFileDiversityName, nodeId, parentStat.getMin()));
		
		result.add(new GenealogyMetricValue(avgDependantsFileDiversityName, nodeId, dependantStat.getMean()));
		result.add(new GenealogyMetricValue(maxDependantsFileDiversityName, nodeId, dependantStat.getMax()));
		result.add(new GenealogyMetricValue(minDependantsFileDiversityName, nodeId, dependantStat.getMin()));
		
		return result;
	}
}

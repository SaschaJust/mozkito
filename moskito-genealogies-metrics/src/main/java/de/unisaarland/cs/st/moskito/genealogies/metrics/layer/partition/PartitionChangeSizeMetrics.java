package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionChangeSizeMetrics extends GenealogyPartitionMetric {
	
	public static String changeSize = "changeSize";
	public static String avgDepChangeSize = "avgDepChangeSize";
	public static String maxDepChangeSize = "maxDepChangeSize";
	public static String sumDepChangeSize = "sumDepChangeSize";
	public static String avgParentChangeSize = "avgParentChangeSize";
	public static String maxParentChangeSize = "maxParentChangeSize";
	public static String sumParentChangeSize = "sumParentChangeSize";
	
	public PartitionChangeSizeMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings, genealogy);
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
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(7);
		
		Collection<JavaChangeOperation> partition = item.getNode();
		String nodeId = genealogy.getNodeId(partition);
		
		DescriptiveStatistics dependantStats = new DescriptiveStatistics();
		DescriptiveStatistics parentStats = new DescriptiveStatistics();
		
		metricValues.add(new GenealogyMetricValue(changeSize, nodeId, partition.size()));
		
		for (Collection<JavaChangeOperation> dependant : genealogy.getAllDependants(partition)) {
			dependantStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgDepChangeSize, nodeId, dependantStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxDepChangeSize, nodeId, dependantStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumDepChangeSize, nodeId, dependantStats.getSum()));
		
		for (Collection<JavaChangeOperation> dependant : genealogy.getAllParents(partition)) {
			parentStats.addValue(dependant.size());
		}
		
		metricValues.add(new GenealogyMetricValue(avgParentChangeSize, nodeId, parentStats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentChangeSize, nodeId, parentStats.getMax()));
		metricValues.add(new GenealogyMetricValue(sumParentChangeSize, nodeId, parentStats.getSum()));
		
		return metricValues;
	}
	
}

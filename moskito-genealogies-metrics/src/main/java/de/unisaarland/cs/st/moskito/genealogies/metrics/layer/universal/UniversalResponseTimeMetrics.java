package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalResponseTimeMetrics<T> {
	
	private ChangeGenealogy<T> genealogy;
	private DayTimeDiff<T>     dayComparator;
	private static String avgResponseTime = "avgResponseTime";
	private static String minResponseTime = "minResponseTime";
	private static String maxResponseTime = "maxResponseTime";
	
	public UniversalResponseTimeMetrics(ChangeGenealogy<T> genealogy, DayTimeDiff<T> dayComparator) {
		this.genealogy = genealogy;
		this.dayComparator = dayComparator;
	}
	
	
	
	public final Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(avgResponseTime);
		metricNames.add(minResponseTime);
		metricNames.add(maxResponseTime);
		return metricNames;
	}
	
	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	public final Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(3);
		
		String nodeId = genealogy.getNodeId(node);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (T dependant : genealogy.getAllDependants(node)) {
			stats.addValue(dayComparator.daysDiff(node, dependant));
		}
		
		metricValues.add(new GenealogyMetricValue(avgResponseTime, nodeId, stats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxResponseTime, nodeId, stats.getMax()));
		metricValues.add(new GenealogyMetricValue(minResponseTime, nodeId, stats.getMin()));
		
		return metricValues;
	}
	
}

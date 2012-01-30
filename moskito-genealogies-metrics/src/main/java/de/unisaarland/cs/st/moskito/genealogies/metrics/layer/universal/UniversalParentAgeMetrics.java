package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalParentAgeMetrics<T> {

	private final ChangeGenealogy<T> genealogy;
	private final DayTimeDiff<T>     dayComparator;
	private static String      avgParentAge = "avgParentAge";
	private static String      minParentAge = "minParentAge";
	private static String      maxParentAge = "maxParentAge";

	public static final Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(avgParentAge);
		metricNames.add(minParentAge);
		metricNames.add(maxParentAge);
		return metricNames;
	}



	public UniversalParentAgeMetrics(ChangeGenealogy<T> genealogy, DayTimeDiff<T> dayComparator) {
		this.genealogy = genealogy;
		this.dayComparator = dayComparator;
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

		for (T parent : genealogy.getAllParents(node)) {
			stats.addValue(dayComparator.daysDiff(node, parent));
		}

		metricValues.add(new GenealogyMetricValue(avgParentAge, nodeId, (stats
				.getN() < 1) ? 0 : stats.getMean()));
		metricValues.add(new GenealogyMetricValue(maxParentAge, nodeId, (stats
				.getN() < 1) ? 0 : stats.getMax()));
		metricValues.add(new GenealogyMetricValue(minParentAge, nodeId, (stats
				.getN() < 1) ? 0 : stats.getMin()));

		return metricValues;
	}

}

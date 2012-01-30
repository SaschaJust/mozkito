package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.JungGenealogyGraph;
import de.unisaarland.cs.st.moskito.genealogies.utils.JungGenealogyGraph.Edge;
import edu.uci.ics.jung.algorithms.scoring.PageRank;

/**
 * The Class UniversalPageRankMetric.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalPageRankMetric<T> {

	/** The page rank. */
	private static final String pageRankName = "pageRank";

	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(pageRankName);
		return metricNames;
	}

	/** The genealogy. */
	private final ChangeGenealogy<T> genealogy;
	private final PageRank<T, Edge<T>> pageRank;

	/**
	 * Instantiates a new universal page rank metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalPageRankMetric(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		JungGenealogyGraph<T> jungGraph = new JungGenealogyGraph<T>(genealogy);
		pageRank = new PageRank<T, JungGenealogyGraph.Edge<T>>(
				jungGraph, 0.1);
	}

	/**
	 * Handle.
	 * 
	 * @param node
	 *            the node
	 * @return the collection
	 */
	public Collection<GenealogyMetricValue> handle(final T node,
			final boolean finalNode) {
		Double vertexScore = pageRank.getVertexScore(node);
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		result.add(new GenealogyMetricValue(pageRankName, genealogy
				.getNodeId(node), vertexScore));
		return result;
	}
}

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.Tuple;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalPageRankMetric.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalPageRankMetric<T> {
	
	/** The page rank. */
	private static final String pageRank = "pageRank";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		final Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(pageRank);
		return metricNames;
	}
	
	/**
	 * @return
	 */
	public static String getPagerank() {
		return pageRank;
	}
	
	/** The tentative cache. */
	private final Map<String, Double> tentativeCache = new HashMap<String, Double>();
	
	/** The confirmed cache. */
	private final Map<String, Double> confirmedCache = new HashMap<String, Double>();
	
	/** The genealogy. */
	private final ChangeGenealogy<T>  genealogy;
	
	/** The to send. */
	private final Map<String, T>      delayed        = new HashMap<String, T>();
	
	/**
	 * Instantiates a new universal page rank metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalPageRankMetric(final ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		// for (T root : genealogy.getRoots()) {
		// computePageRank(root, new HashSet<T>());
		// }
	}
	
	/**
	 * Compute page rank.
	 * 
	 * @param node
	 *            the node
	 * @param seen
	 *            the seen
	 * @return the tuple
	 */
	private Tuple<Double, Boolean> computePageRank(final T node,
	                                               final Collection<T> seen) {
		
		final String nodeId = this.genealogy.getNodeId(node);
		if (this.confirmedCache.containsKey(nodeId)) {
			return new Tuple<Double, Boolean>(this.confirmedCache.get(nodeId), true);
		}
		
		double pageRank = 0d;
		
		boolean confirmed = true;
		
		final Collection<T> incoming = this.genealogy.getAllDependants(node);
		for (final T in : incoming) {
			Tuple<Double, Boolean> pageRankIn = null;
			double numOutIn = this.genealogy.getAllDependants(in).size();
			if (seen.contains(in)) {
				confirmed = false;
				if (!this.tentativeCache.containsKey(in)) {
					continue;
				}
				pageRankIn = new Tuple<Double, Boolean>(this.tentativeCache.get(in), false);
			} else {
				if (numOutIn < 1) {
					numOutIn = this.genealogy.vertexSize();
				}
				final Collection<T> seenCopy = new HashSet<T>(seen);
				seenCopy.add(in);
				pageRankIn = computePageRank(in, seenCopy);
			}
			pageRank += pageRankIn.getFirst() / numOutIn;
			confirmed &= pageRankIn.getSecond();
		}
		
		pageRank = 0.1 + (0.9 * pageRank);
		
		if (this.tentativeCache.containsKey(nodeId)) {
			if (this.tentativeCache.get(nodeId) == pageRank) {
				confirmed = true;
			}
		}
		
		if (confirmed) {
			this.tentativeCache.remove(nodeId);
			this.confirmedCache.put(nodeId, pageRank);
			// check if node is delayed!
		} else {
			this.tentativeCache.put(nodeId, pageRank);
		}
		return new Tuple<Double, Boolean>(pageRank, confirmed);
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
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		final String nodeId = this.genealogy.getNodeId(node);
		
		Tuple<Double, Boolean> pageRank = computePageRank(node, new HashSet<T>());
		if (pageRank.getSecond()) {
			metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, nodeId, pageRank.getFirst()));
		} else {
			this.delayed.put(nodeId, node);
		}
		
		final Set<String> sent = new HashSet<String>();
		for (final String delayId : this.delayed.keySet()) {
			if (this.confirmedCache.containsKey(delayId)) {
				metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, delayId,
				                                          this.confirmedCache.get(delayId)));
				sent.add(delayId);
			}
		}
		for (final String r : sent) {
			this.delayed.remove(r);
		}
		
		if (finalNode) {
			for (final String key : this.delayed.keySet()) {
				pageRank = computePageRank(this.delayed.get(key), new HashSet<T>());
				metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, key, pageRank.getFirst()));
			}
			
		}
		return metricValues;
	}
}

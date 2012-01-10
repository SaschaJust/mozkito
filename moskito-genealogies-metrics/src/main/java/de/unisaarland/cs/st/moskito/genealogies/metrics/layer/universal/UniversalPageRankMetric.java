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
	public static String        pageRank       = "pageRank";
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public static Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(pageRank);
		return metricNames;
	}
	
	/** The tentative cache. */
	private Map<String, Double> tentativeCache = new HashMap<String, Double>();
	
	/** The confirmed cache. */
	private Map<String, Double> confirmedCache = new HashMap<String, Double>();
	
	/** The genealogy. */
	private ChangeGenealogy<T>  genealogy;
	
	/** The to send. */
	private Map<String, T>      delayed        = new HashMap<String, T>();
	
	/**
	 * Instantiates a new universal page rank metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalPageRankMetric(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		for (T root : genealogy.getRoots()) {
			computePageRank(root, new HashSet<T>());
		}
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
	private Tuple<Double, Boolean> computePageRank(T node, Collection<T> seen) {
		
		String nodeId = genealogy.getNodeId(node);
		if (confirmedCache.containsKey(nodeId)) {
			return new Tuple<Double, Boolean>(confirmedCache.get(nodeId), true);
		}
		
		double pageRank = 0d;
		
		boolean confirmed = true;
		
		Collection<T> incoming = genealogy.getAllDependants(node);
		for (T in : incoming) {
			Tuple<Double, Boolean> pageRankIn = null;
			double numOutIn = genealogy.getAllDependants(in).size();
			if (seen.contains(in)) {
				confirmed = false;
				if (!tentativeCache.containsKey(in)) {
					continue;
				}
				pageRankIn = new Tuple<Double, Boolean>(tentativeCache.get(in), false);
			} else {
				if (numOutIn < 1) {
					numOutIn = genealogy.vertexSize();
				}
				Collection<T> seenCopy = new HashSet<T>(seen);
				seenCopy.add(in);
				pageRankIn = computePageRank(in, seenCopy);
			}
			pageRank += pageRankIn.getFirst() / numOutIn;
			confirmed &= pageRankIn.getSecond();
		}
		
		pageRank = 0.1 + (0.9 * pageRank);
		
		if (tentativeCache.containsKey(nodeId)) {
			if (tentativeCache.get(nodeId) == pageRank) {
				confirmed = true;
			}
		}
		
		if (confirmed) {
			tentativeCache.remove(nodeId);
			confirmedCache.put(nodeId, pageRank);
			//check if node is delayed!
		} else {
			tentativeCache.put(nodeId, pageRank);
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
	public Collection<GenealogyMetricValue> handle(T node, boolean finalNode) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		String nodeId = genealogy.getNodeId(node);
		
		Tuple<Double, Boolean> pageRank = computePageRank(node, new HashSet<T>());
		if (pageRank.getSecond()) {
			metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, nodeId, pageRank.getFirst()));
		} else {
			delayed.put(nodeId,node);
		}
		
		Set<String> sent = new HashSet<String>();
		for (String delayId : delayed.keySet()) {
			if (confirmedCache.containsKey(delayId)) {
				metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, delayId, confirmedCache
						.get(delayId)));
				sent.add(delayId);
			}
		}
		for(String r : sent){
			delayed.remove(r);
		}
		
		if (finalNode) {
			for (String key : delayed.keySet()) {
				pageRank = computePageRank(delayed.get(key), new HashSet<T>());
				metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, key, pageRank.getFirst()));
			}
			
		}
		return metricValues;
	}
}

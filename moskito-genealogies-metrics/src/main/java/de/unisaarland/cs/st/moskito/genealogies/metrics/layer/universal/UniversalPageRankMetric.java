package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.Tuple;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.PageRankCache;

/**
 * The Class UniversalPageRankMetric.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalPageRankMetric<T> {
	
	//TODO requires intensive testing
	
	/** The page rank. */
	private static String      pageRank     = "pageRank";
	
	/** The tentative cache. */
	private PageRankCache      tentativeCache;
	
	/** The confirmed cache. */
	private PageRankCache      confirmedCache;
	
	/** The genealogy. */
	private ChangeGenealogy<T> genealogy;
	
	/** The delayed. */
	private Map<String, T>     delayed  = new HashMap<String, T>();
	
	/** The to send. */
	private Set<GenealogyMetricValue> toSend   = new HashSet<GenealogyMetricValue>();
	
	/**
	 * Instantiates a new universal page rank metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public UniversalPageRankMetric(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			tentativeCache = new PageRankCache(digest);
			confirmedCache = new PageRankCache(digest);
			
			for (T root : genealogy.getRoots()) {
				computePageRank(root, new HashSet<T>());
			}
			
		} catch (NoSuchAlgorithmException e) {
			throw new UnrecoverableError(e);
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
		if (confirmedCache.containsPageRank(nodeId)) {
			return new Tuple<Double, Boolean>(confirmedCache.getPageRank(nodeId), true);
		}
		
		double pageRank = 0d;
		
		boolean confirmed = true;
		
		Collection<T> incoming = genealogy.getAllDependants(node);
		seen.add(node);
		for (T in : incoming) {
			if (seen.contains(in)) {
				continue;
			}
			double numOutIn = genealogy.getAllParents(in).size();
			Tuple<Double, Boolean> pageRankIn = computePageRank(node, seen);
			pageRank += pageRankIn.getFirst() / numOutIn;
			confirmed &= pageRankIn.getSecond();
		}
		
		pageRank = 0.1 + (0.9 * pageRank);
		
		if(tentativeCache.containsPageRank(nodeId)){
			if (tentativeCache.getPageRank(nodeId) == pageRank) {
				confirmed = true;
			}
		}
		
		if (confirmed) {
			tentativeCache.removePageRank(nodeId);
			confirmedCache.putPageRank(nodeId, pageRank);
			//check if node is delayed!
			if (delayed.containsKey(nodeId)) {
				toSend.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, nodeId, pageRank));
				delayed.remove(nodeId);
			}
		} else {
			tentativeCache.putPageRank(nodeId, pageRank);
		}
		return new Tuple<Double, Boolean>(pageRank, confirmed);
	}
	
	/**
	 * Gets the metric names.
	 * 
	 * @return the metric names
	 */
	public Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(pageRank);
		return metricNames;
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
			delayed.put(nodeId, node);
		}
		
		metricValues.addAll(toSend);
		toSend.clear();
		
		if (finalNode) {
			for (String delayedNodeId : delayed.keySet()) {
				Double delayedPageRank = tentativeCache.getPageRank(delayedNodeId);
				if (delayedPageRank != null) {
					metricValues.add(new GenealogyMetricValue(UniversalPageRankMetric.pageRank, delayedNodeId,
							delayedPageRank));
				}
			}
		}
		return metricValues;
	}
}

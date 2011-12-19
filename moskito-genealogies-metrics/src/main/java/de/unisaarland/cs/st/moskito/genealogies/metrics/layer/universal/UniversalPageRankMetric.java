package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.PageRankCache;

public class UniversalPageRankMetric<T> {
	
	//TODO requires intensive testing
	
	private static String      pageRank     = "pageRank";
	private PageRankCache      cache;
	private ChangeGenealogy<T> genealogy;
	
	public UniversalPageRankMetric(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			cache = new PageRankCache(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	private double computePageRank(T node, Collection<T> seen) {
		//TODO detect cycles
		String nodeId = genealogy.getNodeId(node);
		if (cache.containsPageRank(nodeId)) {
			return cache.getPageRank(nodeId);
		}
		
		double pageRank = 0d;
		
		Collection<T> incoming = genealogy.getAllDependants(node);
		seen.add(node);
		for (T in : incoming) {
			if (seen.contains(in)) {
				continue;
			}
			double numOutIn = genealogy.getAllParents(in).size();
			double pageRankIn = computePageRank(node, seen);
			pageRank += pageRankIn / numOutIn;
		}
		
		return 0.1 + (0.9 * pageRank);
	}
	
	public Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(pageRank);
		return metricNames;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		return metricValues;
	}
	
}

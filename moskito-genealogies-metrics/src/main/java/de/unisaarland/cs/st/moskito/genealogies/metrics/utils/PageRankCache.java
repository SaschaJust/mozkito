package de.unisaarland.cs.st.moskito.genealogies.metrics.utils;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;


public class PageRankCache {
	
	private MessageDigest digest;
	private Map<byte[], Double> pageRanks = new HashMap<byte[], Double>();
	
	public PageRankCache(MessageDigest digest) {
		this.digest = digest;
	}
	
	public boolean containsPageRank(String nodeId){
		return pageRanks.containsKey(hash(nodeId));
	}
	
	public Double getPageRank(String nodeId) {
		return pageRanks.get(hash(nodeId));
	}
	
	public byte[] hash(String input) {
		digest.reset();
		digest.update(input.getBytes());
		return digest.digest();
	}
	
}

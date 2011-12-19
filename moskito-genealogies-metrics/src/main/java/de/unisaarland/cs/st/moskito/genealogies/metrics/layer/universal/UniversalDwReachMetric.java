package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntListIterator;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DwReachCache;

public class UniversalDwReachMetric<T> {
	
	//TODO requires intensive testing
	
	private static String      dwReach      = "dwReach";
	
	private boolean            traversed   = false;
	private DwReachCache       cache;
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalDwReachMetric(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			cache = new DwReachCache(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	private double computeDwReach(String nodeId) {
		ArrayIntList reach = cache.getReach(nodeId);
		IntListIterator iterator = reach.listIterator();
		int i = 0;
		double result = 0d;
		while (iterator.hasNext()) {
			++i;
			result += (((double) iterator.next()) / ((double) i));
		}
		return result;
	}
	
	private void dwReachVisit(Collection<T> nodes, Collection<T> nextNodes) {
		
		if (nodes.isEmpty()) {
			if (nextNodes.isEmpty()) {
				return;
			}
			nodes = nextNodes;
			nextNodes = new LinkedList<T>();
			//increase depth for all cache entries
			cache.newRound();
		}
		
		for (T node : nodes) {
			String nodeId = genealogy.getNodeId(node);
			byte[] nodeMD5 = cache.hash(nodeId);
			Collection<T> children = genealogy.getAllDependants(node);
			Set<byte[]> childrenMD5 = new HashSet<byte[]>();
			for(T child : children){
				byte[] childMD5 = cache.hash(genealogy.getNodeId(child));
				childrenMD5.add(childMD5);
				cache.addUpdateHook(childMD5, nodeMD5);
			}
			cache.addSeen(nodeMD5, childrenMD5);
			cache.addNumChildren(nodeMD5, children.size());
			
			//process update hooks
			Set<byte[]> updateHooks = cache.getUpdateHooks(nodeMD5);
			for(byte[] hook : updateHooks){
				@SuppressWarnings("unchecked") Collection<byte[]> relevantChildren = CollectionUtils.subtract(
						childrenMD5, cache.getSeen(hook));
				cache.addNumChildren(hook, relevantChildren.size());
				for (byte[] relevantChild : relevantChildren) {
					cache.addUpdateHook(relevantChild, hook);
				}
			}
			//clear update hooks for this node
			cache.clearUpdateHooksFor(nodeMD5);
			
			//add children for next round
			nextNodes.addAll(children);
		}
	}
	
	public Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(dwReach);
		return metricNames;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		if (!traversed) {
			Collection<T> roots = genealogy.getRoots();
			dwReachVisit(roots, new LinkedList<T>());
			traversed = true;
		}
		
		String nodeId = genealogy.getNodeId(node);
		
		metricValues.add(new GenealogyMetricValue(dwReach, nodeId, computeDwReach(nodeId)));
		
		return metricValues;
	}
	
}

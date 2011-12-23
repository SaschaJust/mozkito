package de.unisaarland.cs.st.moskito.genealogies.metrics.utils;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.primitives.ArrayIntList;

public class DwReachCache {
	
	private class CacheContainer {
		
		private Set<byte[]>  seen;
		private ArrayIntList reach;
		private Set<byte[]>  updateHooks;
		private int          depth;
		
		public CacheContainer(Set<byte[]> seen, ArrayIntList reach, Set<byte[]> updateHooks) {
			this.seen = seen;
			this.reach = reach;
			this.updateHooks = updateHooks;
			this.depth = 0;
		}
		
		public void clearUpdateHooks() {
			updateHooks.clear();
		}
		
		public int getDepth() {
			return this.depth;
		}
		
		public ArrayIntList getReach() {
			return reach;
		}
		
		public Set<byte[]> getSeen() {
			return seen;
		}
		
		public Set<byte[]> getUpdateHooks() {
			return updateHooks;
		}
		
		public void increaseDepth() {
			++depth;
		}
	}
	
	private Map<byte[], CacheContainer> cache = new HashMap<byte[], CacheContainer>();
	private MessageDigest               digest;
	private Set<byte[]>                 hooksUpdated = new HashSet<byte[]>();
	
	public DwReachCache(MessageDigest digest) {
		this.digest = digest;
	}
	
	public void addNumChildren(byte[] hash, int size) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		CacheContainer container = cache.get(hash);
		int depth = container.getDepth();
		ArrayIntList reach = container.getReach();
		if (reach.size() > depth) {
			reach.set(depth, reach.get(depth) + size);
		} else {
			if (reach.size() < depth) {
				throw new UnrecoverableError("reach.size() < depth");
			}
			reach.add(size);
		}
	}
	
	public void addSeen(byte[] node, Set<byte[]> seenNodes) {
		getSeen(node).addAll(seenNodes);
	}
	
	public void addUpdateHook(byte[] trigger, byte[] target) {
		Set<byte[]> updateHooks = getUpdateHooks(trigger);
		updateHooks.add(target);
		hooksUpdated.add(trigger);
		hooksUpdated.add(target);
	}
	
	public void clearUpdateHooksFor(byte[] hash) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		cache.get(hash).clearUpdateHooks();
	}
	
	public boolean contains(byte[] hash){
		return cache.containsKey(hash);
	}
	
	public int getCurrentDepth(byte[] hash) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		return cache.get(hash).getDepth();
	}
	
	public ArrayIntList getReach(byte[] hash) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		return cache.get(hash).getReach();
	}
	
	public ArrayIntList getReach(String nodeId) {
		return getReach(hash(nodeId));
	}
	
	public Set<byte[]> getSeen(byte[] hash) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		return cache.get(hash).getSeen();
	}
	
	public Set<byte[]> getSeen(String nodeId) {
		return getSeen(hash(nodeId));
	}
	
	public Set<byte[]> getUpdateHooks(byte[] hash) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		return cache.get(hash).getUpdateHooks();
	}
	
	public Set<byte[]> getUpdateHooks(String nodeId) {
		return getUpdateHooks(hash(nodeId));
	}
	
	public void globallyIncreaseDepth() {
		for (byte[] hash : cache.keySet()) {
			increaseDepth(hash);
		}
	}
	
	public byte[] hash(String input) {
		digest.reset();
		digest.update(input.getBytes());
		return digest.digest();
	}
	
	public void increaseDepth(byte[] hash) {
		if (!cache.containsKey(hash)) {
			initCache(hash);
		}
		cache.get(hash).increaseDepth();
	}
	
	private void initCache(byte[] hash) {
		CacheContainer triple = new CacheContainer(new HashSet<byte[]>(), new ArrayIntList(), new HashSet<byte[]>());
		cache.put(hash, triple);
	}
	
	public void newRound() {
		globallyIncreaseDepth();
		@SuppressWarnings("unchecked") Collection<byte[]> noNewHooks = CollectionUtils.subtract(cache.keySet(),
		        hooksUpdated);
		for (byte[] node : noNewHooks) {
			cache.get(node).getSeen().clear();
		}
		hooksUpdated.clear();
	}

}

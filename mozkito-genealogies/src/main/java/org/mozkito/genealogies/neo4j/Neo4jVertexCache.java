/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.genealogies.neo4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;

/**
 * The Class Neo4jVertexCache.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Neo4jVertexCache {
	
	/**
	 * The Class Neo4jVertexCacheEntry.
	 */
	private class Neo4jVertexCacheEntry implements Comparable<Neo4jVertexCacheEntry> {
		
		/** The last acccess. */
		private DateTime   lastAcccess;
		
		/** The nodeid. */
		private final Long nodeid;
		
		/** The node. */
		private final Node node;
		
		/**
		 * Instantiates a new neo4j vertex cache entry.
		 * 
		 * @param nodeid
		 *            the nodeid
		 * @param node
		 *            the node
		 */
		public Neo4jVertexCacheEntry(final long nodeid, final Node node) {
			this.nodeid = nodeid;
			this.lastAcccess = new DateTime();
			this.node = node;
		}
		
		/**
		 * Access.
		 */
		public void access() {
			this.lastAcccess = new DateTime();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final Neo4jVertexCacheEntry o) {
			if (this.lastAcccess.isAfter(o.lastAcccess)) {
				return 1;
			} else if (this.lastAcccess.isBefore(o.lastAcccess)) {
				return -1;
			}
			return 0;
		}
		
		/**
		 * Gets the node.
		 * 
		 * @return the node
		 */
		public Node getNode() {
			access();
			return this.node;
		}
		
		/**
		 * Gets the node id.
		 * 
		 * @return the node id
		 */
		public long getNodeId() {
			access();
			return this.nodeid;
		}
		
	}
	
	/** The node index. */
	private final Index<Node>                      nodeIndex;
	
	/** The cache. */
	private final Map<Long, Neo4jVertexCacheEntry> cache = new HashMap<>();
	
	/**
	 * Instantiates a new neo4j vertex cache.
	 * 
	 * @param nodeIndex
	 *            the node index
	 */
	public Neo4jVertexCache(final Index<Node> nodeIndex) {
		this.nodeIndex = nodeIndex;
	}
	
	/**
	 * Gets the node.
	 * 
	 * @param op
	 *            the op
	 * @return the node
	 */
	public Node getNode(final JavaChangeOperation op) {
		final Neo4jVertexCacheEntry cacheEntry = this.cache.get(op.getId());
		if (cacheEntry != null) {
			return cacheEntry.getNode();
		}
		final IndexHits<Node> indexHits = this.nodeIndex.query(CoreChangeGenealogy.NODE_ID, op.getId());
		if (!indexHits.hasNext()) {
			indexHits.close();
			return null;
		}
		final Node node = indexHits.next();
		this.cache.put(op.getId(), new Neo4jVertexCacheEntry(op.getId(), node));
		indexHits.close();
		// TODO make cache size configurable
		if (this.cache.size() > 25000) {
			final List<Neo4jVertexCacheEntry> cacheList = new ArrayList<>(this.cache.size());
			cacheList.addAll(this.cache.values());
			Collections.sort(cacheList);
			final int deleteIndex = cacheList.size() / 2;
			for (int i = 0; i < deleteIndex; ++i) {
				this.cache.remove(cacheList.get(i).getNodeId());
			}
		}
		
		return node;
	}
}

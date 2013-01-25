/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.genealogies.neo4j;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import org.mozkito.genealogies.core.CoreChangeGenealogy;

/**
 * The Class Neo4jRootCache.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Neo4jRootCache implements Iterable<Node> {
	
	/** The cache. */
	private final Set<Node>            cache = new HashSet<>();
	
	/** The graph. */
	private final GraphDatabaseService graph;
	
	/** The root index. */
	private final Index<Node>          rootIndex;
	
	/**
	 * Instantiates a new neo4j root cache.
	 * 
	 * @param graph
	 *            the graph
	 */
	public Neo4jRootCache(final GraphDatabaseService graph) {
		this.graph = graph;
		this.rootIndex = graph.index().forNodes(CoreChangeGenealogy.ROOT_VERTICES);
		final IndexHits<Node> indexHits = this.rootIndex.query(CoreChangeGenealogy.ROOT_VERTICES, 1);
		while (indexHits.hasNext()) {
			this.cache.add(indexHits.next());
		}
		indexHits.close();
	}
	
	/**
	 * Adds the.
	 * 
	 * @param node
	 *            the node
	 */
	public void add(final Node node) {
		this.rootIndex.add(node, CoreChangeGenealogy.ROOT_VERTICES, 1);
		this.cache.add(node);
	}
	
	/**
	 * Checks if is root.
	 * 
	 * @param node
	 *            the node
	 * @return true, if is root
	 */
	public boolean isRoot(final Node node) {
		// PRECONDITIONS
		
		try {
			return this.cache.contains(node);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Node> iterator() {
		// PRECONDITIONS
		
		try {
			return this.cache.iterator();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Removes the.
	 * 
	 * @param node
	 *            the node
	 */
	public void remove(final Node node) {
		final Transaction tx2 = this.graph.beginTx();
		this.rootIndex.remove(node, CoreChangeGenealogy.ROOT_VERTICES);
		tx2.success();
		tx2.finish();
		this.cache.remove(node);
	}
	
}

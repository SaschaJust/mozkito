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
package de.unisaarland.cs.st.moskito.genealogies.neo4j;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class Neo4jRootCache implements Iterable<Node> {
	
	private final Set<Node>            cache = new HashSet<>();
	private final GraphDatabaseService graph;
	private final Index<Node>          rootIndex;
	
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
	 * @param node
	 */
	public void add(final Node node) {
		this.rootIndex.add(node, CoreChangeGenealogy.ROOT_VERTICES, 1);
		this.cache.add(node);
	}
	
	/**
	 * @param node
	 * @return
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
	 * @param node
	 */
	public void remove(final Node node) {
		final Transaction tx2 = this.graph.beginTx();
		this.rootIndex.remove(node, CoreChangeGenealogy.ROOT_VERTICES);
		tx2.success();
		tx2.finish();
		this.cache.remove(node);
	}
	
}

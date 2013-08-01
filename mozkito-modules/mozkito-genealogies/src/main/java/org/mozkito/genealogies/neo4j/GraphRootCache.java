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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;

import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.graphs.GraphIndex;
import org.mozkito.graphs.GraphManager;

/**
 * The Class Neo4jRootCache.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class GraphRootCache implements Iterable<Vertex> {
	
	/** The cache. */
	private final Set<Vertex> cache = new HashSet<>();
	
	/** The graph. */
	private final Graph       graph;
	
	/**
	 * Instantiates a new neo4j root cache.
	 * 
	 * @param graph
	 *            the graph
	 */
	public GraphRootCache(final GraphManager graphManager) {
		graphManager.createIndex(new GraphIndex().name(CoreChangeGenealogy.ROOT_VERTICES).dataType(Boolean.class)
		                                         .targetType(Vertex.class).unique(Direction.BOTH));
		final GraphQuery query = this.graph.query();
		final Iterable<Vertex> vertices = query.has(CoreChangeGenealogy.ROOT_VERTICES, 1).vertices();
		
		for (final Vertex vertex : vertices) {
			this.cache.add(vertex);
		}
		
		this.graph = this.graph;
	}
	
	/**
	 * Adds the.
	 * 
	 * @param vertex
	 *            the vertex
	 */
	public void add(final Vertex vertex) {
		this.graph.addVertex(vertex.getId());
		this.cache.add(vertex);
	}
	
	/**
	 * Checks if is root.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if is root
	 */
	public boolean isRoot(final Vertex vertex) {
		// PRECONDITIONS
		
		try {
			return this.cache.contains(vertex);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Vertex> iterator() {
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
	 * @param vertex
	 *            the vertex
	 */
	public void remove(final Vertex vertex) {
		this.graph.removeVertex(vertex);
		this.cache.remove(vertex);
	}
	
}

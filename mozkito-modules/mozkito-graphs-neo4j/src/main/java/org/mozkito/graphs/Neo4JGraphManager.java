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

package org.mozkito.graphs;

import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import org.mozkito.graphs.GraphEnvironment;
import org.mozkito.graphs.GraphIndex;
import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.GraphType;

/**
 * The Class GraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Neo4JGraphManager extends GraphManager {
	
	/** The graph. */
	private Neo4jGraph                           graph;
	
	/** The database service. */
	private GraphDatabaseService                 databaseService;
	
	private final Map<GraphIndex, Index<Vertex>> vertexIndexes = new HashMap<GraphIndex, Index<Vertex>>();
	private final Map<GraphIndex, Index<Edge>>   edgeIndexes   = new HashMap<GraphIndex, Index<Edge>>();
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#createIndex(org.mozkito.graphs.GraphIndex)
	 */
	@Override
	public void createIndex(final GraphIndex graphIndex) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// IndexManager indexManager = databaseService.index();
			
			if (graphIndex.getTargetType() == Edge.class) {
				final Index<Edge> index = this.graph.createIndex(graphIndex.getFieldName(), Edge.class);
				this.edgeIndexes.put(graphIndex, index);
			} else if (graphIndex.getTargetType() == Vertex.class) {
				final Index<Vertex> index = this.graph.createIndex(graphIndex.getFieldName(), Vertex.class);
				this.vertexIndexes.put(graphIndex, index);
			} else {
				// TODO error
			}
			
			// Index<Vertex> index = databaseService.createIndex("myIdx", Vertex.class, new Parameter("analyzer",
			// LowerCaseKeywordAnalyzer.class.getName()));
			// Vertex a = graph.addVertex(null);
			// a.setProperty("name", "marko");
			// index.put("name", "marko", a);
			// Iterator itty = graph.getIndex("myIdx", Vertex.class).query("name", "MaRkO").iterator();
			// assertEquals(itty.next(), a);
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Creates the key index.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param key
	 *            the key
	 * @param elementClass
	 *            the element class
	 * @param indexParameters
	 *            the index parameters
	 */
	@Override
	public <T extends Element> void createKeyIndex(final String key,
	                                               final Class<T> elementClass,
	                                               final Parameter<?, ?>... indexParameters) {
		this.graph.createKeyIndex(key, elementClass, indexParameters);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#createUtil(org.mozkito.graphs.GraphEnvironment)
	 */
	@Override
	public Neo4jGraph createUtil(@NotNull final GraphEnvironment environment) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.databaseService = new GraphDatabaseFactory().newEmbeddedDatabase(environment.getDirectory()
			                                                                                 .getAbsolutePath());
			this.graph = new Neo4jGraph(this.databaseService);
			return this.graph;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#getGraph()
	 */
	@Override
	public Neo4jGraph getGraph() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.graph;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#provides()
	 */
	@Override
	public GraphType provides() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return GraphType.NEO4J;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

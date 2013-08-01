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

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanKey;
import com.thinkaurelius.titan.core.TitanLabel;
import com.thinkaurelius.titan.core.TypeMaker;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.mozkito.graphs.GraphEnvironment;
import org.mozkito.graphs.GraphIndex;
import org.mozkito.graphs.GraphManager;
import org.mozkito.graphs.GraphType;

/**
 * The Class GraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TitanGraphManager extends GraphManager {
	
	/** The Constant INDEX_NAME. */
	public static final String                       INDEX_NAME = "search";
	
	/** The Constant keyMap. */
	private static final Map<GraphIndex, TitanKey>   keyMap     = new HashMap<>();
	
	/** The Constant labelMap. */
	private static final Map<GraphIndex, TitanLabel> labelMap   = new HashMap<>();
	
	/** The graph. */
	private TitanGraph                               graph;
	
	/**
	 * Creates the edge index.
	 * 
	 * @param graphIndex
	 *            the graph index
	 */
	private void createEdgeIndex(@NotNull final GraphIndex graphIndex) {
		PRECONDITIONS: {
			
			assert graphIndex.getTargetType() != null;
			assert graphIndex.getTargetType() == Edge.class;
		}
		
		TypeMaker titanType = this.graph.makeType();
		titanType = titanType.name(graphIndex.getFieldName());
		titanType = titanType.dataType(graphIndex.getDataType());
		
		if (graphIndex.getIndexName() != null) {
			titanType = titanType.indexed(graphIndex.getIndexName(), Edge.class);
		} else {
			titanType = titanType.indexed(Edge.class);
		}
		
		if (graphIndex.uniqueness() != null) {
			titanType = titanType.unique(graphIndex.uniqueness());
		}
		
		if (graphIndex.getPrimaryKey() != null) {
			final GraphIndex[] primaryKey = graphIndex.getPrimaryKey();
			final TitanKey[] titanKeys = new TitanKey[primaryKey.length];
			
			int i = 0;
			for (final GraphIndex pKey : primaryKey) {
				if (!keyMap.containsKey(pKey)) {
					// TODO error
				}
				titanKeys[i++] = keyMap.get(pKey);
			}
			titanType.primaryKey(titanKeys);
		}
		
		if (graphIndex.getSignature() != null) {
			final GraphIndex[] signature = graphIndex.getSignature();
			final TitanKey[] titanKeys = new TitanKey[signature.length];
			
			int i = 0;
			for (final GraphIndex sKey : signature) {
				if (!keyMap.containsKey(sKey)) {
					// TODO error
				}
				titanKeys[i++] = keyMap.get(sKey);
			}
			titanType.signature(titanKeys);
		}
		
		final TitanLabel edgeLabel = titanType.makeEdgeLabel();
		
		labelMap.put(graphIndex, edgeLabel);
	}
	
	/**
	 * Creates the index.
	 * 
	 * @param graphIndex
	 *            the graph index
	 */
	@Override
	public void createIndex(@NotNull final GraphIndex graphIndex) {
		PRECONDITIONS: {
			if (graphIndex.getTargetType() == null) {
				// TODO error
			}
			
			if (graphIndex.getFieldName() == null) {
				// TODO error
			}
			
			if (graphIndex.getDataType() == null) {
				// TODO error
			}
		}
		
		if (Vertex.class.equals(graphIndex.getTargetType())) {
			createVertexIndex(graphIndex);
		} else if (Edge.class.equals(graphIndex.getTargetType())) {
			createEdgeIndex(graphIndex);
		} else {
			// TODO error unsupported index type -- this should never happen
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.utils.graph.GraphManager#createKeyIndex(java.lang.String, java.lang.Class,
	 *      com.tinkerpop.blueprints.Parameter<?,?>[])
	 */
	@Override
	public <T extends Element> void createKeyIndex(final String key,
	                                               final Class<T> elementClass,
	                                               final Parameter<?, ?>... indexParameters) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.graph.createKeyIndex(key, elementClass, indexParameters);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Creates the util.
	 * 
	 * @param environment
	 *            the environment
	 * @return the graph
	 */
	@Override
	public final TitanGraph createUtil(@NotNull final GraphEnvironment environment) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			SANITY: {
				Condition.notNull(environment,
				                  "Argument '%s' in '%s'.", "environment", TitanGraphManager.class.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				if (!GraphType.TITANDB.equals(environment.getType())) {
					// TODO error
					throw new RuntimeException();
				}
			}
			
			final BaseConfiguration config = new BaseConfiguration();
			final Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
			// configuring local backend
			storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
			storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, environment.getDirectory()
			                                                                                 .getAbsolutePath());
			// configuring elastic search index
			final Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
			index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
			index.setProperty("local-mode", true);
			index.setProperty("client-only", false);
			index.setProperty(STORAGE_DIRECTORY_KEY, environment.getDirectory().getAbsolutePath() + File.separator
			        + "es");
			
			this.graph = TitanFactory.open(config);
			return this.graph;
		} finally {
			assert this.graph != null;
		}
	}
	
	/**
	 * Creates the vertex index.
	 * 
	 * @param graphIndex
	 *            the graph index
	 */
	private void createVertexIndex(@NotNull final GraphIndex graphIndex) {
		
		PRECONDITIONS: {
			
			assert graphIndex.getTargetType() != null;
			assert graphIndex.getTargetType() == Vertex.class;
		}
		
		TypeMaker titanType = this.graph.makeType();
		titanType = titanType.name(graphIndex.getFieldName());
		titanType = titanType.dataType(graphIndex.getDataType());
		
		if (graphIndex.getIndexName() != null) {
			titanType = titanType.indexed(graphIndex.getIndexName(), Edge.class);
		} else {
			titanType = titanType.indexed(Edge.class);
		}
		
		if (graphIndex.uniqueness() != null) {
			titanType = titanType.unique(graphIndex.uniqueness());
		}
		
		if (graphIndex.getPrimaryKey() != null) {
			final GraphIndex[] primaryKey = graphIndex.getPrimaryKey();
			final TitanKey[] titanKeys = new TitanKey[primaryKey.length];
			
			int i = 0;
			for (final GraphIndex pKey : primaryKey) {
				if (!keyMap.containsKey(pKey)) {
					// TODO error
				}
				titanKeys[i++] = keyMap.get(pKey);
			}
			titanType.primaryKey(titanKeys);
		}
		
		if (graphIndex.getSignature() != null) {
			final GraphIndex[] signature = graphIndex.getSignature();
			final TitanKey[] titanKeys = new TitanKey[signature.length];
			
			int i = 0;
			for (final GraphIndex sKey : signature) {
				if (!keyMap.containsKey(sKey)) {
					// TODO error
				}
				titanKeys[i++] = keyMap.get(sKey);
			}
			titanType.signature(titanKeys);
		}
		
		final TitanKey titanKey = titanType.makePropertyKey();
		
		keyMap.put(graphIndex, titanKey);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.graphs.GraphManager#getGraph()
	 */
	@Override
	public TitanGraph getGraph() {
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
		return GraphType.TITANDB;
	}
}

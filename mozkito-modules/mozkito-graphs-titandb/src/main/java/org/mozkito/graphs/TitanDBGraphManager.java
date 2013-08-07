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

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * The Class GraphManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TitanDBGraphManager extends LocalFileDBGraphManager {
	
	/** The Constant INDEX_NAME. */
	public static final String                       INDEX_NAME = "search";
	
	/** The Constant keyMap. */
	private static final Map<GraphIndex, TitanKey>   keyMap     = new HashMap<>();
	
	/** The Constant labelMap. */
	private static final Map<GraphIndex, TitanLabel> labelMap   = new HashMap<>();
	
	/** The graph. */
	private TitanGraph                               graph;
	
	/** The index. */
	private Configuration                            index;
	
	/** The config. */
	private BaseConfiguration                        config;
	
	/** The storage. */
	private Configuration                            storage;
	
	/**
	 * Instantiates a new titan db graph manager.
	 */
	TitanDBGraphManager() {
		// used only in reflections
	}
	
	/**
	 * Instantiates a new titan db graph manager.
	 * 
	 * @param directory
	 *            the directory
	 */
	public TitanDBGraphManager(@NotNull final File directory) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			setDirectory(directory);
			this.config = new BaseConfiguration();
			this.storage = this.config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
			// configuring local backend
			this.storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
			this.storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, directory.getAbsolutePath());
			this.index = this.storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
			this.index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
			this.index.setProperty("local-mode", true);
			this.index.setProperty("client-only", false);
			this.index.setProperty(STORAGE_DIRECTORY_KEY, directory.getAbsolutePath() + File.separator + "es");
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
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
	 * @return the graph
	 */
	@Override
	public final TitanGraph createUtil() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.graph = TitanFactory.open(this.config);
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
	@SuppressWarnings ("unchecked")
	@Override
	public TitanGraph getGraph() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (this.graph == null) {
				createUtil();
			}
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

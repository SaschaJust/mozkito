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

package org.mozkito.mappings.utils.graph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.graphs.GraphIndex;
import org.mozkito.graphs.GraphManager;
import org.mozkito.mappings.mappable.model.MappableChangeSet;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.persistence.model.Artifact;

/**
 * The Class MappingsGraph.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MappingsGraph {
	
	/** The Constant TYPE_KEY. */
	public static final String TYPE_KEY    = "type";
	
	/** The Constant ID_KEY. */
	public static final String ID_KEY      = "id";
	
	/** The Constant FIXES_LABEL. */
	public static final String FIXES_LABEL = "fixes";
	
	/** The graph manager. */
	private GraphManager       graphManager;
	
	/**
	 * Instantiates a new mappings graph.
	 * 
	 * @param graphManager
	 *            the graph manager
	 */
	public MappingsGraph(@NotNull final GraphManager graphManager) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.graphManager = graphManager;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Adds the mapping.
	 * 
	 * @param mapping
	 *            the mapping
	 */
	public void addMapping(final Mapping mapping) {
		final Vertex oneVertex = getOrCreateVertex(mapping.getFrom());
		final Vertex otherVertex = getOrCreateVertex(mapping.getTo());
		
		Edge edge = null;
		if (oneVertex instanceof MappableChangeSet) {
			assert otherVertex instanceof MappableReport;
			edge = getOrCreateEdge(oneVertex, otherVertex);
		} else {
			assert otherVertex instanceof MappableChangeSet;
			assert oneVertex instanceof MappableReport;
			edge = getOrCreateEdge(otherVertex, oneVertex);
		}
		
		assert edge != null;
	}
	
	/**
	 * Gets the graph id.
	 * 
	 * @param entity
	 *            the entity
	 * @return the graph id
	 */
	public final String getGraphID(final Artifact entity) {
		return entity.getBaseType().getSimpleName() + "##" + entity.getId();
	}
	
	/**
	 * Gets the or create.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the or create
	 */
	private Edge getOrCreateEdge(final Vertex from,
	                             final Vertex to) {
		final Iterable<Edge> edges = from.getEdges(Direction.OUT, MappingsGraph.FIXES_LABEL);
		Edge theEdge = null;
		
		boolean create = true;
		FIND_EDGE: for (final Edge edge : edges) {
			final Vertex outVertex = edge.getVertex(Direction.OUT);
			if ((outVertex != null) && outVertex.equals(to)) {
				create = false;
				theEdge = edge;
				break FIND_EDGE;
			}
		}
		
		if (create) {
			theEdge = from.addEdge(MappingsGraph.FIXES_LABEL, to);
		}
		
		assert theEdge != null;
		
		return theEdge;
	}
	
	/**
	 * Gets the or create.
	 * 
	 * @param entity
	 *            the entity
	 * @return the or create
	 */
	private Vertex getOrCreateVertex(final Artifact entity) {
		final String fromID = getGraphID(entity);
		Vertex vertex = this.graphManager.getGraph().getVertex(fromID);
		if (vertex == null) {
			vertex = this.graphManager.getGraph().addVertex(fromID);
			vertex.setProperty(MappingsGraph.TYPE_KEY, getVertexType(entity));
		}
		
		return vertex;
	}
	
	/**
	 * Gets the vertex type.
	 * 
	 * @param entity
	 *            the entity
	 * @return the vertex type
	 */
	public final String getVertexType(final Artifact entity) {
		return entity.getClassName();
	}
	
	/**
	 * Initialize.
	 * 
	 */
	public void initialize() {
		this.graphManager.createIndex(new GraphIndex().name(ID_KEY).dataType(String.class).unique(Direction.BOTH));
		this.graphManager.createIndex(new GraphIndex().name(TYPE_KEY).dataType(String.class));
		this.graphManager.createIndex(new GraphIndex().name(FIXES_LABEL));
	}
	
	/**
	 * Shutdown.
	 */
	public void shutdown() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.graphManager.getGraph().shutdown();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}

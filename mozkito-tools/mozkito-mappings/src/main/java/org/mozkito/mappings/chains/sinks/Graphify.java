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

package org.mozkito.mappings.chains.sinks;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.mappable.model.MappableChangeSet;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.mappings.utils.graph.MappingsGraph;

/**
 * The Class Graphify.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Graphify extends Sink<Mapping> {
	
	/**
	 * Adds the mapping.
	 * 
	 * @param graph
	 *            the graph
	 * @param mapping
	 *            the mapping
	 */
	private static final void addMapping(final Graph graph,
	                                     final Mapping mapping) {
		final Vertex oneVertex = getOrCreate(graph, mapping.getFrom());
		final Vertex otherVertex = getOrCreate(graph, mapping.getTo());
		
		Edge edge = null;
		if (oneVertex instanceof MappableChangeSet) {
			assert otherVertex instanceof MappableReport;
			edge = getOrCreate(graph, oneVertex, otherVertex);
		} else {
			assert otherVertex instanceof MappableChangeSet;
			assert oneVertex instanceof MappableReport;
			edge = getOrCreate(graph, otherVertex, oneVertex);
		}
		
		assert edge != null;
	}
	
	/**
	 * Gets the or create.
	 * 
	 * @param graph
	 *            the graph
	 * @param entity
	 *            the entity
	 * @return the or create
	 */
	private static final Vertex getOrCreate(final Graph graph,
	                                        final MappableEntity entity) {
		final String fromID = MappingsGraph.getGraphID(entity);
		Vertex vertex = graph.getVertex(fromID);
		if (vertex == null) {
			vertex = graph.addVertex(fromID);
			vertex.setProperty(MappingsGraph.TYPE_KEY, MappingsGraph.getVertexType(entity));
		}
		
		return vertex;
	}
	
	/**
	 * Gets the or create.
	 * 
	 * @param graph
	 *            the graph
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the or create
	 */
	private static final Edge getOrCreate(final Graph graph,
	                                      final Vertex from,
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
	 * Instantiates a new graphify.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param graph
	 *            the graph
	 */
	public Graphify(final Group threadGroup, final ISettings settings, final Graph graph) {
		super(threadGroup, settings, false);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			new ProcessHook<Mapping, Mapping>(this) {
				
				@Override
				public void process() {
					final Mapping mapping = getInputData();
					
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("Graphify.storing", mapping)); //$NON-NLS-1$
					}
					
					addMapping(graph, mapping);
					
					// if ((++i % PERSIST_COUNT_THRESHOLD) == 0) {
					// ((TitanGraph) graph).commit();
					// }
				}
			};
			
			new PostExecutionHook<Mapping, Mapping>(this) {
				
				@Override
				public void postExecution() {
					PRECONDITIONS: {
						// none
					}
					
					try {
						graph.shutdown();
					} finally {
						POSTCONDITIONS: {
							// none
						}
					}
				}
				
			};
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
		
	}
}

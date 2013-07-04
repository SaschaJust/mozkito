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

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.mappings.mappable.model.MappableEntity;

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
	
	/**
	 * Gets the graph id.
	 * 
	 * @param entity
	 *            the entity
	 * @return the graph id
	 */
	public static final String getGraphID(final MappableEntity entity) {
		return entity.getBaseType().getCanonicalName() + "##" + entity.getId();
	}
	
	/**
	 * Gets the vertex type.
	 * 
	 * @param entity
	 *            the entity
	 * @return the vertex type
	 */
	public static final String getVertexType(final MappableEntity entity) {
		return entity.getClassName();
	}
	
	/**
	 * Initialize.
	 * 
	 * @param graph
	 *            the graph
	 */
	public static void initialize(@NotNull final Graph graph) {
		if (graph instanceof TitanGraph) {
			final TitanGraph tGraph = (TitanGraph) graph;
			
			// unique
			tGraph.makeType().name(ID_KEY).dataType(String.class).indexed(Vertex.class).unique(Direction.BOTH)
			      .makePropertyKey();
			tGraph.makeType().name(TYPE_KEY).dataType(String.class).indexed(Vertex.class).makePropertyKey();
			tGraph.makeType().name(FIXES_LABEL).makeEdgeLabel();
			
			tGraph.commit();
			
		}
		
	}
}

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
 *******************************************************************************/

package org.mozkito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;

/**
 * The Class ChangeGenealogy.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class ChangeGenealogyLayer implements ChangeGenealogy<ChangeGenealogyLayerNode> {
	
	/** The core. */
	protected CoreChangeGenealogy core;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param core
	 *            the core
	 */
	public ChangeGenealogyLayer(final CoreChangeGenealogy core) {
		this.core = core;
	}
	
	/**
	 * Close.
	 */
	@Override
	public void close() {
		this.core.close();
	}
	
	/**
	 * Contains edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if successful
	 */
	@Override
	public abstract boolean containsEdge(final ChangeGenealogyLayerNode from,
	                                     final ChangeGenealogyLayerNode to);
	
	/**
	 * Contains vertex.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if successful
	 */
	@Override
	public abstract boolean containsVertex(final ChangeGenealogyLayerNode vertex);
	
	/**
	 * Edge size.
	 * 
	 * @return the int
	 */
	@Override
	public final int edgeSize() {
		return this.core.edgeSize();
	}
	
	/**
	 * Gets the all dependents.
	 * 
	 * @param t
	 *            the t
	 * @return the all dependents
	 */
	@Override
	public final Collection<ChangeGenealogyLayerNode> getAllDependants(final ChangeGenealogyLayerNode t) {
		return getDependants(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                     GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                     GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                     GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Gets the all parents.
	 * 
	 * @param t
	 *            the t
	 * @return the all parents
	 */
	@Override
	public final Collection<ChangeGenealogyLayerNode> getAllParents(final ChangeGenealogyLayerNode t) {
		return getParents(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
		                  GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
		                  GenealogyEdgeType.DeletedCallOnDeletedDefinition,
		                  GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.ChangeGenealogy#getCore()
	 */
	@Override
	public final CoreChangeGenealogy getCore() {
		return this.core;
	}
	
	/**
	 * Gets the dependents.
	 * 
	 * @param t
	 *            the t
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	@Override
	public abstract Collection<ChangeGenealogyLayerNode> getDependants(ChangeGenealogyLayerNode t,
	                                                                   GenealogyEdgeType... edgeTypes);
	
	/**
	 * Gets the edges.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the edges
	 */
	@Override
	public abstract Collection<GenealogyEdgeType> getEdges(final ChangeGenealogyLayerNode from,
	                                                       final ChangeGenealogyLayerNode to);
	
	/**
	 * Gets the existing edge types.
	 * 
	 * @return the existing edge types
	 */
	@Override
	public final Set<GenealogyEdgeType> getExistingEdgeTypes() {
		return this.core.getExistingEdgeTypes();
	}
	
	/**
	 * Gets the graph db dir.
	 * 
	 * @return the graph db dir
	 */
	@Override
	public final File getGraphDBDir() {
		return this.core.getGraphDBDir();
	}
	
	/**
	 * Gets the graph db service.
	 * 
	 * @return the graph db service
	 */
	@Override
	public final GraphDatabaseService getGraphDBService() {
		return this.core.getGraphDBService();
	}
	
	/**
	 * Gets the parents.
	 * 
	 * @param t
	 *            the t
	 * @param edgeTypes
	 *            the edge types
	 * @return the parents
	 */
	@Override
	public abstract Collection<ChangeGenealogyLayerNode> getParents(ChangeGenealogyLayerNode t,
	                                                                GenealogyEdgeType... edgeTypes);
	
	/**
	 * Vertex set.
	 * 
	 * @return the iterator
	 */
	@Override
	public abstract Iterable<ChangeGenealogyLayerNode> vertexSet();
	
	/**
	 * Vertex size.
	 * 
	 * @return the int
	 */
	@Override
	public abstract int vertexSize();
	
}

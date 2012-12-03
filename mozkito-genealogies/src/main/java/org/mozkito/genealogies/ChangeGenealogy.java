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

package org.mozkito.genealogies;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;

/**
 * The Interface ChangeGenealogy.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public interface ChangeGenealogy<T> {
	
	/**
	 * Close.
	 */
	public void close();
	
	/**
	 * Contains edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return true, if successful
	 */
	public boolean containsEdge(final T from,
	                            final T to);
	
	/**
	 * Contains vertex.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if successful
	 */
	public boolean containsVertex(final T vertex);
	
	/**
	 * Edge size.
	 * 
	 * @return the int
	 */
	public int edgeSize();
	
	/**
	 * Gets the all dependents.
	 * 
	 * @param t
	 *            the t
	 * @return the all dependents
	 */
	public Collection<T> getAllDependants(T t);
	
	/**
	 * Gets the all parents.
	 * 
	 * @param t
	 *            the t
	 * @return the all parents
	 */
	public Collection<T> getAllParents(T t);
	
	/**
	 * Gets the core.
	 * 
	 * @return the core
	 */
	public CoreChangeGenealogy getCore();
	
	/**
	 * Gets the dependents.
	 * 
	 * @param t
	 *            the t
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	public Collection<T> getDependants(T t,
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
	public Collection<GenealogyEdgeType> getEdges(final T from,
	                                              final T to);
	
	/**
	 * Gets the existing edge types.
	 * 
	 * @return the existing edge types
	 */
	public Set<GenealogyEdgeType> getExistingEdgeTypes();
	
	/**
	 * Gets the graph db dir.
	 * 
	 * @return the graph db dir
	 */
	public File getGraphDBDir();
	
	/**
	 * Gets the graph db service.
	 * 
	 * @return the graph db service
	 */
	public GraphDatabaseService getGraphDBService();
	
	/**
	 * Gets the id of the node represented within this change genealogy.
	 * 
	 * @param t
	 *            the t
	 * @return the node id if node within this genealogy vertex. Returns null otherwise.
	 */
	public String getNodeId(T t);
	
	/**
	 * Gets the parents.
	 * 
	 * @param t
	 *            the t
	 * @param edgeTypes
	 *            the edge types
	 * @return the parents
	 */
	public Collection<T> getParents(T t,
	                                GenealogyEdgeType... edgeTypes);
	
	/**
	 * Gets the roots.
	 * 
	 * @return the roots
	 */
	public Collection<T> getRoots();
	
	/**
	 * In degree.
	 * 
	 * @param node
	 *            the node
	 * @return the int
	 */
	public int inDegree(T node);
	
	/**
	 * In degree.
	 * 
	 * @param node
	 *            the node
	 * @param edgeTypes
	 *            the edge types
	 * @return the int
	 */
	public int inDegree(T node,
	                    GenealogyEdgeType... edgeTypes);
	
	/**
	 * Out degree.
	 * 
	 * @param node
	 *            the node
	 * @return the int
	 */
	public int outDegree(T node);
	
	/**
	 * Out degree.
	 * 
	 * @param node
	 *            the node
	 * @param edgeTypes
	 *            the edge types
	 * @return the int
	 */
	public int outDegree(T node,
	                     GenealogyEdgeType... edgeTypes);
	
	/**
	 * Vertex set.
	 * 
	 * @return the iterator
	 */
	public Iterable<T> vertexSet();
	
	/**
	 * Vertex size.
	 * 
	 * @return the int
	 */
	public int vertexSize();
}

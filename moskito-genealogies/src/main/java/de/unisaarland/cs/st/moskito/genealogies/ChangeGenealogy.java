package de.unisaarland.cs.st.moskito.genealogies;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;

public interface ChangeGenealogy<T, K> {
	
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
	public boolean containsEdge(final T from, final T to);
	
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
	public Collection<K> getAllDependents(T t);
	
	/**
	 * Gets the all parents.
	 * 
	 * @param t
	 *            the t
	 * @return the all parents
	 */
	public Collection<K> getAllParents(T t);
	
	/**
	 * Gets the dependents.
	 * 
	 * @param t
	 *            the t
	 * @param edgeTypes
	 *            the edge types
	 * @return the dependents
	 */
	public Collection<K> getDependents(T t, GenealogyEdgeType... edgeTypes);
	
	/**
	 * Gets the edges.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the edges
	 */
	public Collection<GenealogyEdgeType> getEdges(final T from, final T to);
	
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
	 * Gets the parents.
	 * 
	 * @param t
	 *            the t
	 * @param edgeTypes
	 *            the edge types
	 * @return the parents
	 */
	public Collection<K> getParents(T t, GenealogyEdgeType... edgeTypes);
	
	/**
	 * Vertex set.
	 * 
	 * @return the iterator
	 */
	public Iterator<K> vertexSet();
	
	/**
	 * Vertex size.
	 * 
	 * @return the int
	 */
	public int vertexSize();
	
}

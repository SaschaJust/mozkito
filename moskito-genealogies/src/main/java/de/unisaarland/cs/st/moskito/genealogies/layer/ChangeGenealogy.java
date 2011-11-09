package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;

/**
 * The Class ChangeGenealogy.
 * 
 * @param <T>
 *            The type of objects accepted as input arguments
 * @param <K>
 *            the type of objects method results will be based on
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public abstract class ChangeGenealogy<T, K> {
	
	/** The core. */
	protected CoreChangeGenealogy core;
	
	/**
	 * Instantiates a new change genealogy.
	 * 
	 * @param core
	 *            the core
	 */
	public ChangeGenealogy(CoreChangeGenealogy core){
		this.core = core;
	}
	
	/**
	 * Close.
	 */
	public void close() {
		core.close();
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
	public abstract boolean containsEdge(final T from, final T to);
	
	/**
	 * Contains vertex.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return true, if successful
	 */
	public abstract boolean containsVertex(final T vertex);
	
	/**
	 * Edge size.
	 * 
	 * @return the int
	 */
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
	public final Collection<K> getAllDependents(T t) {
		return getDependents(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	/**
	 * Gets the all parents.
	 * 
	 * @param t
	 *            the t
	 * @return the all parents
	 */
	public final Collection<K> getAllParents(T t) {
		return getParents(t, GenealogyEdgeType.CallOnDefinition, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedCallOnCall,
				GenealogyEdgeType.DeletedCallOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition);
	}
	
	protected final CoreChangeGenealogy getCore(){
		return core;
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
	public abstract Collection<K> getDependents(T t, GenealogyEdgeType... edgeTypes);
	
	/**
	 * Gets the edges.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the edges
	 */
	public abstract Collection<GenealogyEdgeType> getEdges(final T from, final T to);
	
	/**
	 * Gets the existing edge types.
	 * 
	 * @return the existing edge types
	 */
	public final Set<GenealogyEdgeType> getExistingEdgeTypes(){
		return this.core.getExistingEdgeTypes();
	}
	
	/**
	 * Gets the graph db dir.
	 * 
	 * @return the graph db dir
	 */
	public final File getGraphDBDir() {
		return this.core.getGraphDBDir();
	}
	
	/**
	 * Gets the graph db service.
	 * 
	 * @return the graph db service
	 */
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
	public abstract Collection<K> getParents(T t, GenealogyEdgeType... edgeTypes);
	
	/**
	 * Vertex set.
	 * 
	 * @return the iterator
	 */
	public abstract Iterator<K> vertexSet();
	
	/**
	 * Vertex size.
	 * 
	 * @return the int
	 */
	public abstract int vertexSize();
	
}

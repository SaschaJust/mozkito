package de.unisaarland.cs.st.reposuite.genealogies;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class ChangeGenealogy<T> {
	
	private CoreChangeGenealogy core;
	
	public void close() {
		core.close();
	}
	
	public abstract boolean containsEdge(final T from, final T to);
	
	public abstract boolean containsVertex(final T vertex);
	
	public final int edgeSize() {
		return this.core.edgeSize();
	}
	
	public abstract Collection<GenealogyEdgeType> getEdges(final T from, final T to);
	
	public final File getGraphDBDir() {
		return this.core.getGraphDBDir();
	}
	
	public final GraphDatabaseService getGraphDBService() {
		return this.core.getGraphDBService();
	}
	
	public abstract Iterator<T> vertexSet();
	
	public abstract int vertexSize();
	
}

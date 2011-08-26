package de.unisaarland.cs.st.reposuite.genealogies;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;

import org.neo4j.graphdb.GraphDatabaseService;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public interface ChangeGenealogy {
	
	public boolean addEdge(@NotEmpty final GenealogyVertex dependantVertex,
			@NotEmpty final GenealogyVertex targetVertex, final GenealogyEdgeType edgeType);
	
	public void close();
	
	public boolean containsEdge(final GenealogyVertex from, final GenealogyVertex to);
	
	public boolean containsVertex(final GenealogyVertex vertex);
	
	int edgeSize();
	
	public Collection<GenealogyEdgeType> getEdges(final GenealogyVertex from, final GenealogyVertex to);
	
	Set<String> getExistingEdgeTypes();
	
	public File getGraphDBDir();
	
	GraphDatabaseService getGraphDBService();
	
	public Collection<JavaChangeOperation> getJavaChangeOperationsForVertex(final GenealogyVertex v);
	
	public RCSTransaction getTransactionForVertex(final GenealogyVertex v);
	
	public GenealogyVertexIterator vertexSet();
	
	public int vertexSize();
	
}

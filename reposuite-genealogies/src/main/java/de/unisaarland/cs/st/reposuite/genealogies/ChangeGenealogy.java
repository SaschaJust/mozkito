package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;

import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

public interface ChangeGenealogy {
	
	public boolean addEdge(@NotEmpty final GenealogyVertex dependantVertex,
			@NotEmpty final GenealogyVertex targetVertex, final GenealogyEdgeType edgeType);
	
	public boolean containsEdge(final GenealogyVertex from, final GenealogyVertex to);
	
	public boolean containsVertex(final GenealogyVertex vertex);
	
	public Collection<GenealogyEdgeType> getEdges(final GenealogyVertex from, final GenealogyVertex to);
	
	public Collection<JavaChangeOperation> getJavaChangeOperationsForVertex(final GenealogyVertex v);
	
	public RCSTransaction getTransactionForVertex(final GenealogyVertex v);
	
	public GenealogyVertexIterator vertexSet();
	
	public int vertexSize();
}

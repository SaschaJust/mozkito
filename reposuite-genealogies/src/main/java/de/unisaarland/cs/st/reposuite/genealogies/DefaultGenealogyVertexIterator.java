package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;



public class DefaultGenealogyVertexIterator implements GenealogyVertexIterator {
	
	private final IndexHits<Node> hits;
	private CoreChangeGenealogy       changeGenealogy;
	
	public DefaultGenealogyVertexIterator(final IndexHits<Node> hits, CoreChangeGenealogy changeGenealogy) {
		this.hits = hits;
		this.changeGenealogy = changeGenealogy;
	}
	
	@Override
	public void close() {
		this.hits.close();
	}
	
	@Override
	public boolean hasNext() {
		boolean result = this.hits.hasNext();
		if(!result){
			this.close();
		}
		return result;
	}
	
	@Override
	public Iterator<GenealogyVertex> iterator() {
		return this;
	}
	
	@Override
	public GenealogyVertex next() {
		if (!hasNext()) {
			return null;
		}
		Node node = this.hits.next();
		return changeGenealogy.getVertexForNode(node);
	}
	
	@Override
	public void remove() {
		this.hits.remove();
	}
	
}

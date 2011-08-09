package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Iterator;



public interface GenealogyVertexIterator extends Iterator<GenealogyVertex>, Iterable<GenealogyVertex> {
	
	public void close();
	
}

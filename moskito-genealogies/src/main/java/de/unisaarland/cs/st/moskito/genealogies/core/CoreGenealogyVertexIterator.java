package de.unisaarland.cs.st.moskito.genealogies.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.GenealogyPersistenceAdapter;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class CoreGenealogyVertexIterator implements Iterator<JavaChangeOperation>, Iterable<JavaChangeOperation> {
	
	private final Collection<Long> operationIds;
	private Iterator<Long>         iterator;
	private GenealogyPersistenceAdapter persistenceAdapter;
	
	public CoreGenealogyVertexIterator(Set<Long> operations, GenealogyPersistenceAdapter persistenceAdapter) {
		operationIds = operations;
		iterator = operationIds.iterator();
		this.persistenceAdapter = persistenceAdapter;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	@Override
	public Iterator<JavaChangeOperation> iterator() {
		return this;
	}
	
	@Override
	public JavaChangeOperation next() {
		Long nextId = iterator.next();
		return persistenceAdapter.loadById(nextId, JavaChangeOperation.class);
	}
	
	@Override
	public void remove() {
		iterator.remove();
	}
	
}

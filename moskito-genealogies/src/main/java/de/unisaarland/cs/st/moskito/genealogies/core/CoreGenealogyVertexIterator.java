package de.unisaarland.cs.st.moskito.genealogies.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class CoreGenealogyVertexIterator implements Iterator<JavaChangeOperation> {
	
	private final Collection<Long> operationIds;
	private Iterator<Long>         iterator;
	private PersistenceUtil        persistenceUtil;
	
	public CoreGenealogyVertexIterator(Set<Long> operations, PersistenceUtil persistenceUtil) {
		operationIds = operations;
		iterator = operationIds.iterator();
		this.persistenceUtil = persistenceUtil;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	@Override
	public JavaChangeOperation next() {
		Long nextId = iterator.next();
		return persistenceUtil.loadById(nextId, JavaChangeOperation.class);
	}
	
	@Override
	public void remove() {
		iterator.remove();
	}
	
}

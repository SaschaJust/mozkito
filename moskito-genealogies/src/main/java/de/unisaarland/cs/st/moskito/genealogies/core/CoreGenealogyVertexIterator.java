/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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

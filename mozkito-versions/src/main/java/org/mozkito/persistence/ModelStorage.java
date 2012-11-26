package org.mozkito.persistence;

public interface ModelStorage<S, T> {
	
	public T getById(S id);
	
}

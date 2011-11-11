package de.unisaarland.cs.st.moskito.genealogies.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class OperationCollection {
	
	private List<JavaChangeOperation> collection = new LinkedList<JavaChangeOperation>();
	
	public OperationCollection(Collection<JavaChangeOperation> collection) {
		this.collection.addAll(collection);
	}
	
	public Collection<JavaChangeOperation> unpack() {
		return collection;
	}
	
}

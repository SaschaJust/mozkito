package de.unisaarland.cs.st.moskito.genealogies;


public interface PartitionGenerator<I, O> {
	
	public O partition(I input);
	
}

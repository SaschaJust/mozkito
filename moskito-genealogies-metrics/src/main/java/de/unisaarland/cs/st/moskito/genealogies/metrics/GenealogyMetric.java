package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;


public interface GenealogyMetric<T> {
	
	public Collection<String> getMetricNames();
	
	public Collection<GenealogyMetricValue> handle(T item);
	
}

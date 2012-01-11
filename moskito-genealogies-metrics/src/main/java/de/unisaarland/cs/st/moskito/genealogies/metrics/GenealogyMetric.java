package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;


public interface GenealogyMetric<T> {
	
	public abstract Collection<String> getMetricNames();
	
	public abstract Collection<GenealogyMetricValue> handle(T item);
}

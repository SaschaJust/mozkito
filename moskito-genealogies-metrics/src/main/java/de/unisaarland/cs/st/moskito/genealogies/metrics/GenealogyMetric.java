package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;


public interface GenealogyMetric<T> {
	
	public Collection<String> getMetricNames();
	
	public Collection<GenealogyMetricValue> handle(GenealogyNode<T> item);
	
}

package de.unisaarland.cs.st.moskito.genealogies.metrics;

import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;


public interface GenealogyMetric<T> {
	
	public String getMetricName();
	
	public Double handle(GenealogyNode<T> item);
	
}

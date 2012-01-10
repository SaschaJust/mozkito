package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalTestPageRankMetric {
	
	private UniversalPageRankMetric<String> universalMetric;
	
	public UniversalTestPageRankMetric(ChangeGenealogy<String> genealogy) {
		universalMetric = new UniversalPageRankMetric<String>(genealogy);
	}
	
	public Collection<GenealogyMetricValue> handle(String item, boolean last) {
		return universalMetric.handle(item, last);
	}
	
}

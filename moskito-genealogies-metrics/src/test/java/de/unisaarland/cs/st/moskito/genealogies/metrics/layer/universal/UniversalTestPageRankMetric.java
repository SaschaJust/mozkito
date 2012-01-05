package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalPageRankMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;

public class UniversalTestPageRankMetric {
	
	private UniversalPageRankMetric<String> universalMetric;
	
	public UniversalTestPageRankMetric(ChangeGenealogy<String> genealogy) {
		universalMetric = new UniversalPageRankMetric<String>(genealogy);
	}
	
	public Collection<GenealogyMetricValue> handle(GenealogyNode<String> item, boolean last) {
		return universalMetric.handle(item.getNode(), last);
	}
	
}

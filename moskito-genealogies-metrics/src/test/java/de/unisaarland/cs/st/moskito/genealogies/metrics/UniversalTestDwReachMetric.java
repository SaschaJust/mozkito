package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;
import java.util.Comparator;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;

public class UniversalTestDwReachMetric {
	
	private UniversalDwReachMetric<String> universalMetric;
	
	public UniversalTestDwReachMetric(ChangeGenealogy<String> genealogy) {
		universalMetric = new UniversalDwReachMetric<String>(genealogy, new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				return -1;
			}
		});
	}
	
	public Collection<GenealogyMetricValue> handle(GenealogyNode<String> item) {
		return universalMetric.handle(item.getNode());
	}
	
}

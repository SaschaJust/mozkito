package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;

public class UniversalTestTempDepthMetric implements DayTimeDiff<String> {
	
	private UniversalTempDepthMetrics<String> universalMetric;
	
	public UniversalTestTempDepthMetric(ChangeGenealogy<String> genealogy) {
		universalMetric = new UniversalTempDepthMetrics<String>(genealogy, this);
	}
	
	@Override
	public int daysDiff(String t1, String t2) {
		int i1 = Integer.valueOf(t1).intValue();
		int i2 = Integer.valueOf(t2).intValue();
		return Math.abs(i1 - i2);
	}
	
	public Collection<GenealogyMetricValue> handle(GenealogyNode<String> item) {
		return universalMetric.handle(item.getNode());
	}
	
}

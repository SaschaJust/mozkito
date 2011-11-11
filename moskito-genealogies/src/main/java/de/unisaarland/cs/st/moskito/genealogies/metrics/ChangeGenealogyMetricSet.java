package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.collections.DataFrame;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;

public class ChangeGenealogyMetricSet {
	
	private Map<String, ChangeGenealogyVertexMetric> vertexMetrics = new HashMap<String, ChangeGenealogyVertexMetric>();
	
	public <K, T> ChangeGenealogyMetricSet() {
	}
	
	public <T, K> DataFrame<Double> computeVertexMetrics(ChangeGenealogy<T, K> genealogy) {
		
		if (vertexMetrics.isEmpty()) {
			return new DataFrame<Double>(new ArrayList<String>());
		}
		
		List<String> metricNames = new ArrayList<String>(vertexMetrics.size());
		ChangeGenealogyVertexMetric nameMetric = vertexMetrics.values().iterator().next();
		
		for (String metricName : vertexMetrics.keySet()) {
			metricNames.add(metricName);
		}
		
		DataFrame<Double> result = new DataFrame<Double>(metricNames);
		
		Iterator<K> vertexIter = genealogy.vertexSet();
		while (vertexIter.hasNext()) {
			K vertex = vertexIter.next();
			String vertexName = nameMetric.getVertexLabel(vertex);
			if (result.containsRow(vertexName)) {
				if (Logger.logError()) {
					Logger.error("Seen vertex with label `" + vertexName + "` multiple times. Ignoring!");
				}
				break;
			}
			ArrayList<Double> valueList = new ArrayList<Double>(metricNames.size());
			for(String metricName : metricNames){
				valueList.add(vertexMetrics.get(metricName).visit(vertex));
			}
			result.addRow(vertexName, valueList.toArray(new Double[valueList.size()]));
		}
		return result;
	}
	
	public boolean register(ChangeGenealogyVertexMetric metric, String metricName) {
		if (vertexMetrics.containsKey(metricName)) {
			return false;
		}
		if (Logger.logDebug()) {
			Logger.debug("Registering genealogy metric " + metricName);
		}
		vertexMetrics.put(metricName, metric);
		return true;
	}
	
}

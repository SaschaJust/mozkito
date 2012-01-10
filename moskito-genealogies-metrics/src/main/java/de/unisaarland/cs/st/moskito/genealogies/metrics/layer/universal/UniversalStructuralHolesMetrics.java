package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;


public class UniversalStructuralHolesMetrics<T> {
	
	private static String      inEffSize     = "InEffSize";
	private static String      outEffSize    = "OutEffSize";
	private static String      effSize       = "EffSize";
	private static String      inEfficiency  = "InEfficiency";
	private static String      outEfficiency = "OutEfficiency";
	private static String      efficiency    = "Efficiency";
	
	public static Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(inEffSize);
		result.add(outEffSize);
		result.add(effSize);
		
		result.add(inEfficiency);
		result.add(outEfficiency);
		result.add(efficiency);
		return result;
	}
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalStructuralHolesMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		Collection<T> incoming = genealogy.getAllDependants(node);
		Collection<T> outgoing = genealogy.getAllParents(node);
		@SuppressWarnings("unchecked") Collection<T> egoNetwork = CollectionUtils.union(incoming, outgoing);
		
		String nodeId = genealogy.getNodeId(node);
		
		DescriptiveStatistics inEffStat = new DescriptiveStatistics();
		for (T in : incoming) {
			//get the number of connections between in and all other incomings
			Collection<T> inTies = genealogy.getAllDependants(in);
			inTies.addAll(genealogy.getAllParents(in));
			inEffStat.addValue(CollectionUtils.intersection(incoming, inTies).size());
		}
		double inEgoSize = incoming.size();
		double inEffValue = inEgoSize - inEffStat.getMean();
		result.add(new GenealogyMetricValue(inEffSize, nodeId, inEffValue));
		result.add(new GenealogyMetricValue(inEfficiency, nodeId, (inEffValue / (inEgoSize + 1))));
		
		DescriptiveStatistics outEffStat = new DescriptiveStatistics();
		for (T out : outgoing) {
			//get the number of connections between in and all other outgoings
			Collection<T> outTies = genealogy.getAllDependants(out);
			outTies.addAll(genealogy.getAllParents(out));
			outEffStat.addValue(CollectionUtils.intersection(outgoing, outTies).size());
		}
		double outEgoSize = outgoing.size();
		double outEffValue = outEgoSize - inEffStat.getMean();
		result.add(new GenealogyMetricValue(outEffSize, nodeId, outEffValue));
		result.add(new GenealogyMetricValue(outEfficiency, nodeId, (outEffValue / (outEgoSize + 1))));
		
		DescriptiveStatistics effStat = new DescriptiveStatistics();
		for (T ego : egoNetwork) {
			//get the number of connections between in and all other ego-network
			Collection<T> ties = genealogy.getAllDependants(ego);
			ties.addAll(genealogy.getAllParents(ego));
			inEffStat.addValue(CollectionUtils.intersection(egoNetwork, ties).size());
		}
		double egoSize = egoNetwork.size();
		double effValue = egoSize - effStat.getMean();
		result.add(new GenealogyMetricValue(effSize, nodeId, effValue));
		result.add(new GenealogyMetricValue(efficiency, nodeId, (effValue / (egoSize + 1))));
		
		return result;
	}
	
}

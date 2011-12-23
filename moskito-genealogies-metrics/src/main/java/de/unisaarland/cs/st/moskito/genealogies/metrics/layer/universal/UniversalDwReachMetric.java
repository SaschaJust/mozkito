package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalDwReachMetric<T> {
	
	//TODO requires intensive testing
	
	protected static String    dwReach   = "dwReach";
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalDwReachMetric(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
	}
	
	
	
	public Collection<String> getMetricNames() {
		Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(dwReach);
		return metricNames;
	}
	
	private List<Set<T>> getReach(Collection<T> nodes, Set<T> seen, int depth) {
		List<Set<T>> result = new LinkedList<Set<T>>();
		
		if (depth < 31) {
			
			Set<T> level = new HashSet<T>();
			
			for (T node : nodes) {
				for (T parent : genealogy.getAllParents(node)) {
					if (!seen.contains(parent)) {
						level.add(parent);
						seen.add(parent);
					}
				}
			}
			result.add(level);
			
			List<Set<T>> reach = getReach(level, seen, ++depth);
			for (int i = 0; i < reach.size(); ++i) {
				if (result.size() == (i + 1)) {
					result.add(reach.get(i));
				} else {
					result.get(i + 1).addAll(reach.get(i));
				}
			}
		}
		return result;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(1);
		
		Set<T> roots = new HashSet<T>();
		Set<T> seen = new HashSet<T>();
		roots.add(node);
		seen.add(node);
		List<Set<T>> reach = getReach(roots, seen, 0);
		
		//		System.out.println("########## " + genealogy.getNodeId(node));
		//		System.out.println(StringUtils.join(reach.toArray(new Object[reach.size()])));

		double dwReachValue = 0;
		for (int i = 0; i < reach.size(); ++i) {
			dwReachValue += (reach.get(i).size() / ((double) i + 1));
		}
		
		metricValues.add(new GenealogyMetricValue(dwReach, genealogy.getNodeId(node), dwReachValue));
		
		return metricValues;
	}
	
}

/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class UniversalDwReachMetric<T> {
	
	private static final String dwReach = "dwReach";
	
	/**
	 * @return
	 */
	public static String getDwreach() {
		return dwReach;
	}
	
	public static Collection<String> getMetricNames() {
		final Collection<String> metricNames = new ArrayList<String>(2);
		metricNames.add(dwReach);
		return metricNames;
	}
	
	private final ChangeGenealogy<T> genealogy;
	
	private final Comparator<T>      comparator;
	
	public UniversalDwReachMetric(final ChangeGenealogy<T> genealogy, final Comparator<T> comparator) {
		this.genealogy = genealogy;
		this.comparator = comparator;
	}
	
	private List<Set<T>> getReach(final T originalNode,
	                              final Collection<T> nodes,
	                              final Set<T> seen) {
		final List<Set<T>> result = new LinkedList<Set<T>>();
		
		if (nodes.isEmpty()) {
			return result;
		}
		
		final Set<T> level = new HashSet<T>();
		
		for (final T node : nodes) {
			for (final T dependant : this.genealogy.getAllDependants(node)) {
				if (!seen.contains(dependant)) {
					if (this.comparator.compare(originalNode, node) >= 0) {
						continue;
					}
					level.add(dependant);
					seen.add(dependant);
				}
			}
		}
		result.add(level);
		
		final List<Set<T>> reach = getReach(originalNode, level, seen);
		for (int i = 0; i < reach.size(); ++i) {
			if (result.size() == (i + 1)) {
				result.add(reach.get(i));
			} else {
				result.get(i + 1).addAll(reach.get(i));
			}
		}
		return result;
	}
	
	public Collection<GenealogyMetricValue> handle(final T node) {
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(1);
		
		final Set<T> roots = new HashSet<T>();
		final Set<T> seen = new HashSet<T>();
		roots.add(node);
		seen.add(node);
		final List<Set<T>> reach = getReach(node, roots, seen);
		
		// System.out.println("########## " + genealogy.getNodeId(node));
		// System.out.println(StringUtils.join(reach.toArray(new
		// Object[reach.size()])));
		
		double dwReachValue = 0;
		for (int i = 0; i < reach.size(); ++i) {
			dwReachValue += (reach.get(i).size() / ((double) i + 1));
		}
		
		metricValues.add(new GenealogyMetricValue(dwReach, this.genealogy.getNodeId(node), dwReachValue));
		
		return metricValues;
	}
	
}

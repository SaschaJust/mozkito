/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.untangling.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozkito.untangling.blob.combine.CombineOperator;

/**
 * The Class CollectionUtils.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CollectionUtils {
	
	/**
	 * Gets the all combinations.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param elements
	 *            the elements
	 * @param operator
	 *            the operator
	 * @param maxBlobSize
	 *            the max blob size
	 * @return the all combinations
	 */
	@SuppressWarnings ("unchecked")
	public static <T> Set<Set<T>> getAllCombinations(final Collection<T> elements,
	                                                 final CombineOperator<T> operator,
	                                                 final int maxBlobSize) {
		final List<T> elementList = new ArrayList<T>(elements.size());
		elementList.addAll(elements);
		
		final Map<Set<T>, Collection<T>> combinations = new HashMap<Set<T>, Collection<T>>();
		
		for (int i = 0; i < elementList.size(); ++i) {
			for (int j = 0; j < elementList.size(); ++j) {
				if (i == j) {
					continue;
				}
				final T t1 = elementList.get(i);
				final T t2 = elementList.get(j);
				if (operator.canBeCombined(t1, t2)) {
					final Set<T> keySet = new HashSet<T>();
					keySet.add(t1);
					if (!combinations.containsKey(keySet)) {
						combinations.put(keySet, new HashSet<T>());
					}
					combinations.get(keySet).add(t2);
				}
			}
		}
		Set<Set<T>> newAdded = new HashSet<Set<T>>();
		newAdded.addAll(combinations.keySet());
		
		while (!newAdded.isEmpty()) {
			final Set<Set<T>> newNewAdded = new HashSet<Set<T>>();
			for (final Set<T> t : newAdded) {
				if (!combinations.containsKey(t)) {
					continue;
				}
				for (final T t2 : combinations.get(t)) {
					final Set<T> newT = new HashSet<T>();
					newT.addAll(t);
					newT.add(t2);
					if (newT.size() > maxBlobSize) {
						continue;
					}
					newNewAdded.add(newT);
					// check the possible combinations for newT
					// candidates are (all possible combinations of t union all
					// possible of t2) minus the elements of (union t and t2)
					
					final Set<T> possibleCombinations = new HashSet<T>();
					possibleCombinations.addAll(combinations.get(t));
					final HashSet<T> tmpSet = new HashSet<T>();
					tmpSet.add(t2);
					if (!combinations.containsKey(tmpSet)) {
						continue;
					}
					possibleCombinations.addAll(combinations.get(tmpSet));
					final Collection<T> newCombinations = org.apache.commons.collections.CollectionUtils.subtract(possibleCombinations,
					                                                                                              newT);
					combinations.put(newT, newCombinations);
				}
			}
			newAdded = newNewAdded;
		}
		return combinations.keySet();
	}
	
	/**
	 * Transitive closure.
	 * 
	 * @param original
	 *            the original
	 * @return the sets the
	 */
	public static Set<Set<String>> transitiveClosure(final Set<Set<String>> original) {
		final Set<Set<String>> newAdded = new HashSet<Set<String>>();
		newAdded.addAll(original);
		final Set<Set<String>> pathsToCombine = new HashSet<Set<String>>();
		pathsToCombine.addAll(newAdded);
		while (!newAdded.isEmpty()) {
			final Set<Set<String>> tmp = new HashSet<Set<String>>();
			tmp.addAll(newAdded);
			newAdded.clear();
			for (final Set<String> l : pathsToCombine) {
				for (final Set<String> t : tmp) {
					if (org.apache.commons.collections.CollectionUtils.containsAny(l, t) && (!t.containsAll(l))) {
						final Set<String> newL = new HashSet<String>();
						newL.addAll(t);
						newL.addAll(l);
						newAdded.add(newL);
					}
				}
			}
			pathsToCombine.addAll(tmp);
		}
		return pathsToCombine;
	}
}

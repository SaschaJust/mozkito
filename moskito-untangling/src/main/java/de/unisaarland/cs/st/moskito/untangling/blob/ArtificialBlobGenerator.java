/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.blob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.compare.GreaterOrEqualInt;
import net.ownhero.dev.kanuni.annotations.simple.NotNegative;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

/**
 * The Class ArtificialBlobGenerator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ArtificialBlobGenerator {
	
	/**
	 * Generate all artificial blobs from the set of atomicChanges supplied.
	 * This algorithm combines all transactions that change files within the
	 * same file path directory or files whose file paths are not more that
	 * <code>packageDistance</code> file path directories apart. The distance
	 * between two file paths is determined as follows: 1) compute the longest
	 * common sub-path between both file paths. 2) split the remaining file
	 * paths by '<FileUtils.fileSeparator>' 3) The longest array of split
	 * remaining file path segments defines the distance.
	 * 
	 * @param transactions
	 *            the transactions
	 * @param packageDistance
	 *            The maximal number of path segements allowed to combine two
	 *            transactions. E.g. <code>a/b/x</code> and
	 *            <code>a/b/c/d/y</code> have a distance of two, while
	 *            <code>a/b/x</code> and <code>a/c/d/e/y</code> have a distance
	 *            of three.
	 * @param minBlobSize
	 *            generate no artificial blobs smaller than
	 *            <code>minBlobSize</code> transactions.
	 * @param maxBlobSize
	 *            generate no artificial blobs larger than
	 *            <code>maxBlobSize</code> transactions.
	 * @param long1
	 * @return the set of generated artificial blobs
	 */
	public static Set<ArtificialBlob> generateAll(@NotNull final Collection<AtomicTransaction> transactions,
	                                              @NotNegative final int packageDistance,
	                                              @GreaterOrEqualInt (ref = 2) @NotNegative final int minBlobSize,
	                                              @GreaterOrEqualInt (ref = -1) final int maxBlobSize,
	                                              final Long timeWindowSize) {
		
		// check the more complicated preconditions
		if (maxBlobSize > -1) {
			if (maxBlobSize < minBlobSize) {
				throw new UnrecoverableError(
				                             "The 'maxBlobSize' argument must either be -1 (for unlimited size) or greater or equals than minBlobSize. All other settings make no sense.");
			}
		}
		
		BlobTransactionCombineOperator operator = new BlobTransactionCombineOperator(packageDistance, timeWindowSize);
		
		Set<Set<AtomicTransaction>> allCombinations = getAllCombinations(transactions, operator, maxBlobSize);
		
		if (Logger.logDebug()) {
			Logger.debug("Found " + allCombinations.size() + " transaction combinations (may be decreased).");
		}
		
		// Filter out too small combinations
		Iterator<Set<AtomicTransaction>> setIter = allCombinations.iterator();
		while (setIter.hasNext()) {
			Set<AtomicTransaction> next = setIter.next();
			if (next.size() < minBlobSize) {
				setIter.remove();
			}
		}
		
		Set<ArtificialBlob> result = new HashSet<ArtificialBlob>();
		for (Set<AtomicTransaction> set : allCombinations) {
			result.add(new ArtificialBlob(set));
		}
		
		if (Logger.logInfo()) {
			Logger.info("Found " + result.size() + " artificial blobs.");
		}
		
		return result;
	}
	
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
	protected static <T> Set<Set<T>> getAllCombinations(final Collection<T> elements,
	                                                    final CombineOperator<T> operator,
	                                                    final int maxBlobSize) {
		List<T> elementList = new ArrayList<T>(elements.size());
		elementList.addAll(elements);
		
		Map<Set<T>, Collection<T>> combinations = new HashMap<Set<T>, Collection<T>>();
		
		for (int i = 0; i < elementList.size(); ++i) {
			for (int j = 0; j < elementList.size(); ++j) {
				if (i == j) {
					continue;
				}
				T t1 = elementList.get(i);
				T t2 = elementList.get(j);
				if (operator.canBeCombined(t1, t2)) {
					Set<T> keySet = new HashSet<T>();
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
			Set<Set<T>> newNewAdded = new HashSet<Set<T>>();
			for (Set<T> t : newAdded) {
				if (!combinations.containsKey(t)) {
					continue;
				}
				for (T t2 : combinations.get(t)) {
					Set<T> newT = new HashSet<T>();
					newT.addAll(t);
					newT.add(t2);
					if (newT.size() > maxBlobSize) {
						continue;
					}
					newNewAdded.add(newT);
					// check the possible combinations for newT
					// candidates are (all possible combinations of t union all
					// possible of t2) minus the elements of (union t and t2)
					
					Set<T> possibleCombinations = new HashSet<T>();
					possibleCombinations.addAll(combinations.get(t));
					HashSet<T> tmpSet = new HashSet<T>();
					tmpSet.add(t2);
					if (!combinations.containsKey(tmpSet)) {
						continue;
					}
					possibleCombinations.addAll(combinations.get(tmpSet));
					Collection<T> newCombinations = CollectionUtils.subtract(possibleCombinations, newT);
					combinations.put(newT, newCombinations);
				}
			}
			newAdded = newNewAdded;
		}
		return combinations.keySet();
	}
	
	/**
	 * Gets the longest common path.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the longest common path
	 */
	public static String getLongestCommonPath(final String x,
	                                          final String y) {
		int M = x.length();
		int N = y.length();
		
		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M + 1][N + 1];
		
		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.charAt(i) == y.charAt(j)) {
					opt[i][j] = opt[i + 1][j + 1] + 1;
				} else {
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		// recover LCS itself and print it to standard output
		int i = 0, j = 0;
		while ((i < M) && (j < N)) {
			if (x.charAt(i) == y.charAt(j)) {
				sb.append(x.charAt(i));
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1]) {
				i++;
			} else {
				j++;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Transitive closure.
	 * 
	 * @param original
	 *            the original
	 * @return the sets the
	 */
	protected static Set<Set<String>> transitiveClosure(final Set<Set<String>> original) {
		Set<Set<String>> newAdded = new HashSet<Set<String>>();
		newAdded.addAll(original);
		Set<Set<String>> pathsToCombine = new HashSet<Set<String>>();
		pathsToCombine.addAll(newAdded);
		while (!newAdded.isEmpty()) {
			Set<Set<String>> tmp = new HashSet<Set<String>>();
			tmp.addAll(newAdded);
			newAdded.clear();
			for (Set<String> l : pathsToCombine) {
				for (Set<String> t : tmp) {
					if (CollectionUtils.containsAny(l, t) && (!t.containsAll(l))) {
						Set<String> newL = new HashSet<String>();
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

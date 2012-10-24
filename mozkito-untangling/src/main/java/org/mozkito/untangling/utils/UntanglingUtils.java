/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.untangling.utils;

/**
 * The Class UntanglingUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UntanglingUtils {
	
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
		final int M = x.length();
		final int N = y.length();
		
		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		final int[][] opt = new int[M + 1][N + 1];
		
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
		
		final StringBuilder sb = new StringBuilder();
		
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
}

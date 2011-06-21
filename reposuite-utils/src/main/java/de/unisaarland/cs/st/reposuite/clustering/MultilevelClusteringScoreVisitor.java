/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.clustering;

/**
 * The Interface MultilevelPartitioningScoreVisitor.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface MultilevelClusteringScoreVisitor<T> {
	
	/**
	 * Returns the highest possible score value
	 * 
	 * @return
	 */
	public double getMaxPossibleScore();
	
	/**
	 * Returns a confidence value between 0 and getMaxPossibleScore(). The
	 * higher the confidence, the stronger the voter indicates that the provided
	 * artifacts t1 and t2 should belong to the same cluster.
	 * 
	 * @param ts
	 *            the ts
	 * @param oldScore
	 *            the old score
	 * @return the new (manipulated) score
	 */
	public double getScore(final T t1,
	                       final T t2);
}

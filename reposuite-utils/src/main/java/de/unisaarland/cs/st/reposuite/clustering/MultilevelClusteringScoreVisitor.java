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
	 * Gets the new score by manipulating the given score.
	 * 
	 * @param ts
	 *            the ts
	 * @param oldScore
	 *            the old score
	 * @return the new (manipulated) score
	 */
	public double getScore(final T t1, final T t2, double oldScore);
	
}

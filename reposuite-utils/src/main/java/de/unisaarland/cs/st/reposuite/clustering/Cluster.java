package de.unisaarland.cs.st.reposuite.clustering;

import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * The Class PartitionCell.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Cluster<T> implements Comparable<Cluster<T>> {
	
	/** The elements. */
	private Tuple<Cluster<T>, Cluster<T>> children = null;
	
	private final double          score;
	
	/**
	 * Instantiates a new partition cell.
	 * 
	 * @param t1
	 *            the t1
	 * @param t2
	 *            the t2
	 */
	public Cluster(final Cluster<T> t1, final Cluster<T> t2, final double score) {
		this.children = new Tuple<Cluster<T>, Cluster<T>>(t1, t2);
		this.score = score;
	}
	
	/**
	 * Instantiates a new partition.
	 */
	protected Cluster(final double score) {
		this.score = score;
	}
	
	
	@Override
	public int compareTo(final Cluster<T> o) {
		if (this.getScore() > o.getScore()) {
			return -1;
		} else if (this.getScore() < o.getScore()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
	
	
	/**
	 * Gets the elements.
	 * 
	 * @return the elements
	 */
	public Tuple<Cluster<T>, Cluster<T>> getChildren() {
		return this.children;
	}
	
	public double getScore() {
		return this.score;
	}
	
	
}

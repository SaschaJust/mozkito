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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

/**
 * The Class MultilevelPartitioning.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class MultilevelClustering<T> {
	
	/**
	 * The Class ComparableTuple.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <M>
	 *            the generic type
	 * @author Kim Herzig <herzig@cs.uni-saarland.de>
	 */
	protected static class ComparableTuple<K, M> extends Tuple<Double, M> implements Comparable<Tuple<Double, M>> {
		
		/**
		 * Instantiates a new comparable tuple.
		 * 
		 * @param f
		 *            the f
		 * @param s
		 *            the s
		 */
		public ComparableTuple(final Double f, final M s) {
			super(f, s);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final Tuple<Double, M> o) {
			return o.getFirst().compareTo(getFirst());
		}
		
	}
	
	/** The matrix. */
	private final Map<T, Map<T, Double>>                    matrix = new HashMap<T, Map<T, Double>>();
	
	/** The score visitors. */
	private final List<MultilevelClusteringScoreVisitor<T>> scoreVisitors;
	
	/** The collapse visitor. */
	private final MultilevelClusteringCollapseVisitor<T>    collapseVisitor;
	
	/**
	 * Instantiates a new multilevel partitioning.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param scoreVisitors
	 *            the score visitors
	 * @param collapseVisitor
	 *            the collapse visitor
	 */
	@NoneNull
	public MultilevelClustering(final Collection<T> nodes,
	                            final List<MultilevelClusteringScoreVisitor<T>> scoreVisitors,
	                            final MultilevelClusteringCollapseVisitor<T> collapseVisitor) {
		@SuppressWarnings ("unchecked")
		T[] array = (T[]) nodes.toArray();
		this.scoreVisitors = scoreVisitors;
		this.collapseVisitor = collapseVisitor;
		this.init(array);
		
	}
	
	/**
	 * Instantiates a new multilevel partitioning.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param scoreVisitors
	 *            the score visitors
	 * @param collapseVisitor
	 *            the collapse visitor
	 */
	@NoneNull
	public MultilevelClustering(final T[] nodes, final List<MultilevelClusteringScoreVisitor<T>> scoreVisitors,
	                            final MultilevelClusteringCollapseVisitor<T> collapseVisitor) {
		this.scoreVisitors = scoreVisitors;
		this.collapseVisitor = collapseVisitor;
		this.init(nodes);
	}
	
	/**
	 * Gets the <numPartitions> partitions.
	 * 
	 * @param numPartitions
	 *            the num partitions
	 * @return the a set of set of nodes. The size of the outer should be equal
	 *         to <code>numPartitions</code>. Return null in case something went
	 *         wrong!
	 */
	public Set<Set<T>> getPartitions(final int numPartitions) {
		
		Set<Cluster<T>> existingClusters = new HashSet<Cluster<T>>();
		PriorityQueue<Cluster<T>> scores = new PriorityQueue<Cluster<T>>();
		
		for (T t1 : this.matrix.keySet()) {
			Cluster<T> p1 = new VirtualCluster<T>(t1);
			existingClusters.add(p1);
			for (T t2 : this.matrix.get(t1).keySet()) {
				Cluster<T> p2 = new VirtualCluster<T>(t2);
				if (!existingClusters.contains(p2)) {
					existingClusters.add(p2);
				}
				
				Cluster<T> tmpCluster = new Cluster<T>(p1, p2, this.matrix.get(t1).get(t2));
				scores.offer(tmpCluster);
			}
		}
		
		while (existingClusters.size() > numPartitions) {
			Cluster<T> highestScore = scores.poll();
			
			Tuple<Cluster<T>, Cluster<T>> children = highestScore.getChildren();
			if ((!existingClusters.contains(children.getFirst())) || (!existingClusters.contains(children.getSecond()))) {
				continue;
			}
			
			// delete old clusters!
			existingClusters.remove(children.getFirst());
			existingClusters.remove(children.getSecond());
			
			// compute all new scores
			for (Cluster<T> cluster : existingClusters) {
				
				double score = this.collapseVisitor.getScore(highestScore, cluster, matrix);
				Cluster<T> tmpCluster = new Cluster<T>(highestScore, cluster, score);
				scores.offer(tmpCluster);
			}
			
			// add new combined cluster
			existingClusters.add(highestScore);
		}
		
		Set<Set<T>> result = new HashSet<Set<T>>();
		for (Cluster<T> cluster : existingClusters) {
			result.add(cluster.getAllElements());
		}
		return result;
	}
	
	/**
	 * Gets the score.
	 * 
	 * @param t1
	 *            the t1
	 * @param t2
	 *            the t2
	 * @return the score
	 */
	public double getScore(final T t1,
	                       final T t2) {
		double score = 0d;
		for (MultilevelClusteringScoreVisitor<T> visitor : this.scoreVisitors) {
			score += visitor.getScore(t1, t2);
		}
		return score;
	}
	
	/**
	 * Inits the.
	 * 
	 * @param nodes
	 *            the nodes
	 */
	public void init(final T[] nodes) {
		int size = nodes.length;
		for (int i = 0; i < size; ++i) {
			this.matrix.put(nodes[i], new HashMap<T, Double>());
			Map<T, Double> values = this.matrix.get(nodes[i]);
			for (int j = i + 1; j < size; ++j) {
				values.put(nodes[j], getScore(nodes[i], nodes[j]));
			}
		}
	}
}

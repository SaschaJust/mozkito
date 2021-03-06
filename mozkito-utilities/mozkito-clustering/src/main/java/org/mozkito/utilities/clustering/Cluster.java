/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.utilities.clustering;

import java.util.HashSet;
import java.util.Set;

import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class PartitionCell.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Cluster<T> implements Comparable<Cluster<T>> {
	
	/** The elements. */
	private Tuple<Cluster<T>, Cluster<T>> children = null;
	
	/** The score. */
	private final double                  score;
	
	/**
	 * Instantiates a new partition cell.
	 * 
	 * @param t1
	 *            the t1
	 * @param t2
	 *            the t2
	 * @param score
	 *            the score
	 */
	public Cluster(final Cluster<T> t1, final Cluster<T> t2, final double score) {
		this.children = new Tuple<Cluster<T>, Cluster<T>>(t1, t2);
		this.score = score;
	}
	
	/**
	 * Instantiates a new partition.
	 * 
	 * @param score
	 *            the score
	 */
	protected Cluster(final double score) {
		this.score = score;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Cluster other = (Cluster) obj;
		if (this.children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!this.children.equals(other.children)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns all elements that are contained within this cluster (incl. all sub-clusters).
	 * 
	 * @return the all elements
	 */
	public Set<T> getAllElements() {
		final Set<T> result = new HashSet<T>();
		result.addAll(this.children.getFirst().getAllElements());
		result.addAll(this.children.getSecond().getAllElements());
		return result;
	}
	
	/**
	 * Gets the elements.
	 * 
	 * @return the elements
	 */
	public Tuple<Cluster<T>, Cluster<T>> getChildren() {
		return this.children;
	}
	
	/**
	 * Gets the score.
	 * 
	 * @return the score
	 */
	public double getScore() {
		return this.score;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.children == null)
		                                                    ? 0
		                                                    : this.children.hashCode());
		return result;
	}
	
}

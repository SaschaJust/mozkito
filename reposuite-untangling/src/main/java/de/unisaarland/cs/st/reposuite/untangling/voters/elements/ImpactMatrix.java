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
package de.unisaarland.cs.st.reposuite.untangling.voters.elements;

import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class ImpactMatrix.
 * 
 * @param <T>
 *            the generic type
 * @param <S>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ImpactMatrix<T, S> {
	
	/** The impact matrix. */
	private final Map<T, Map<S, Integer>> impactMatrix        = new HashMap<T, Map<S, Integer>>();
	
	/** The sum source changed. */
	private final Map<T, Integer>         sumSourceChanged    = new HashMap<T, Integer>();
	
	/** The sum source impacted. */
	private final Map<S, Integer>         sumSourceImpacted   = new HashMap<S, Integer>();
	
	/** The impact weighted churn. */
	private final Map<T, Long>            impactWeightedChurn = new HashMap<T, Long>();
	
	/**
	 * Adds the churn.
	 * 
	 * @param t
	 *            the t
	 */
	public void addChurn(final T t) {
		if (!sumSourceChanged.containsKey(t)) {
			sumSourceChanged.put(t, 1);
		} else {
			sumSourceChanged.put(t, sumSourceChanged.get(t) + 1);
		}
		
	}
	
	/**
	 * Adds the relation.
	 * 
	 * @param changedSource
	 *            the changed source
	 * @param impactedSource
	 *            the impacted source
	 * @param numDiff
	 *            the num diff
	 */
	@NoneNull
	public void addRelation(final T changedSource,
	                        final S impactedSource,
	                        final long numDiff) {
		if (!this.impactMatrix.containsKey(changedSource)) {
			this.impactMatrix.put(changedSource, new HashMap<S, Integer>());
		}
		Map<S, Integer> innerMap = this.impactMatrix.get(changedSource);
		if (!innerMap.containsKey(impactedSource)) {
			innerMap.put(impactedSource, 0);
		}
		innerMap.put(impactedSource, innerMap.get(impactedSource) + 1);
		
		if (!this.sumSourceChanged.containsKey(changedSource)) {
			this.sumSourceChanged.put(changedSource, 0);
		}
		this.sumSourceChanged.put(changedSource, this.sumSourceChanged.get(changedSource) + 1);
		
		if (!this.sumSourceImpacted.containsKey(impactedSource)) {
			this.sumSourceImpacted.put(impactedSource, 0);
		}
		this.sumSourceImpacted.put(impactedSource, this.sumSourceImpacted.get(impactedSource) + 1);
		
		if (!this.impactWeightedChurn.containsKey(changedSource)) {
			this.impactWeightedChurn.put(changedSource, 0l);
		}
		this.impactWeightedChurn.put(changedSource, this.impactWeightedChurn.get(changedSource) + numDiff);
	}
	
	/**
	 * Returns the number of changes applied to t.
	 * 
	 * @param t
	 *            the t
	 * @return the churn
	 */
	@NoneNull
	public int getChurn(final T t) {
		if (this.sumSourceChanged.containsKey(t)) {
			return this.sumSourceChanged.get(t);
		}
		return 0;
	}
	
	/**
	 * Return the quotient between the number of impact caused on s changing t
	 * divided by the number of total churn on t.
	 * 
	 * @param t
	 *            the t
	 * @param s
	 *            the s
	 * @return the impact confidence. A value between 0 and 1, where 1 means
	 *         that every change on t caused an impact on s.
	 */
	@NoneNull
	public double getImpactConfidence(final T t,
	                                  final S s) {
		int impactRelation = this.getImpactRelation(t, s);
		if (impactRelation < 1) {
			return 0d;
		}
		int churn = getChurn(t);
		if (churn < 1) {
			return 0d;
		}
		double result = (((double) impactRelation) / ((double) churn));
		Condition.check(result <= 1, "The impact confidence ust not be larger than 1, but was: " + result);
		return result;
	}
	
	/**
	 * Returns the number of times a change applied to t caused an execution
	 * difference in s (#distinct transactions).
	 * 
	 * @param t
	 *            the t
	 * @param s
	 *            the s
	 * @return the impact relation
	 */
	@NoneNull
	public int getImpactRelation(final T t,
	                             final S s) {
		if (!this.impactMatrix.containsKey(t)) {
			return 0;
		}
		Map<S, Integer> innerMap = this.impactMatrix.get(t);
		if (!innerMap.containsKey(s)) {
			return 0;
		}
		return innerMap.get(s);
	}
	
	/**
	 * Returns the number of total (not distinct) methods impacted over all
	 * changes on t.
	 * 
	 * @param t
	 *            the t
	 * @return the impact weighed churn
	 */
	@NoneNull
	public long getImpactWeighedChurn(final T t) {
		if (this.impactWeightedChurn.containsKey(t)) {
			return this.impactWeightedChurn.get(t);
		}
		return 0;
	}
	
	/**
	 * Return the number of execution differences registered for s.
	 * 
	 * @param s
	 *            the s
	 * @return the num impacted
	 */
	@NoneNull
	public int getNumImpacted(final S s) {
		if (this.sumSourceImpacted.containsKey(s)) {
			return this.sumSourceImpacted.get(s);
		}
		return 0;
	}
	
}

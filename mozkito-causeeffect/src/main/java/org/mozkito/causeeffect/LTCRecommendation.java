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
package org.mozkito.causeeffect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.joda.time.DateTime;

import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class LTCRecommendation.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class LTCRecommendation {
	
	/**
	 * The Enum ChangeProperty.
	 */
	public enum ChangeProperty {
		
		/** The fix. */
		FIX, 
 /** The bigchange. */
 BIGCHANGE, 
 /** The none. */
 NONE;
	}
	
	/** The recommendations. */
	private static Map<Long, Map<CTLFormula, LTCRecommendation>> recommendations = new HashMap<>();
	
	/**
	 * Adds the change.
	 *
	 * @param changedFile the changed file
	 * @param rCSTransaction the r cs transaction
	 */
	public static void addChange(final RCSFile changedFile,
	                             final RCSTransaction rCSTransaction) {
		if (!LTCRecommendation.recommendations.containsKey(changedFile.getGeneratedId())) {
			return;
		}
		for (final LTCRecommendation r : LTCRecommendation.recommendations.get(changedFile.getGeneratedId()).values()) {
			r.fileChanged(changedFile, rCSTransaction);
		}
	}
	
	/**
	 * Gets the recommendation.
	 *
	 * @param premise the premise
	 * @param formula the formula
	 * @return the recommendation
	 */
	public static LTCRecommendation getRecommendation(final RCSFile premise,
	                                                  final CTLFormula formula) {
		if (!LTCRecommendation.recommendations.containsKey(premise.getGeneratedId())) {
			LTCRecommendation.recommendations.put(premise.getGeneratedId(),
			                                      new HashMap<CTLFormula, LTCRecommendation>());
		}
		if (!LTCRecommendation.recommendations.get(premise.getGeneratedId()).containsKey(formula)) {
			LTCRecommendation.recommendations.get(premise.getGeneratedId())
			                                 .put(formula, new LTCRecommendation(premise, formula));
		}
		return LTCRecommendation.recommendations.get(premise.getGeneratedId()).get(formula);
	}
	
	/**
	 * Gets the recommendations.
	 *
	 * @param changedFile the changed file
	 * @param property the property
	 * @return the recommendations
	 */
	public static SortedSet<LTCRecommendation> getRecommendations(final RCSFile changedFile,
	                                                              final ChangeProperty property) {
		
		if (!LTCRecommendation.recommendations.containsKey(changedFile.getGeneratedId())) {
			return new TreeSet<LTCRecommendation>();
		}
		
		final SortedSet<LTCRecommendation> treeSet = new TreeSet<>(new LTCRecommendationComparator(property));
		treeSet.addAll(LTCRecommendation.recommendations.get(changedFile.getGeneratedId()).values());
		return treeSet;
	}
	
	/** The premise. */
	private final Long                                                premise;
	
	/** The formula. */
	private final CTLFormula                                          formula;
	
	/** The support. */
	private final Map<ChangeProperty, Queue<Tuple<String, DateTime>>> support        = new HashMap<>();
	
	/** The premise changes. */
	private final Queue<Tuple<String, DateTime>>                      premiseChanges = new LinkedList<>();
	
	/**
	 * Instantiates a new lTC recommendation.
	 *
	 * @param premise the premise
	 * @param formula the formula
	 */
	@NoneNull
	private LTCRecommendation(final RCSFile premise, final CTLFormula formula) {
		this.premise = premise.getGeneratedId();
		this.formula = formula;
		this.support.put(ChangeProperty.NONE, new LinkedList<Tuple<String, DateTime>>());
	}
	
	/**
	 * Adds the support.
	 *
	 * @param rCSTransaction the r cs transaction
	 * @param property the property
	 * @param expiry the expiry
	 */
	public void addSupport(final RCSTransaction rCSTransaction,
	                       final ChangeProperty property,
	                       final DateTime expiry) {
		if (!this.support.containsKey(property)) {
			this.support.put(property, new LinkedList<Tuple<String, DateTime>>());
		}
		
		this.support.get(ChangeProperty.NONE).add(new Tuple<String, DateTime>(rCSTransaction.getId(),
		                                                                      rCSTransaction.getTimestamp()));
		Tuple<String, DateTime> supportEntry = this.support.get(ChangeProperty.NONE).peek();
		while ((supportEntry != null) && supportEntry.getSecond().isBefore(expiry)) {
			supportEntry = this.support.get(ChangeProperty.NONE).poll();
		}
		Tuple<String, DateTime> premiseEntry = this.premiseChanges.peek();
		while ((premiseEntry != null) && premiseEntry.getSecond().isBefore(expiry)) {
			premiseEntry = this.premiseChanges.poll();
		}
		
		if (!property.equals(ChangeProperty.NONE)) {
			this.support.get(property).add(new Tuple<String, DateTime>(rCSTransaction.getId(),
			                                                           rCSTransaction.getTimestamp()));
			supportEntry = this.support.get(property).peek();
			while ((supportEntry != null) && supportEntry.getSecond().isBefore(expiry)) {
				supportEntry = this.support.get(property).poll();
			}
		}
	}
	
	/**
	 * File changed.
	 *
	 * @param rCSFile the r cs file
	 * @param rCSTransaction the r cs transaction
	 */
	public void fileChanged(final RCSFile rCSFile,
	                        final RCSTransaction rCSTransaction) {
		if (this.premise.equals(rCSFile.getGeneratedId())) {
			this.premiseChanges.add(new Tuple<String, DateTime>(rCSTransaction.getId(), rCSTransaction.getTimestamp()));
		}
	}
	
	/**
	 * Gets the confidence.
	 *
	 * @param property the property
	 * @return the confidence
	 */
	public double getConfidence(final ChangeProperty property) {
		if (this.premiseChanges.isEmpty()) {
			return 0;
		}
		return (getSupport(property) / (double) this.premiseChanges.size());
	}
	
	/**
	 * Gets the formula.
	 *
	 * @return the formula
	 */
	public CTLFormula getFormula() {
		return this.formula;
	}
	
	/**
	 * Gets the support.
	 *
	 * @param property the property
	 * @return the support
	 */
	public int getSupport(final ChangeProperty property) {
		if (!this.support.containsKey(property)) {
			return 0;
		}
		return this.support.get(property).size();
	}
	
}

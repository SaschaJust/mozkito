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
package de.unisaarland.cs.st.reposuite.ltc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLFormula;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class LTCRecommendation {
	
	public enum ChangeProperty {
		FIX, BIGCHANGE, NONE;
	}
	
	private static Map<Long, Map<CTLFormula, LTCRecommendation>> recommendations = new HashMap<>();
	
	/**
	 * @param changedFile
	 */
	public static void addChange(final RCSFile changedFile,
	                             final RCSTransaction transaction) {
		if (!recommendations.containsKey(changedFile)) {
			return;
		}
		for (final LTCRecommendation r : recommendations.get(changedFile).values()) {
			r.fileChanged(changedFile, transaction);
		}
	}
	
	public static LTCRecommendation getRecommendation(final RCSFile premise,
	                                                  final CTLFormula formula) {
		if (!recommendations.containsKey(premise)) {
			recommendations.put(premise.getGeneratedId(), new HashMap<CTLFormula, LTCRecommendation>());
		}
		if (!recommendations.get(premise).containsKey(formula)) {
			recommendations.get(premise).put(formula, new LTCRecommendation(premise, formula));
		}
		return recommendations.get(premise).get(formula);
	}
	
	public static SortedSet<LTCRecommendation> getRecommendations(final RCSFile changedFile,
	                                                              final ChangeProperty property) {
		
		if (!recommendations.containsKey(changedFile)) {
			return new TreeSet<LTCRecommendation>();
		}
		
		final SortedSet<LTCRecommendation> treeSet = new TreeSet<>(new LTCRecommendationComparator(property));
		treeSet.addAll(recommendations.get(changedFile).values());
		return treeSet;
	}
	
	private final RCSFile                                             premise;
	
	private final CTLFormula                                          formula;
	
	private final Map<ChangeProperty, Queue<Tuple<String, DateTime>>> support        = new HashMap<>();
	
	private final Queue<Tuple<String, DateTime>>                      premiseChanges = new LinkedList<>();
	
	/**
	 * @param premise
	 * @param formula
	 */
	@NoneNull
	private LTCRecommendation(final RCSFile premise, final CTLFormula formula) {
		this.premise = premise;
		this.formula = formula;
		this.support.put(ChangeProperty.NONE, new LinkedList<Tuple<String, DateTime>>());
	}
	
	public void addSupport(final RCSTransaction transaction,
	                       final ChangeProperty property,
	                       final DateTime expiry) {
		if (!this.support.containsKey(property)) {
			this.support.put(property, new LinkedList<Tuple<String, DateTime>>());
		}
		
		this.support.get(ChangeProperty.NONE).add(new Tuple<String, DateTime>(transaction.getId(),
		                                                                      transaction.getTimestamp()));
		Tuple<String, DateTime> supportEntry = this.support.get(ChangeProperty.NONE).peek();
		while ((supportEntry != null) && supportEntry.getSecond().isBefore(expiry)) {
			supportEntry = this.support.get(ChangeProperty.NONE).poll();
		}
		Tuple<String, DateTime> premiseEntry = this.premiseChanges.peek();
		while ((premiseEntry != null) && premiseEntry.getSecond().isBefore(expiry)) {
			premiseEntry = this.premiseChanges.poll();
		}
		
		if (!property.equals(ChangeProperty.NONE)) {
			this.support.get(property)
			            .add(new Tuple<String, DateTime>(transaction.getId(), transaction.getTimestamp()));
			supportEntry = this.support.get(property).peek();
			while ((supportEntry != null) && supportEntry.getSecond().isBefore(expiry)) {
				supportEntry = this.support.get(property).poll();
			}
		}
	}
	
	public void fileChanged(final RCSFile file,
	                        final RCSTransaction transaction) {
		if (this.premise.equals(file)) {
			this.premiseChanges.add(new Tuple<String, DateTime>(transaction.getId(), transaction.getTimestamp()));
		}
	}
	
	/**
	 * @return
	 */
	public double getConfidence(final ChangeProperty property) {
		if (this.premiseChanges.isEmpty()) {
			return 0;
		}
		return (getSupport(property) / (double) this.premiseChanges.size());
	}
	
	public CTLFormula getFormula() {
		return this.formula;
	}
	
	/**
	 * @return
	 */
	public int getSupport(final ChangeProperty property) {
		if (!this.support.containsKey(property)) {
			return 0;
		}
		return this.support.get(property).size();
	}
	
}

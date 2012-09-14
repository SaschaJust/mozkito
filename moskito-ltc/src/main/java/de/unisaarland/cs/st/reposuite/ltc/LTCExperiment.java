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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.utils.VertexSelector;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.ltc.LTCRecommendation.ChangeProperty;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLFormula;
import de.unisaarland.cs.st.reposuite.ltc.kripke.KripkeStructure;
import de.unisaarland.cs.st.reposuite.ltc.kripke.Label;
import de.unisaarland.cs.st.reposuite.ltc.kripke.LabelGenerator;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class LTCExperiment {
	
	public static boolean isBigChange(final RCSTransaction t) {
		if (t.getChangedFiles().size() > 19) {
			return true;
		}
		return false;
	}
	
	public static boolean isBugFix(final RCSTransaction t) {
		return false;
	}
	
	private final ChangeGenealogy<RCSTransaction> genealogy;
	
	private final LTCFormulaFactory               formulaFactory;
	private int                                   truePositives            = 0;
	private int                                   falsePositives           = 0;
	private int                                   sumLowestRank            = 0;
	private int                                   numVertices              = 0;
	private int                                   numRecommendatedVertices = 0;
	private final double                          minConfidence;
	private final int                             timeWindow;
	private final int                             minSupport;
	
	private final int                             keepFormulaMaxDays;
	
	@NoneNull
	public LTCExperiment(final ChangeGenealogy<RCSTransaction> genealogy, final LTCFormulaFactory formulaFactory,
	        final int minSupport, final double minConfidence, final int keepFormulaMaxDays, final int timeWindow) {
		this.genealogy = genealogy;
		this.formulaFactory = formulaFactory;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.keepFormulaMaxDays = keepFormulaMaxDays;
		this.timeWindow = timeWindow;
	}
	
	public double getAverageLowestRank() {
		return ((double) this.sumLowestRank / ((double) this.numVertices));
	}
	
	public int getNumRecommendatedVertices() {
		return this.numRecommendatedVertices;
	}
	
	public double getPrecision() {
		return (this.truePositives / (((double) this.truePositives) + ((double) this.falsePositives)));
	}
	
	@NoneNull
	public void run(final TransactionSet transactions,
	                final int trainSize,
	                final boolean includeInnerRules) {
		
		if (Logger.logInfo()) {
			Logger.info("Training phase.");
		}
		
		final Iterator<RCSTransaction> transactionIter = transactions.iterator();
		
		for (int i = 0; ((i < trainSize) && transactionIter.hasNext()); ++i) {
			train(transactionIter.next(), includeInnerRules);
		}
		if (Logger.logInfo()) {
			Logger.info("Testing phase.");
		}
		while (transactionIter.hasNext()) {
			final RCSTransaction testInstance = transactionIter.next();
			test(testInstance);
			train(testInstance, includeInnerRules);
		}
		if (Logger.logInfo()) {
			Logger.info("Status of inner change set rules was: %s.", String.valueOf(includeInnerRules));
		}
		
		if (Logger.logInfo()) {
			Logger.info("Produced recommendations for %d change sets.", getNumRecommendatedVertices());
		}
		if (Logger.logInfo()) {
			Logger.info("Average lowest rank is: %d.", getAverageLowestRank());
		}
		if (Logger.logInfo()) {
			Logger.info("Precision is: %d.", getPrecision());
		}
	}
	
	@NoneNull
	public void test(final RCSTransaction t) {
		
		if (Logger.logDebug()) {
			Logger.debug("Recommending LTC changes for transaction %s.", t.getId());
		}
		
		++this.numVertices;
		if (!this.genealogy.containsVertex(t)) {
			throw new NoSuchElementException(String.format("Transaction %s could not be found in genealogy graph.",
			                                               t.toString()));
		}
		
		final LabelGenerator<RCSTransaction> labelGenerator = new LabelGenerator<RCSTransaction>() {
			
			@Override
			public Collection<Label> getLabels(final RCSTransaction t) {
				final Collection<Label> labels = new HashSet<Label>();
				for (final RCSFile file : t.getChangedFiles()) {
					labels.add(Label.getLabel(file));
				}
				return labels;
			}
		};
		
		// generate Kripke Structure
		final KripkeStructure<RCSTransaction> kripkeStructure = KripkeStructure.createFrom(this.genealogy,
		                                                                                   t,
		                                                                                   labelGenerator,
		                                                                                   new VertexSelector<RCSTransaction>() {
			                                                                                   
			                                                                                   @Override
			                                                                                   public boolean selectVertex(final RCSTransaction vertex) {
				                                                                                   if (Math.abs(Days.daysBetween(t.getTimestamp(),
				                                                                                                                 vertex.getTimestamp())
				                                                                                                    .getDays()) < LTCExperiment.this.timeWindow) {
					                                                                                   return true;
				                                                                                   }
				                                                                                   return false;
			                                                                                   }
		                                                                                   });
		
		ChangeProperty changeProperty = ChangeProperty.NONE;
		if (isBugFix(t)) {
			changeProperty = ChangeProperty.FIX;
		} else if (isBigChange(t)) {
			changeProperty = ChangeProperty.BIGCHANGE;
		}
		
		// make recommendations and register results
		final SortedSet<LTCRecommendation> recommendations = new TreeSet<>();
		for (final RCSFile changedFile : t.getChangedFiles()) {
			final SortedSet<LTCRecommendation> fileRecommends = LTCRecommendation.getRecommendations(changedFile,
			                                                                                         changeProperty);
			final Iterator<LTCRecommendation> fileRecommendIterator = fileRecommends.iterator();
			int added = 0;
			while (fileRecommendIterator.hasNext()) {
				final LTCRecommendation r = fileRecommendIterator.next();
				if ((r.getSupport(changeProperty) > this.minSupport)
				        && (r.getConfidence(changeProperty) > this.minConfidence)) {
					recommendations.add(r);
					++added;
				}
				if (added > 2) {
					break;
				}
			}
			
		}
		
		final Iterator<LTCRecommendation> rIterator = recommendations.iterator();
		int lowestRank = 4;
		
		for (int i = 0; ((i < 3) && rIterator.hasNext()); ++i) {
			final LTCRecommendation recommendation = rIterator.next();
			final boolean valid = recommendation.getFormula().modelCheck(kripkeStructure);
			if (valid) {
				++this.truePositives;
				if (lowestRank > i) {
					lowestRank = i;
				}
			} else {
				++this.falsePositives;
			}
		}
		if (lowestRank < 4) {
			this.sumLowestRank += lowestRank;
			++this.numRecommendatedVertices;
		}
		
	}
	
	@NoneNull
	public void train(final RCSTransaction t,
	                  final boolean inner) {
		// generate formulas to be added for this vertex
		
		if (Logger.logDebug()) {
			Logger.debug("Learning LTC rules from transaction %s.", t.getId());
		}
		
		final Collection<CTLFormula> templateFormulas = this.formulaFactory.generateFormulas(this.genealogy,
		                                                                                     t,
		                                                                                     new VertexSelector<RCSTransaction>() {
			                                                                                     
			                                                                                     @Override
			                                                                                     public boolean selectVertex(final RCSTransaction vertex) {
				                                                                                     if (Math.abs(Days.daysBetween(t.getTimestamp(),
				                                                                                                                   vertex.getTimestamp())
				                                                                                                      .getDays()) < LTCExperiment.this.keepFormulaMaxDays) {
					                                                                                     return true;
				                                                                                     }
				                                                                                     return false;
			                                                                                     }
		                                                                                     });
		ChangeProperty changeProperty = ChangeProperty.NONE;
		if (isBugFix(t)) {
			changeProperty = ChangeProperty.FIX;
		} else if (isBigChange(t)) {
			changeProperty = ChangeProperty.BIGCHANGE;
		}
		
		// add support for these formulas
		for (final CTLFormula f : templateFormulas) {
			for (final RCSFile changedFile : t.getChangedFiles()) {
				LTCRecommendation.getRecommendation(changedFile, f).addSupport(t,
				                                                               changeProperty,
				                                                               t.getTimestamp()
				                                                                .minusDays(this.keepFormulaMaxDays));
			}
		}
		
		if (inner) {
			final Collection<CTLFormula> innerFormulas = this.formulaFactory.generateInnerTransactionFormulas(this.genealogy,
			                                                                                                  t);
			// add support for these formulas
			for (final CTLFormula f : innerFormulas) {
				if (!templateFormulas.contains(f)) {
					for (final RCSFile changedFile : t.getChangedFiles()) {
						LTCRecommendation.getRecommendation(changedFile, f)
						                 .addSupport(t, changeProperty,
						                             t.getTimestamp().minusDays(this.keepFormulaMaxDays));
					}
				}
			}
		}
		
		// mark file as changed in recommendations
		for (final RCSFile changedFile : t.getChangedFiles()) {
			LTCRecommendation.addChange(changedFile, t);
		}
	}
	
}

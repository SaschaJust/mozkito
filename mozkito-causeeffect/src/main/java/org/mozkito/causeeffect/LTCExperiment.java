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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.Days;

import org.mozkito.causeeffect.LTCRecommendation.ChangeProperty;
import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.causeeffect.kripke.KripkeStructure;
import org.mozkito.causeeffect.kripke.Label;
import org.mozkito.causeeffect.kripke.LabelGenerator;
import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.utils.VertexSelector;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSRevision;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class LTCExperiment.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class LTCExperiment {
	
	/**
	 * Checks if is big change.
	 * 
	 * @param t
	 *            the t
	 * @return true, if is big change
	 */
	public static boolean isBigChange(final RCSTransaction t) {
		if (t.getChangedFiles().size() > 19) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is bug fix.
	 * 
	 * @param t
	 *            the t
	 * @return true, if is bug fix
	 */
	public static boolean isBugFix(final RCSTransaction t) {
		return false;
	}
	
	/** The genealogy. */
	private final ChangeGenealogy<RCSTransaction> genealogy;
	
	/** The formula factory. */
	private final LTCFormulaFactory               formulaFactory;
	
	/** The true positives. */
	private int                                   truePositives            = 0;
	
	/** The false positives. */
	private int                                   falsePositives           = 0;
	
	/** The sum lowest rank. */
	private int                                   sumLowestRank            = 0;
	
	/** The num vertices. */
	private int                                   numVertices              = 0;
	
	/** The num recommendated vertices. */
	private int                                   numRecommendatedVertices = 0;
	
	/** The min confidence. */
	private final double                          minConfidence;
	
	/** The time window. */
	private final int                             timeWindow;
	
	/** The min support. */
	private final int                             minSupport;
	
	/** The change set size stat. */
	private final DescriptiveStatistics           changeSetSizeStat        = new DescriptiveStatistics();
	
	/** The keep formula max days. */
	private final int                             keepFormulaMaxDays;
	
	/** The num recommendations. */
	private final int                             numRecommendations;
	
	/**
	 * Instantiates a new lTC experiment.
	 * 
	 * @param genealogy
	 *            the genealogy
	 * @param formulaFactory
	 *            the formula factory
	 * @param minSupport
	 *            the min support
	 * @param minConfidence
	 *            the min confidence
	 * @param keepFormulaMaxDays
	 *            the keep formula max days
	 * @param timeWindow
	 *            the time window
	 * @param numRecommendations
	 *            the num recommendations
	 */
	@NoneNull
	public LTCExperiment(final ChangeGenealogy<RCSTransaction> genealogy, final LTCFormulaFactory formulaFactory,
	        final int minSupport, final double minConfidence, final int keepFormulaMaxDays, final int timeWindow,
	        final int numRecommendations) {
		this.genealogy = genealogy;
		this.formulaFactory = formulaFactory;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.keepFormulaMaxDays = keepFormulaMaxDays;
		this.timeWindow = timeWindow;
		this.numRecommendations = numRecommendations;
	}
	
	/**
	 * Gets the average lowest rank.
	 * 
	 * @return the average lowest rank
	 */
	public double getAverageLowestRank() {
		return ((double) this.sumLowestRank / ((double) this.numVertices));
	}
	
	/**
	 * Gets the num recommendated vertices.
	 * 
	 * @return the num recommendated vertices
	 */
	public int getNumRecommendatedVertices() {
		return this.numRecommendatedVertices;
	}
	
	/**
	 * Gets the precision.
	 * 
	 * @return the precision
	 */
	public double getPrecision() {
		return (this.truePositives / (((double) this.truePositives) + ((double) this.falsePositives)));
	}
	
	/**
	 * Run.
	 * 
	 * @param trainingSet
	 *            the training set
	 * @param testingSet
	 *            the testing set
	 * @param includeInnerRules
	 *            the include inner rules
	 */
	@NoneNull
	public void run(final List<RCSTransaction> trainingSet,
	                final List<RCSTransaction> testingSet,
	                final boolean includeInnerRules) {
		
		if (Logger.logInfo()) {
			Logger.info("Training phase.");
		}
		
		for (final RCSTransaction trainingInstance : trainingSet) {
			train(trainingInstance, includeInnerRules);
		}
		if (Logger.logInfo()) {
			Logger.info("Testing phase.");
		}
		for (final RCSTransaction testInstance : testingSet) {
			test(testInstance);
			train(testInstance, includeInnerRules);
		}
		if (Logger.logInfo()) {
			Logger.info("Status of inner change set rules was: %s.", String.valueOf(includeInnerRules));
		}
		
		if (Logger.logInfo()) {
			Logger.info("Produced recommendations for %s change sets.", String.valueOf(getNumRecommendatedVertices()));
		}
		if (Logger.logInfo()) {
			Logger.info("Average lowest rank is: %s.", String.valueOf(getAverageLowestRank()));
		}
		if (Logger.logInfo()) {
			Logger.info("Precision is: %s.", String.valueOf(getPrecision()));
		}
	}
	
	/**
	 * Test.
	 * 
	 * @param t
	 *            the t
	 */
	@NoneNull
	public void test(final RCSTransaction t) {
		
		if ((t.getRevisions().size() > 10) && (t.getRevisions().size() >= this.changeSetSizeStat.getPercentile(75))) {
			if (Logger.logInfo()) {
				Logger.info("NOT recommeding LTC changes for transaction %s. Too many changed files: %s.", t.getId(),
				            String.valueOf(t.getRevisions().size()));
			}
		}
		
		if (Logger.logInfo()) {
			Logger.info("Recommending LTC changes for transaction %s.", t.getId());
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
				for (final RCSFile rCSFile : t.getChangedFiles()) {
					labels.add(Label.getLabel(rCSFile));
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
		final SortedSet<LTCRecommendation> recommendations = new TreeSet<>(
		                                                                   new LTCRecommendationComparator(
		                                                                                                   changeProperty));
		for (final RCSRevision rCSRevision : t.getRevisions()) {
			if (rCSRevision.getChangeType().equals(ChangeType.Deleted)) {
				continue;
			}
			final RCSFile changedFile = rCSRevision.getChangedFile();
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
		if (Logger.logDebug()) {
			Logger.debug("Produced %s recommendations.", String.valueOf(recommendations.size()));
		}
		final Iterator<LTCRecommendation> rIterator = recommendations.iterator();
		int lowestRank = 4;
		
		for (int i = 0; ((i < this.numRecommendations) && rIterator.hasNext()); ++i) {
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
	
	/**
	 * Train.
	 * 
	 * @param t
	 *            the t
	 * @param inner
	 *            the inner
	 */
	@NoneNull
	public void train(final RCSTransaction t,
	                  final boolean inner) {
		// generate formulas to be added for this vertex
		
		if ((t.getRevisions().size() > 10) && (t.getRevisions().size() >= this.changeSetSizeStat.getPercentile(75))) {
			if (Logger.logDebug()) {
				Logger.debug("Ignoring LTC rules from transaction %s. Change set size exceeds 3/4-percintile of median change set size.",
				             t.getId());
			}
			this.changeSetSizeStat.addValue(t.getRevisions().size());
			return;
		}
		this.changeSetSizeStat.addValue(t.getRevisions().size());
		
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
			for (final RCSRevision rCSRevision : t.getRevisions()) {
				if (rCSRevision.getChangeType().equals(ChangeType.Deleted)) {
					continue;
				}
				final RCSFile changedFile = rCSRevision.getChangedFile();
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

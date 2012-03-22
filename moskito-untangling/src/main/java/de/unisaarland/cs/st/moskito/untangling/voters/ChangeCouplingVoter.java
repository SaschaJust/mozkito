/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.voters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.changecouplings.ChangeCouplingRuleFactory;
import de.unisaarland.cs.st.moskito.changecouplings.model.MethodChangeCoupling;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClustering;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class ChangeCouplingVoter.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplingVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The couplings. */
	private LinkedList<MethodChangeCoupling> couplings;
	
	/** The transaction. */
	private final RCSTransaction             transaction;
	
	/** The min support. */
	private final int                        minSupport;
	
	/** The min confidence. */
	private final double                     minConfidence;
	
	/** The persistence util. */
	private final PersistenceUtil            persistenceUtil;
	
	/**
	 * Instantiates a new change coupling voter.
	 *
	 * @param transaction the transaction
	 * @param minSupport the min support
	 * @param minConfidence the min confidence
	 * @param persistenceUtil the persistence util
	 * @param cacheDir the cache dir
	 */
	
	@SuppressWarnings ("unchecked")
	public ChangeCouplingVoter(@NotNull final RCSTransaction transaction, final int minSupport,
	        final double minConfidence, @NotNull final PersistenceUtil persistenceUtil, final File cacheDir) {
		
		this.transaction = transaction;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory())) {
			File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + transaction.getId()
			        + ".cc");
			if (serialFile.exists()) {
				// load serial file
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					couplings = (LinkedList<MethodChangeCoupling>) in.readObject();
				} catch (FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (IOException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
			}
			if (couplings == null) {
				// run query and save tmp file
				couplings = ChangeCouplingRuleFactory.getMethodChangeCouplings(transaction, minSupport, minConfidence,
				                                                               new HashSet<String>(), persistenceUtil);
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
					out.writeObject(couplings);
					out.close();
				} catch (FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				} catch (IOException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	@NoneNull
	public double getScore(final JavaChangeOperation t1,
	                       final JavaChangeOperation t2) {
		double score = 0d;
		
		Condition.check(t1.getChangedElementLocation() != null, "The changed element location must not be null!");
		Condition.check(t2.getChangedElementLocation() != null, "The changed element location must not be null!");
		
		JavaElement element1 = t1.getChangedElementLocation().getElement();
		JavaElement element2 = t2.getChangedElementLocation().getElement();
		
		Condition.check(element1 != null, "The changed elements must not be null!");
		Condition.check(element2 != null, "The changed elements must not be null!");
		Condition.check(element1.getElementType().equals(element2.getElementType()),
		                "The change operations must be on the same types of elements");
		
		if (!element1.getElementType().equals(JavaMethodDefinition.class.getCanonicalName())) {
			if (Logger.logWarn()) {
				Logger.warn("ChangeCouplingVoter does not support change operations on element type "
				        + element1.getElementType() + ". Returning 0.");
			}
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		// get relevant method names
		Set<String> relevantMethodNames = new HashSet<String>();
		relevantMethodNames.add(element1.getFullQualifiedName());
		relevantMethodNames.add(element2.getFullQualifiedName());
		
		List<MethodChangeCoupling> currentCouplings = new LinkedList<MethodChangeCoupling>();
		
		if (couplings != null) {
			for (MethodChangeCoupling cc : couplings) {
				if (((cc.getPremise().size() == 1) && (cc.getPremise().contains(element1)) && (cc.getImplication().equals(element2)))
				        || ((cc.getPremise().size() == 1) && (cc.getPremise().contains(element2)) && (cc.getImplication().equals(element1)))) {
					currentCouplings.add(cc);
				}
				
			}
		} else {
			currentCouplings = ChangeCouplingRuleFactory.getMethodChangeCouplings(transaction, minSupport,
			                                                                      minConfidence, relevantMethodNames,
			                                                                      persistenceUtil);
		}
		
		if (!currentCouplings.isEmpty()) {
			Collections.sort(currentCouplings, new Comparator<MethodChangeCoupling>() {
				
				@Override
				public int compare(final MethodChangeCoupling c1,
				                   final MethodChangeCoupling c2) {
					return c1.getConfidence().compareTo(c2.getConfidence());
				}
				
			});
			score = currentCouplings.get(0).getConfidence();
		}
		Condition.check(score <= 1d, "The returned distance must be a value between 0 and 1, but was: " + score);
		Condition.check(score >= 0d, "The returned distance must be a value between 0 and 1, but was: " + score);
		return score;
		
	}
}

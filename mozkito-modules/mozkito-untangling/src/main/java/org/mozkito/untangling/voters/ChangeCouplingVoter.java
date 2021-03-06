/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.untangling.voters;

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

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.changecouplings.ChangeCouplingRuleFactory;
import org.mozkito.changecouplings.model.MethodChangeCoupling;
import org.mozkito.changecouplings.model.SerialMethodChangeCoupling;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utilities.clustering.MultilevelClustering;
import org.mozkito.utilities.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ChangeCouplingVoter.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeCouplingVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/**
	 * The Class Factory.
	 */
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<ChangeCouplingVoter> {
		
		/** The min support. */
		private final int             minSupport;
		
		/** The min confidence. */
		private final double          minConfidence;
		
		/** The persistence util. */
		private final PersistenceUtil persistenceUtil;
		
		/** The cache dir. */
		private final File            cacheDir;
		
		/**
		 * Instantiates a new factory.
		 * 
		 * @param minSupport
		 *            the min support
		 * @param minConfidence
		 *            the min confidence
		 * @param persistenceUtil
		 *            the persistence util
		 * @param cacheDir
		 *            the cache dir
		 */
		public Factory(final int minSupport, final double minConfidence,
		        @NotNull final PersistenceUtil persistenceUtil, final File cacheDir) {
			this.minSupport = minSupport;
			this.minConfidence = minConfidence;
			this.persistenceUtil = persistenceUtil;
			this.cacheDir = cacheDir;
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#createVoter(org.mozkito.versions.model
		 * .ChangeSet)
		 */
		@Override
		public ChangeCouplingVoter createVoter(final ChangeSet changeset) {
			return new ChangeCouplingVoter(changeset, this.minSupport, this.minConfidence, this.persistenceUtil,
			                               this.cacheDir);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory#getVoterName()
		 */
		@Override
		public String getVoterName() {
			// PRECONDITIONS
			
			try {
				return ChangeCouplingVoter.class.getSimpleName();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The couplings. */
	private LinkedList<MethodChangeCoupling> couplings;
	
	/** The transaction. */
	private final ChangeSet                  changeset;
	
	/** The min support. */
	private final int                        minSupport;
	
	/** The min confidence. */
	private final double                     minConfidence;
	
	/** The persistence util. */
	private final PersistenceUtil            persistenceUtil;
	
	/**
	 * Instantiates a new change coupling voter.
	 * 
	 * @param changeset
	 *            the transaction
	 * @param minSupport
	 *            the min support
	 * @param minConfidence
	 *            the min confidence
	 * @param persistenceUtil
	 *            the persistence util
	 * @param cacheDir
	 *            the cache dir
	 */
	
	@SuppressWarnings ("unchecked")
	public ChangeCouplingVoter(@NotNull final ChangeSet changeset, final int minSupport, final double minConfidence,
	        @NotNull final PersistenceUtil persistenceUtil, final File cacheDir) {
		
		this.changeset = changeset;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory())) {
			final File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + changeset.getId()
			        + ".cc");
			if (serialFile.exists()) {
				// load serial file
				try {
					final ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					final LinkedList<SerialMethodChangeCoupling> serialCouplings = (LinkedList<SerialMethodChangeCoupling>) in.readObject();
					this.couplings = new LinkedList<>();
					for (final SerialMethodChangeCoupling serialC : serialCouplings) {
						this.couplings.add(serialC.unserialize(persistenceUtil));
					}
					in.close();
				} catch (final FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final ClassNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
			if (this.couplings == null) {
				// run query and save tmp file
				this.couplings = ChangeCouplingRuleFactory.getMethodChangeCouplings(changeset, minSupport,
				                                                                    minConfidence,
				                                                                    new HashSet<String>(),
				                                                                    persistenceUtil);
				try {
					final LinkedList<SerialMethodChangeCoupling> serialCouplings = new LinkedList<>();
					for (final MethodChangeCoupling c : this.couplings) {
						serialCouplings.add(new SerialMethodChangeCoupling(c));
					}
					final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serialFile));
					out.writeObject(serialCouplings);
					out.close();
				} catch (final FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				} catch (final IOException e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor#close()
	 */
	@Override
	public void close() {
		return;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getClassName() {
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getMaxPossibleScore()
	 */
	@Override
	public double getMaxPossibleScore() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object, java.lang.Object)
	 */
	@Override
	@NoneNull
	public double getScore(final JavaChangeOperation t1,
	                       final JavaChangeOperation t2) {
		
		double score = 0d;
		
		Condition.check(t1.getChangedElementLocation() != null, "The changed element location must not be null!");
		Condition.check(t2.getChangedElementLocation() != null, "The changed element location must not be null!");
		
		final JavaElement element1 = t1.getChangedElementLocation().getElement();
		final JavaElement element2 = t2.getChangedElementLocation().getElement();
		
		Condition.notNull(element1, "Local variable '%s' in '%s:%s'.", "element1", getClassName(), "getScore"); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(element2, "Local variable '%s' in '%s:%s'.", "element2", getClassName(), "getScore"); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (!element1.getElementType().equals(element2.getElementType())) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		if (!element1.getElementType().equals(JavaMethodDefinition.class.getCanonicalName())) {
			return MultilevelClustering.IGNORE_SCORE;
		}
		
		// get relevant method names
		final Set<String> relevantMethodNames = new HashSet<String>();
		relevantMethodNames.add(element1.getFullQualifiedName());
		relevantMethodNames.add(element2.getFullQualifiedName());
		
		List<MethodChangeCoupling> currentCouplings = new LinkedList<MethodChangeCoupling>();
		
		if (this.couplings != null) {
			for (final MethodChangeCoupling cc : this.couplings) {
				if (((cc.getPremise().size() == 1) && (cc.getPremise().contains(element1)) && (cc.getImplication().equals(element2)))
				        || ((cc.getPremise().size() == 1) && (cc.getPremise().contains(element2)) && (cc.getImplication().equals(element1)))) {
					currentCouplings.add(cc);
				}
				
			}
		} else {
			currentCouplings = ChangeCouplingRuleFactory.getMethodChangeCouplings(this.changeset, this.minSupport,
			                                                                      this.minConfidence,
			                                                                      relevantMethodNames,
			                                                                      this.persistenceUtil);
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
		} else {
			return MultilevelClustering.IGNORE_SCORE;
		}
		Condition.check(score <= 1d, "The returned distance must be a value between 0 and 1, but was: " + score);
		Condition.check(score >= 0d, "The returned distance must be a value between 0 and 1, but was: " + score);
		return score;
		
	}
}

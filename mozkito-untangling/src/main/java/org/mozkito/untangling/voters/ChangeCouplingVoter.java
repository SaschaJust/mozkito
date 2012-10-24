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
 *******************************************************************************/
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozkito.changecouplings.ChangeCouplingRuleFactory;
import org.mozkito.changecouplings.model.MethodChangeCoupling;
import org.mozkito.changecouplings.model.SerialMethodChangeCoupling;
import org.mozkito.clustering.MultilevelClustering;
import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class ChangeCouplingVoter.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplingVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	public static class Factory extends MultilevelClusteringScoreVisitorFactory<ChangeCouplingVoter> {
		
		private final int             minSupport;
		private final double          minConfidence;
		private final PersistenceUtil persistenceUtil;
		private final File            cacheDir;
		
		protected Factory(final int minSupport, final double minConfidence,
		        @NotNull final PersistenceUtil persistenceUtil, final File cacheDir) {
			this.minSupport = minSupport;
			this.minConfidence = minConfidence;
			this.persistenceUtil = persistenceUtil;
			this.cacheDir = cacheDir;
		}
		
		@Override
		public ChangeCouplingVoter createVoter(final RCSTransaction transaction) {
			return new ChangeCouplingVoter(transaction, this.minSupport, this.minConfidence, this.persistenceUtil,
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
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<ChangeCouplingVoter.Factory, ArgumentSet<ChangeCouplingVoter.Factory, Options>> {
		
		private net.ownhero.dev.hiari.settings.LongArgument.Options      minSupportOptions;
		private net.ownhero.dev.hiari.settings.DoubleArgument.Options    minConfidenceOptions;
		private net.ownhero.dev.hiari.settings.DirectoryArgument.Options cacheDirOptions;
		private final DatabaseOptions                                    databaseOptions;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
		        final DatabaseOptions databaseOptions) {
			super(argumentSet, "changeCouplingVoter", "ChangeCouplingVoter options.", requirements);
			this.databaseOptions = databaseOptions;
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ChangeCouplingVoter.Factory init() {
			// PRECONDITIONS
			
			final Long minSupport = getSettings().getArgument(this.minSupportOptions).getValue();
			final Double minConfidence = getSettings().getArgument(this.minConfidenceOptions).getValue();
			final File cacheDir = getSettings().getArgument(this.cacheDirOptions).getValue();
			final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			
			return new ChangeCouplingVoter.Factory(minSupport.intValue(), minConfidence.doubleValue(), persistenceUtil,
			                                       cacheDir);
			
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			this.minSupportOptions = new LongArgument.Options(
			                                                  argumentSet,
			                                                  "minSupport",
			                                                  "Set the minimum support for used change couplings to this value",
			                                                  3l, Requirement.required);
			map.put(this.minSupportOptions.getName(), this.minSupportOptions);
			
			this.minConfidenceOptions = new DoubleArgument.Options(
			                                                       argumentSet,
			                                                       "minConfidence",
			                                                       "Set minimum confidence for used change couplings to this value",
			                                                       0.7d, Requirement.required);
			map.put(this.minConfidenceOptions.getName(), this.minConfidenceOptions);
			
			this.cacheDirOptions = new DirectoryArgument.Options(
			                                                     argumentSet,
			                                                     "cacheDir",
			                                                     "Cache directory containing change coupling pre-computations using the naming converntion <transactionId>.cc",
			                                                     null, Requirement.required, false);
			map.put(this.cacheDirOptions.getName(), this.cacheDirOptions);
			
			return map;
		}
	}
	
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
	 * @param transaction
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
	public ChangeCouplingVoter(@NotNull final RCSTransaction transaction, final int minSupport,
	        final double minConfidence, @NotNull final PersistenceUtil persistenceUtil, final File cacheDir) {
		
		this.transaction = transaction;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory())) {
			final File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + transaction.getId()
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
				this.couplings = ChangeCouplingRuleFactory.getMethodChangeCouplings(transaction, minSupport,
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
	public final String getHandle() {
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
	 * @see org.mozkito.clustering.MultilevelClusteringScoreVisitor #getScore(java.lang.Object,
	 * java.lang.Object)
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
		
		Condition.notNull(element1, "Local variable '%s' in '%s:%s'.", "element1", getHandle(), "getScore"); //$NON-NLS-1$ //$NON-NLS-2$
		Condition.notNull(element2, "Local variable '%s' in '%s:%s'.", "element2", getHandle(), "getScore"); //$NON-NLS-1$ //$NON-NLS-2$
		
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
			currentCouplings = ChangeCouplingRuleFactory.getMethodChangeCouplings(this.transaction, this.minSupport,
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

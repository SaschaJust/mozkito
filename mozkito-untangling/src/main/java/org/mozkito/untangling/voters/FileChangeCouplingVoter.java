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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.changecouplings.ChangeCouplingRuleFactory;
import org.mozkito.changecouplings.model.FileChangeCoupling;
import org.mozkito.changecouplings.model.SerialFileChangeCoupling;
import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class ChangeCouplingVoter.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class FileChangeCouplingVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	/** The couplings. */
	private LinkedList<FileChangeCoupling> couplings = null;
	
	/** The transaction. */
	private final RCSTransaction           rCSTransaction;
	
	/** The min support. */
	private final int                      minSupport;
	
	/** The min confidence. */
	private final double                   minConfidence;
	
	/** The persistence util. */
	private final PersistenceUtil          persistenceUtil;
	
	/**
	 * Instantiates a new change coupling voter.
	 * 
	 * @param rCSTransaction
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
	public FileChangeCouplingVoter(@NotNull final RCSTransaction rCSTransaction, final int minSupport,
	        final double minConfidence, @NotNull final PersistenceUtil persistenceUtil, final java.io.File cacheDir) {
		
		this.rCSTransaction = rCSTransaction;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory())) {
			final java.io.File serialFile = new java.io.File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator
			        + rCSTransaction.getId() + "_file.cc");
			if (serialFile.exists()) {
				// load serial file
				try {
					final ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					final LinkedList<SerialFileChangeCoupling> serialCouplings = (LinkedList<SerialFileChangeCoupling>) in.readObject();
					in.close();
					this.couplings = new LinkedList<FileChangeCoupling>();
					for (final SerialFileChangeCoupling serialCoupling : serialCouplings) {
						this.couplings.add(serialCoupling.unserialize(persistenceUtil));
					}
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
				final LinkedList<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(rCSTransaction,
				                                                                                                            3,
				                                                                                                            0.1,
				                                                                                                            persistenceUtil);
				this.couplings = new LinkedList<FileChangeCoupling>();
				this.couplings.addAll(fileChangeCouplings);
				final LinkedList<SerialFileChangeCoupling> serialCouplings = new LinkedList<>();
				for (final FileChangeCoupling c : fileChangeCouplings) {
					serialCouplings.add(new SerialFileChangeCoupling(c));
				}
				try {
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
		
		final String path1 = t1.getChangedElementLocation().getFilePath();
		final String path2 = t2.getChangedElementLocation().getFilePath();
		
		Condition.check(path1 != null, "The changed elements must not be null!");
		Condition.check(path2 != null, "The changed elements must not be null!");
		
		if (this.couplings == null) {
			this.couplings = ChangeCouplingRuleFactory.getFileChangeCouplings(this.rCSTransaction, 3, 0.1,
			                                                                  this.persistenceUtil);
		}
		
		if (!this.couplings.isEmpty()) {
			
			final List<FileChangeCoupling> currentCouplings = new LinkedList<>();
			
			for (final FileChangeCoupling c : this.couplings) {
				boolean found = false;
				for (final RCSFile rCSFile : c.getPremise()) {
					final String fPath = rCSFile.getPath(this.rCSTransaction);
					if (fPath.equals(path1) || fPath.equals(path2)) {
						found = true;
						break;
					}
				}
				
				final String iPath = c.getImplication().getPath(this.rCSTransaction);
				if (found && (iPath.equals(path1) || iPath.equals(path2))) {
					currentCouplings.add(c);
				}
			}
			
			Collections.sort(currentCouplings, new Comparator<FileChangeCoupling>() {
				
				@Override
				public int compare(final FileChangeCoupling c1,
				                   final FileChangeCoupling c2) {
					return c1.getConfidence().compareTo(c2.getConfidence());
				}
				
			});
			final FileChangeCoupling coupling = this.couplings.get(0);
			if ((coupling.getSupport() >= this.minSupport) && (coupling.getConfidence() >= this.minConfidence)) {
				score = this.couplings.get(0).getConfidence();
			}
		}
		Condition.check(score <= 1d, "The returned distance must be a value between 0 and 1, but was: " + score);
		Condition.check(score >= 0d, "The returned distance must be a value between 0 and 1, but was: " + score);
		return score;
		
	}
}

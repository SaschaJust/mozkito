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
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.changecouplings.ChangeCouplingRuleFactory;
import de.unisaarland.cs.st.moskito.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.moskito.changecouplings.model.SerialFileChangeCoupling;
import de.unisaarland.cs.st.moskito.clustering.MultilevelClusteringScoreVisitor;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class ChangeCouplingVoter.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class FileChangeCouplingVoter implements MultilevelClusteringScoreVisitor<JavaChangeOperation> {
	
	private LinkedList<SerialFileChangeCoupling> couplings;
	private final RCSTransaction                 transaction;
	private final int                            minSupport;
	private final double                         minConfidence;
	private final PersistenceUtil                persistenceUtil;
	
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
	 */
	
	@SuppressWarnings ("unchecked")
	public FileChangeCouplingVoter(@NotNull final RCSTransaction transaction, final int minSupport,
	        final double minConfidence, @NotNull final PersistenceUtil persistenceUtil, final File cacheDir) {
		
		this.transaction = transaction;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
		if ((cacheDir != null) && (cacheDir.exists()) && (cacheDir.isDirectory())) {
			File serialFile = new File(cacheDir.getAbsolutePath() + FileUtils.fileSeparator + transaction.getId()
			        + "_file.cc");
			if (serialFile.exists()) {
				// load serial file
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(serialFile));
					couplings = (LinkedList<SerialFileChangeCoupling>) in.readObject();
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
				LinkedList<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(transaction,
				                                                                                                      3,
				                                                                                                      0.1,
				                                                                                                      persistenceUtil);
				this.couplings = new LinkedList<SerialFileChangeCoupling>();
				for (FileChangeCoupling c : fileChangeCouplings) {
					couplings.add(c.serialize(transaction));
				}
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
		
		String path1 = t1.getChangedElementLocation().getFilePath();
		String path2 = t2.getChangedElementLocation().getFilePath();
		
		Condition.check(path1 != null, "The changed elements must not be null!");
		Condition.check(path2 != null, "The changed elements must not be null!");
		
		if (couplings == null) {
			LinkedList<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(transaction,
			                                                                                                      3,
			                                                                                                      0.1,
			                                                                                                      persistenceUtil);
			this.couplings = new LinkedList<SerialFileChangeCoupling>();
			for (FileChangeCoupling c : fileChangeCouplings) {
				couplings.add(c.serialize(transaction));
			}
		}
		
		if (!couplings.isEmpty()) {
			
			List<SerialFileChangeCoupling> currentCouplings = new LinkedList<SerialFileChangeCoupling>();
			
			for (SerialFileChangeCoupling c : couplings) {
				boolean found = false;
				for (String fPath : c.getPremise()) {
					if (fPath.equals(path1) || fPath.equals(path2)) {
						found = true;
						break;
					}
				}
				
				String iPath = c.getImplication();
				if (found && (iPath.equals(path1) || iPath.equals(path2))) {
					currentCouplings.add(c);
				}
			}
			
			Collections.sort(currentCouplings, new Comparator<SerialFileChangeCoupling>() {
				
				@Override
				public int compare(final SerialFileChangeCoupling c1,
				                   final SerialFileChangeCoupling c2) {
					return c1.getConfidence().compareTo(c2.getConfidence());
				}
				
			});
			SerialFileChangeCoupling coupling = couplings.get(0);
			if ((coupling.getSupport() >= this.minSupport) && (coupling.getConfidence() >= this.minConfidence)) {
				score = couplings.get(0).getConfidence();
			}
		}
		Condition.check(score <= 1d, "The returned distance must be a value between 0 and 1, but was: " + score);
		Condition.check(score >= 0d, "The returned distance must be a value between 0 and 1, but was: " + score);
		return score;
		
	}
}

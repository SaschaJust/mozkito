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
package org.mozkito.untangling.settings;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.untangling.Untangling.ScoreCombinationMode;
import org.mozkito.untangling.Untangling.UntanglingCollapse;
import org.mozkito.untangling.blob.ChangeSet;
import org.mozkito.untangling.blob.combine.CombineOperator;
import org.mozkito.untangling.voters.MultilevelClusteringScoreVisitorFactory;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class UntanglingControl.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UntanglingControl {
	
	private final boolean                                                                                                       measurePrecision;
	
	/** The change coupling combine operator. */
	private CombineOperator<ChangeSet>                                                                                          combineOperator;
	
	/** The confidence voters. */
	private final Set<MultilevelClusteringScoreVisitorFactory<? extends MultilevelClusteringScoreVisitor<JavaChangeOperation>>> confidenceVoters       = new HashSet<>();
	
	/** The seed. */
	private Long                                                                                                                seed;
	
	/** The atomic change sets. */
	private List<String>                                                                                                        atomicTransactionIds;
	
	/** The persistence util. */
	private PersistenceUtil                                                                                                     persistenceUtil;
	
	/** The blob window size. */
	private Long                                                                                                                blobWindowSize;
	
	/** The min blob size. */
	private Long                                                                                                                minBlobSize;
	
	/** The max blob size. */
	private Long                                                                                                                maxBlobSize;
	
	/** The output file. */
	private File                                                                                                                outputFile;
	
	/** The n. */
	private Long                                                                                                                n;
	
	/** The collapse mode. */
	private UntanglingCollapse                                                                                                  collapseMode;
	
	/** The score mode. */
	private ScoreCombinationMode                                                                                                scoreMode;
	
	/** The dry run. */
	private Boolean                                                                                                             dryRun;
	
	private File                                                                                                                artificialBlobCacheDir = null;
	
	private Collection<UntangleInstruction>                                                                                     changeSetsToUntangle;
	
	private File                                                                                                                modelCacheDir;
	
	public UntanglingControl(final boolean measurePrecision) {
		this.measurePrecision = measurePrecision;
	}
	
	/**
	 * Adds the confidence voter.
	 * 
	 * @param voter
	 *            the voter
	 * @return true, if successful
	 */
	@NoneNull
	protected boolean addConfidenceVoter(final MultilevelClusteringScoreVisitorFactory<? extends MultilevelClusteringScoreVisitor<JavaChangeOperation>> voter) {
		
		return this.confidenceVoters.add(voter);
	}
	
	/**
	 * @return
	 */
	public File getArtificialBlobCacheDir() {
		return this.artificialBlobCacheDir;
	}
	
	/**
	 * Gets the atomic change sets.
	 * 
	 * @return the atomic change sets
	 */
	public List<String> getAtomicTransactionIds() {
		return this.atomicTransactionIds;
	}
	
	/**
	 * Gets the blob window size.
	 * 
	 * @return the blob window size
	 */
	public int getBlobWindowSize() {
		return this.blobWindowSize.intValue();
	}
	
	public Collection<UntangleInstruction> getChangeSetsToUntangle() {
		return this.changeSetsToUntangle;
	}
	
	/**
	 * Gets the collapse mode.
	 * 
	 * @return the collapse mode
	 */
	public UntanglingCollapse getCollapseMode() {
		return this.collapseMode;
	}
	
	/**
	 * Gets the change coupling combine operator.
	 * 
	 * @return the change coupling combine operator
	 */
	public CombineOperator<ChangeSet> getCombineOperator() {
		// PRECONDITIONS
		
		try {
			return this.combineOperator;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.combineOperator, "Field '%s' in '%s'.", "changeCouplingCombineOperator",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the confidence voters.
	 * 
	 * @return the confidence voters
	 */
	public Set<MultilevelClusteringScoreVisitorFactory<? extends MultilevelClusteringScoreVisitor<JavaChangeOperation>>> getConfidenceVoters() {
		return this.confidenceVoters;
	}
	
	/**
	 * Gets the max blob size.
	 * 
	 * @return the max blob size
	 */
	public int getMaxBlobSize() {
		return this.maxBlobSize.intValue();
	}
	
	/**
	 * Gets the min blob size.
	 * 
	 * @return the min blob size
	 */
	public int getMinBlobSize() {
		return this.minBlobSize.intValue();
	}
	
	public File getModelCacheDir() {
		return this.modelCacheDir;
	}
	
	/**
	 * Gets the n.
	 * 
	 * @return the n
	 */
	public int getN() {
		return this.n.intValue();
	}
	
	/**
	 * Gets the output file.
	 * 
	 * @return the output file
	 */
	public File getOutputFile() {
		return this.outputFile;
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistence util
	 */
	public PersistenceUtil getPersistenceUtil() {
		return this.persistenceUtil;
	}
	
	/**
	 * Gets the score mode.
	 * 
	 * @return the score mode
	 */
	public ScoreCombinationMode getScoreMode() {
		return this.scoreMode;
	}
	
	/**
	 * Gets the seed.
	 * 
	 * @return the seed
	 */
	public Long getSeed() {
		return this.seed;
	}
	
	/**
	 * Checks if is dry run.
	 * 
	 * @return true, if is dry run
	 */
	public boolean isDryRun() {
		return this.dryRun;
	}
	
	public boolean measurePrecision() {
		return this.measurePrecision;
	}
	
	/**
	 * @param artificialBlobCache
	 */
	public void setArtificialBlobCacheDir(final File artificialBlobCache) {
		// PRECONDITIONS
		
		try {
			this.artificialBlobCacheDir = artificialBlobCache;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the atomic transaction ids.
	 * 
	 * @param atomicTransactionIds
	 *            the new atomic transaction ids
	 */
	protected void setAtomicTransactionIds(final List<String> atomicTransactionIds) {
		// PRECONDITIONS
		
		try {
			this.atomicTransactionIds = atomicTransactionIds;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the blob window size.
	 * 
	 * @param blobWindowSize
	 *            the new blob window size
	 */
	protected void setBlobWindowSize(final Long blobWindowSize) {
		// PRECONDITIONS
		
		try {
			this.blobWindowSize = blobWindowSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param changeSetsToUntangle
	 */
	public void setChangeSetsToUntangle(final Collection<UntangleInstruction> changeSetsToUntangle) {
		// PRECONDITIONS
		
		try {
			this.changeSetsToUntangle = changeSetsToUntangle;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the collapse mode.
	 * 
	 * @param collapseMode
	 *            the new collapse mode
	 */
	protected void setCollapseMode(final UntanglingCollapse collapseMode) {
		// PRECONDITIONS
		
		try {
			this.collapseMode = collapseMode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the change coupling combine operator.
	 * 
	 * @param combineOperator
	 *            the new combine operator
	 */
	protected void setCombineOperator(final CombineOperator<ChangeSet> combineOperator) {
		// PRECONDITIONS
		Condition.notNull(combineOperator, "Argument '%s' in '%s'.", "changeCouplingCombineOperator",
		                  getClass().getSimpleName());
		
		try {
			this.combineOperator = combineOperator;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.combineOperator, combineOperator,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter.");
		}
	}
	
	/**
	 * Sets the dry run.
	 * 
	 * @param dryRun
	 *            the new dry run
	 */
	protected void setDryRun(final Boolean dryRun) {
		// PRECONDITIONS
		
		try {
			this.dryRun = dryRun;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the max blob size.
	 * 
	 * @param maxBlobSize
	 *            the new max blob size
	 */
	protected void setMaxBlobSize(final Long maxBlobSize) {
		// PRECONDITIONS
		
		try {
			this.maxBlobSize = maxBlobSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the min blob size.
	 * 
	 * @param minBlobSize
	 *            the new min blob size
	 */
	protected void setMinBlobSize(final Long minBlobSize) {
		// PRECONDITIONS
		
		try {
			this.minBlobSize = minBlobSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param value
	 */
	public void setSerialModel(final File value) {
		// PRECONDITIONS
		
		try {
			this.modelCacheDir = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the n.
	 * 
	 * @param n
	 *            the new n
	 */
	protected void setN(final Long n) {
		// PRECONDITIONS
		
		try {
			this.n = n;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the output file.
	 * 
	 * @param outFile
	 *            the new output file
	 */
	protected void setOutputFile(final File outFile) {
		// PRECONDITIONS
		
		try {
			this.outputFile = outFile;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the persistence util.
	 * 
	 * @param persistenceUtil
	 *            the new persistence util
	 */
	protected void setPersistenceUtil(final PersistenceUtil persistenceUtil) {
		// PRECONDITIONS
		
		try {
			this.persistenceUtil = persistenceUtil;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the score mode.
	 * 
	 * @param scoreMode
	 *            the new score mode
	 */
	protected void setScoreMode(final ScoreCombinationMode scoreMode) {
		// PRECONDITIONS
		
		try {
			this.scoreMode = scoreMode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the seed.
	 * 
	 * @param seed
	 *            the new seed
	 */
	protected void setSeed(final Long seed) {
		// PRECONDITIONS
		
		try {
			this.seed = seed;
		} finally {
			// POSTCONDITIONS
		}
	}
}

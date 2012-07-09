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
package de.unisaarland.cs.st.moskito.untangling.settings;

import java.io.File;
import java.util.List;

import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.untangling.Untangling.ScoreCombinationMode;
import de.unisaarland.cs.st.moskito.untangling.Untangling.UntanglingCollapse;
import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator.ArtificialBlobGeneratorStrategy;

/**
 * The Class UntanglingControl.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UntanglingControl {
	
	/** The call graph enabled. */
	private Boolean                         callGraphEnabled;
	
	/** The change couplings enabled. */
	private Boolean                         changeCouplingsEnabled;
	
	/** The data dependencies enabled. */
	private Boolean                         dataDependenciesEnabled;
	
	/** The test impact enabled. */
	private Boolean                         testImpactEnabled;
	
	/** The atomic changes. */
	private List<String>                    atomicChanges;
	
	/** The call graph cahce dir. */
	private File                            callGraphCahceDir;
	
	/** The call graph eclipse dir. */
	private File                            callGraphEclipseDir;
	
	/** The change coupling min confidence. */
	private Double                          changeCouplingMinConfidence;
	
	/** The change coupling min support. */
	private Long                            changeCouplingMinSupport;
	
	/** The change couplings cache dir. */
	private File                            changeCouplingsCacheDir;
	
	/** The collapse mode. */
	private UntanglingCollapse              collapseMode;
	
	/** The data dependency cache dir. */
	private File                            dataDependencyCacheDir;
	
	/** The data dependency eclipse dir. */
	private File                            dataDependencyEclipseDir;
	
	/** The dry run. */
	private Boolean                         dryRun;
	
	/** The max blob size. */
	private Long                            maxBlobSize;
	
	/** The min blob size. */
	private Long                            minBlobSize;
	
	/** The n. */
	private Long                            n;
	
	/** The output file. */
	private File                            outputFile;
	
	/** The package distance. */
	private Long                            packageDistance;
	
	/** The repository. */
	private Repository                      repository;
	
	/** The score mode. */
	private ScoreCombinationMode            scoreMode;
	
	/** The seed. */
	private Long                            seed;
	
	/** The test impact file. */
	private File                            testImpactFile;
	
	/** The blob window size. */
	private Long                            blobWindowSize;
	
	private ArtificialBlobGeneratorStrategy generatorStrategy;
	
	/**
	 * Enable call graph.
	 * 
	 * @param value
	 *            the value
	 */
	public void enableCallGraph(final Boolean value) {
		// PRECONDITIONS
		
		try {
			this.callGraphEnabled = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Enable change couplings.
	 * 
	 * @param value
	 *            the value
	 */
	public void enableChangeCouplings(final Boolean value) {
		// PRECONDITIONS
		
		try {
			this.changeCouplingsEnabled = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Enable data dependencies.
	 * 
	 * @param value
	 *            the value
	 */
	public void enableDataDependencies(final Boolean value) {
		// PRECONDITIONS
		
		try {
			this.dataDependenciesEnabled = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Enable test impact.
	 * 
	 * @param value
	 *            the value
	 */
	public void enableTestImpact(final Boolean value) {
		// PRECONDITIONS
		
		try {
			this.testImpactEnabled = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the atomic changes.
	 * 
	 * @return the atomic changes
	 */
	public List<String> getAtomicChanges() {
		// PRECONDITIONS
		
		try {
			return this.atomicChanges;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the blob window size.
	 * 
	 * @return the blob window size
	 */
	public Long getBlobWindowSize() {
		// PRECONDITIONS
		
		try {
			return this.blobWindowSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the call graph cache dir.
	 * 
	 * @return the call graph cache dir
	 */
	public File getCallGraphCacheDir() {
		// PRECONDITIONS
		
		try {
			return this.callGraphCahceDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the call graph eclipse dir.
	 * 
	 * @return the call graph eclipse dir
	 */
	public File getCallGraphEclipseDir() {
		// PRECONDITIONS
		
		try {
			return this.callGraphEclipseDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the change coupling min confidence.
	 * 
	 * @return the change coupling min confidence
	 */
	public Double getChangeCouplingMinConfidence() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingMinConfidence;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the change coupling min support.
	 * 
	 * @return the change coupling min support
	 */
	public Long getChangeCouplingMinSupport() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingMinSupport;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the change couplings cache dir.
	 * 
	 * @return the change couplings cache dir
	 */
	public File getChangeCouplingsCacheDir() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingsCacheDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the collapse mode.
	 * 
	 * @return the collapse mode
	 */
	public UntanglingCollapse getCollapseMode() {
		// PRECONDITIONS
		
		try {
			return this.collapseMode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the data dependency cache dir.
	 * 
	 * @return the data dependency cache dir
	 */
	public File getDataDependencyCacheDir() {
		// PRECONDITIONS
		
		try {
			return this.dataDependencyCacheDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the data dependency eclipse dir.
	 * 
	 * @return the data dependency eclipse dir
	 */
	public File getDataDependencyEclipseDir() {
		// PRECONDITIONS
		
		try {
			return this.dataDependencyEclipseDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public ArtificialBlobGeneratorStrategy getGeneratorStrategy() {
		return this.generatorStrategy;
	}
	
	/**
	 * Gets the max blob size.
	 * 
	 * @return the max blob size
	 */
	public Long getMaxBlobSize() {
		// PRECONDITIONS
		
		try {
			return this.maxBlobSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the min blob size.
	 * 
	 * @return the min blob size
	 */
	public Long getMinBlobSize() {
		// PRECONDITIONS
		
		try {
			return this.minBlobSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the n.
	 * 
	 * @return the n
	 */
	public Long getN() {
		// PRECONDITIONS
		
		try {
			return this.n;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the output file.
	 * 
	 * @return the output file
	 */
	public File getOutputFile() {
		// PRECONDITIONS
		
		try {
			return this.outputFile;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the package distance.
	 * 
	 * @return the package distance
	 */
	public Long getPackageDistance() {
		// PRECONDITIONS
		
		try {
			return this.packageDistance;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	public Repository getRepository() {
		// PRECONDITIONS
		
		try {
			return this.repository;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the score mode.
	 * 
	 * @return the score mode
	 */
	public ScoreCombinationMode getScoreMode() {
		// PRECONDITIONS
		
		try {
			return this.scoreMode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the seed.
	 * 
	 * @return the seed
	 */
	public Long getSeed() {
		// PRECONDITIONS
		
		try {
			return this.seed;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the test impact file.
	 * 
	 * @return the test impact file
	 */
	public File getTestImpactFile() {
		// PRECONDITIONS
		
		try {
			return this.testImpactFile;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Checks if is call graph enabled.
	 * 
	 * @return the boolean
	 */
	public Boolean isCallGraphEnabled() {
		// PRECONDITIONS
		
		try {
			return this.callGraphEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Checks if is change couplings enabled.
	 * 
	 * @return the boolean
	 */
	public Boolean isChangeCouplingsEnabled() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingsEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Checks if is data dependencies enabled.
	 * 
	 * @return the boolean
	 */
	public Boolean isDataDependenciesEnabled() {
		// PRECONDITIONS
		
		try {
			return this.dataDependenciesEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Checks if is dry run.
	 * 
	 * @return the boolean
	 */
	public Boolean isDryRun() {
		// PRECONDITIONS
		
		try {
			return this.dryRun;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Checks if is test impact enabled.
	 * 
	 * @return the boolean
	 */
	public Boolean isTestImpactEnabled() {
		// PRECONDITIONS
		
		try {
			return this.testImpactEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the atomic changes.
	 * 
	 * @param value
	 *            the new atomic changes
	 */
	public void setAtomicChanges(final List<String> value) {
		// PRECONDITIONS
		
		try {
			this.atomicChanges = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the blob window size.
	 * 
	 * @param value
	 *            the new blob window size
	 */
	public void setBlobWindowSize(final Long value) {
		// PRECONDITIONS
		
		try {
			this.blobWindowSize = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the call graph cache dir.
	 * 
	 * @param value
	 *            the new call graph cache dir
	 */
	public void setCallGraphCacheDir(final File value) {
		// PRECONDITIONS
		
		try {
			this.callGraphCahceDir = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the call graph eclipse dir.
	 * 
	 * @param value
	 *            the new call graph eclipse dir
	 */
	public void setCallGraphEclipseDir(final File value) {
		// PRECONDITIONS
		
		try {
			this.callGraphEclipseDir = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the change coupling min confidence.
	 * 
	 * @param value
	 *            the new change coupling min confidence
	 */
	public void setChangeCouplingMinConfidence(final Double value) {
		// PRECONDITIONS
		
		try {
			this.changeCouplingMinConfidence = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the change coupling min support.
	 * 
	 * @param value
	 *            the new change coupling min support
	 */
	public void setChangeCouplingMinSupport(final Long value) {
		// PRECONDITIONS
		
		try {
			this.changeCouplingMinSupport = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the change couplings cache dir.
	 * 
	 * @param value
	 *            the new change couplings cache dir
	 */
	public void setChangeCouplingsCacheDir(final File value) {
		// PRECONDITIONS
		
		try {
			this.changeCouplingsCacheDir = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the collapse mode.
	 * 
	 * @param value
	 *            the new collapse mode
	 */
	public void setCollapseMode(final UntanglingCollapse value) {
		// PRECONDITIONS
		
		try {
			this.collapseMode = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the data dependency cache dir.
	 * 
	 * @param value
	 *            the new data dependency cache dir
	 */
	public void setDataDependencyCacheDir(final File value) {
		// PRECONDITIONS
		
		try {
			this.dataDependencyCacheDir = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the data dependency eclipse dir.
	 * 
	 * @param value
	 *            the new data dependency eclipse dir
	 */
	public void setDataDependencyEclipseDir(final File value) {
		// PRECONDITIONS
		
		try {
			this.dataDependencyEclipseDir = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the dry run.
	 * 
	 * @param value
	 *            the new dry run
	 */
	public void setDryRun(final Boolean value) {
		// PRECONDITIONS
		
		try {
			this.dryRun = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param value
	 */
	public void setGeneratorStrategy(final ArtificialBlobGeneratorStrategy value) {
		// PRECONDITIONS
		
		try {
			this.generatorStrategy = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the max blob size.
	 * 
	 * @param value
	 *            the new max blob size
	 */
	public void setMaxBlobSize(final Long value) {
		// PRECONDITIONS
		
		try {
			this.maxBlobSize = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the min blob size.
	 * 
	 * @param value
	 *            the new min blob size
	 */
	public void setMinBlobSize(final Long value) {
		// PRECONDITIONS
		
		try {
			this.minBlobSize = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the n.
	 * 
	 * @param value
	 *            the new n
	 */
	public void setN(final Long value) {
		// PRECONDITIONS
		
		try {
			this.n = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the output file.
	 * 
	 * @param value
	 *            the new output file
	 */
	public void setOutputFile(final File value) {
		// PRECONDITIONS
		
		try {
			this.outputFile = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the package distance.
	 * 
	 * @param value
	 *            the new package distance
	 */
	public void setPackageDistance(final Long value) {
		// PRECONDITIONS
		
		try {
			this.packageDistance = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the repository.
	 * 
	 * @param value
	 *            the new repository
	 */
	public void setRepository(final Repository value) {
		// PRECONDITIONS
		
		try {
			this.repository = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the score mode.
	 * 
	 * @param value
	 *            the new score mode
	 */
	public void setScoreMode(final ScoreCombinationMode value) {
		// PRECONDITIONS
		
		try {
			this.scoreMode = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the seed.
	 * 
	 * @param value
	 *            the new seed
	 */
	public void setSeed(final Long value) {
		// PRECONDITIONS
		
		try {
			this.seed = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Sets the test impact file.
	 * 
	 * @param value
	 *            the new test impact file
	 */
	public void setTestImpactFile(final File value) {
		// PRECONDITIONS
		
		try {
			this.testImpactFile = value;
		} finally {
			// POSTCONDITIONS
		}
	}
}

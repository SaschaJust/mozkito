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
package de.unisaarland.cs.st.moskito.untangling.settings;

import java.io.File;
import java.util.List;

import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.untangling.Untangling.ScoreCombinationMode;
import de.unisaarland.cs.st.moskito.untangling.Untangling.UntanglingCollapse;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class UntanglingControl {
	
	private Boolean              callGraphEnabled;
	
	private Boolean              changeCouplingsEnabled;
	
	private Boolean              dataDependenciesEnabled;
	
	private Boolean              testImpactEnabled;
	
	private List<String>         atomicChanges;
	
	private File                 callGraphCahceDir;
	
	private File                 callGraphEclipseDir;
	
	private Double               changeCouplingMinConfidence;
	
	private Long                 changeCouplingMinSupport;
	
	private File                 changeCouplingsCacheDir;
	
	private UntanglingCollapse   collapseMode;
	
	private File                 dataDependencyCacheDir;
	
	private File                 dataDependencyEclipseDir;
	
	private Boolean              dryRun;
	
	private Long                 maxBlobSize;
	
	private Long                 minBlobSize;
	
	private Long                 n;
	
	private File                 outputFile;
	
	private Long                 packageDistance;
	
	private Repository           repository;
	
	private ScoreCombinationMode scoreMode;
	
	private Long                 seed;
	
	private File                 testImpactFile;
	
	private Long                 blobWindowSize;
	
	/**
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
	 */
	public void enableTestImpact(final Boolean value) {
		// PRECONDITIONS
		
		try {
			this.testImpactEnabled = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public List<String> getAtomicChanges() {
		// PRECONDITIONS
		
		try {
			return this.atomicChanges;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getBlobWindowSize() {
		// PRECONDITIONS
		
		try {
			return this.blobWindowSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getCallGraphCacheDir() {
		// PRECONDITIONS
		
		try {
			return this.callGraphCahceDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getCallGraphEclipseDir() {
		// PRECONDITIONS
		
		try {
			return this.callGraphEclipseDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Double getChangeCouplingMinConfidence() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingMinConfidence;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getChangeCouplingMinSupport() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingMinSupport;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getChangeCouplingsCacheDir() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingsCacheDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public UntanglingCollapse getCollapseMode() {
		// PRECONDITIONS
		
		try {
			return this.collapseMode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getDataDependencyCacheDir() {
		// PRECONDITIONS
		
		try {
			return this.dataDependencyCacheDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getDataDependencyEclipseDir() {
		// PRECONDITIONS
		
		try {
			return this.dataDependencyEclipseDir;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Boolean isDryRun() {
		// PRECONDITIONS
		
		try {
			return this.dryRun;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getMaxBlobSize() {
		// PRECONDITIONS
		
		try {
			return this.maxBlobSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getMinBlobSize() {
		// PRECONDITIONS
		
		try {
			return this.minBlobSize;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getN() {
		// PRECONDITIONS
		
		try {
			return this.n;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getOutputFile() {
		// PRECONDITIONS
		
		try {
			return this.outputFile;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getPackageDistance() {
		// PRECONDITIONS
		
		try {
			return this.packageDistance;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Repository getRepository() {
		// PRECONDITIONS
		
		try {
			return this.repository;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public ScoreCombinationMode getScoreMode() {
		// PRECONDITIONS
		
		try {
			return this.scoreMode;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Long getSeed() {
		// PRECONDITIONS
		
		try {
			return this.seed;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public File getTestImpactFile() {
		// PRECONDITIONS
		
		try {
			return this.testImpactFile;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Boolean isCallGraphEnabled() {
		// PRECONDITIONS
		
		try {
			return this.callGraphEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Boolean isChangeCouplingsEnabled() {
		// PRECONDITIONS
		
		try {
			return this.changeCouplingsEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Boolean isDataDependenciesEnabled() {
		// PRECONDITIONS
		
		try {
			return this.dataDependenciesEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public Boolean isTestImpactEnabled() {
		// PRECONDITIONS
		
		try {
			return this.testImpactEnabled;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	public void setMaxBlobSize(final Long value) {
		// PRECONDITIONS
		
		try {
			this.maxBlobSize = value;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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
	 * @param value
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

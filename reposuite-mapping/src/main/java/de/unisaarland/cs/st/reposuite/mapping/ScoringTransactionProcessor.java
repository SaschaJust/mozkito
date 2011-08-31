/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableReport;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableTransaction;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringTransactionProcessor extends AndamaTransformer<RCSTransaction, MapScore> {
	
	private final PersistenceUtil persistenceUtil;
	private final MapScore        zeroScore = new MapScore(null, null);
	private final MappingFinder   mappingFinder;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil
	 */
	public ScoringTransactionProcessor(final AndamaGroup threadGroup, final MappingSettings settings, final MappingFinder finder,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		this.persistenceUtil = persistenceUtil;
		this.mappingFinder = finder;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.InputOutputConnectable#process(java.lang
	 * .Object)
	 */
	@Override
	public MapScore process(final RCSTransaction transaction) throws UnrecoverableError, Shutdown {
		MappableTransaction mapTransaction = new MappableTransaction(transaction);
		Set<MappableReport> candidates = this.mappingFinder.getCandidates(mapTransaction, MappableReport.class);
		
		if (!candidates.isEmpty()) {
			if (Logger.logDebug()) {
				Logger.debug("Fetching candidates (" + candidates.size() + ") "
				        + JavaUtils.collectionToString(candidates));
			}
			
			Criteria<Report> criteria = this.persistenceUtil.createCriteria(Report.class);
			
			// FIXME this should not be candidates but a set of actual
			// report IDs
			criteria.in("id", candidates);
			
			for (Report report : this.persistenceUtil.load(criteria)) {
				if (Logger.logDebug()) {
					Logger.debug("Processing mapping for " + transaction.getId() + " to " + report.getId() + ".");
				}
				
				MapScore score = this.mappingFinder.score(mapTransaction, new MappableReport(report));
				if (score.compareTo(this.zeroScore) > 0) {
					if (Logger.logInfo()) {
						Logger.info("Providing for store operation: " + transaction.getId() + " -> " + report.getId()
						        + " (score: " + score + ").");
					}
					return (score);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Discarding " + transaction.getId() + " -> " + report.getId()
						        + " due to non-positive score (" + score + ").");
					}
					return skip(score);
				}
			}
		}
		
		return null;
	}
}

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
package org.mozkito.mappings.chains.transformers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreProcessHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.issues.model.Report;
import org.mozkito.mappings.elements.CandidateFactory;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class TransactionFinder.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportFinder extends Transformer<ChangeSet, Candidate<Report, ChangeSet>> {
	
	/** The candidate factory. */
	private final CandidateFactory<Report, ChangeSet> candidateFactory = CandidateFactory.getInstance(Report.class,
	                                                                                                  ChangeSet.class);
	
	/**
	 * Instantiates a new transaction finder.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 * @param util
	 *            the util
	 */
	public ReportFinder(final Group threadGroup, final Settings settings, final Finder finder,
	        final PersistenceUtil util) {
		super(threadGroup, settings, true);
		
		final Set<Candidate<Report, ChangeSet>> candidates = new HashSet<>();
		
		new PreProcessHook<ChangeSet, Candidate<Report, ChangeSet>>(this) {
			
			@Override
			public void preProcess() {
				if (candidates.isEmpty()) {
					final ChangeSet changeSet = getInputData();
					final Map<Report, Set<Selector>> reportCandidates = finder.getCandidates(changeSet, Report.class,
					                                                                         util);
					
					if (Logger.logInfo()) {
						Logger.info(Messages.getString("ReportFinder.processing", changeSet.getClassName(), //$NON-NLS-1$
						                               changeSet.toString(), reportCandidates.size()));
					}
					
					for (final Report mappableReport : reportCandidates.keySet()) {
						if (ReportFinder.this.candidateFactory.contains(mappableReport, changeSet)) {
							if (Logger.logInfo()) {
								Logger.info(Messages.getString("ReportFinder.skipping", changeSet, mappableReport)); //$NON-NLS-1$
							}
							ReportFinder.this.candidateFactory.get(mappableReport, changeSet)
							                                  .addSelectors(reportCandidates.get(mappableReport));
						} else {
							candidates.add(ReportFinder.this.candidateFactory.add(mappableReport, changeSet,
							                                                      reportCandidates.get(mappableReport)));
						}
						
					}
				}
			}
		};
		
		new ProcessHook<ChangeSet, Candidate<Report, ChangeSet>>(this) {
			
			@Override
			public void process() {
				if (!candidates.isEmpty()) {
					final Candidate<Report, ChangeSet> candidate = candidates.iterator().next();
					candidates.remove(candidate);
					
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("ReportFinder.providing", candidate)); //$NON-NLS-1$
					}
					
					providePartialOutputData(candidate);
					
					if (candidates.isEmpty()) {
						setCompleted();
					}
				} else {
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("ReportFinder.noMatches", //$NON-NLS-1$
						                                getInputData()));
					}
					skipData();
				}
			}
		};
	}
}

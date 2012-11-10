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

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.elements.CandidateFactory;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.mappable.model.MappableTransaction;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class TransactionFinder.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TransactionFinder extends Transformer<Report, Candidate> {
	
	private final CandidateFactory<MappableReport, MappableTransaction> candidateFactory = CandidateFactory.getInstance(MappableReport.class,
	                                                                                                                    MappableTransaction.class);
	
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
	public TransactionFinder(final Group threadGroup, final Settings settings, final Finder finder,
	        final PersistenceUtil util) {
		super(threadGroup, settings, true);
		
		final Set<Candidate> candidates = new HashSet<>();
		
		new PreProcessHook<Report, Candidate>(this) {
			
			@Override
			public void preProcess() {
				if (candidates.isEmpty()) {
					final MappableReport mapReport = new MappableReport(getInputData());
					final Map<MappableTransaction, Set<Selector>> transactionCandidates = finder.getCandidates(mapReport,
					                                                                                           MappableTransaction.class,
					                                                                                           util);
					
					if (Logger.logInfo()) {
						Logger.info(Messages.getString("TransactionFinder.processing", mapReport.getHandle(), //$NON-NLS-1$
						                               mapReport.toString(), transactionCandidates.size()));
					}
					
					for (final MappableTransaction mapTransaction : transactionCandidates.keySet()) {
						if (TransactionFinder.this.candidateFactory.contains(mapReport, mapTransaction)) {
							if (Logger.logInfo()) {
								Logger.info(Messages.getString("TransactionFinder.skipping", mapReport, mapTransaction)); //$NON-NLS-1$
							}
							TransactionFinder.this.candidateFactory.get(mapReport, mapTransaction)
							                                       .addSelectors(transactionCandidates.get(mapTransaction));
						} else {
							candidates.add(TransactionFinder.this.candidateFactory.add(mapReport,
							                                                           mapTransaction,
							                                                           transactionCandidates.get(mapTransaction)));
						}
						
					}
				}
			}
		};
		
		new ProcessHook<Report, Candidate>(this) {
			
			@Override
			public void process() {
				if (!candidates.isEmpty()) {
					final Candidate candidate = candidates.iterator().next();
					candidates.remove(candidate);
					
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("TransactionFinder.providing", candidate)); //$NON-NLS-1$
					}
					
					providePartialOutputData(candidate);
					
					if (candidates.isEmpty()) {
						setCompleted();
					}
				} else {
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("TransactionFinder.noMatches", //$NON-NLS-1$
						                                getInputData()));
					}
					skipData();
				}
			}
		};
	}
	
}

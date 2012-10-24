/**
 * 
 */
package org.mozkito.mappings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.mappings.elements.CandidateFactory;
import org.mozkito.mappings.finder.MappingFinder;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.mappable.model.MappableTransaction;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.persistence.PersistenceUtil;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreProcessHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class TransactionFinder.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
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
	public TransactionFinder(final Group threadGroup, final Settings settings, final MappingFinder finder,
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
						Logger.info("Processing '%s'->%s with '%s' candidates.", mapReport.getHandle(),
						            mapReport.toString(), transactionCandidates.size());
					}
					
					for (final MappableTransaction mapTransaction : transactionCandidates.keySet()) {
						if (TransactionFinder.this.candidateFactory.isKnown(mapReport, mapTransaction)) {
							if (Logger.logInfo()) {
								Logger.info("Skipping candidate '%s'<->'%s'. Already processed.");
							}
						} else {
							candidates.add(TransactionFinder.this.candidateFactory.getCandidate(mapReport,
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
						Logger.debug("Providing candidate '%s'.", candidate);
					}
					providePartialOutputData(candidate);
					if (candidates.isEmpty()) {
						setCompleted();
					}
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Skipping candidate analysis of '%s' due to the lag of pre-selected candidates.",
						             getInputData());
					}
					skipData();
				}
			}
		};
	}
	
}

/**
 * 
 */
package org.mozkito.mappings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreProcessHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.elements.CandidateFactory;
import org.mozkito.mappings.finder.MappingFinder;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.mappable.model.MappableTransaction;
import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.selectors.Selector;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class ReportFinder.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReportFinder extends Transformer<RCSTransaction, Candidate> {
	
	/** The candidate factory. */
	private final CandidateFactory<MappableReport, MappableTransaction> candidateFactory = CandidateFactory.getInstance(MappableReport.class,
	                                                                                                                    MappableTransaction.class);
	
	/**
	 * Instantiates a new report finder.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public ReportFinder(final Group threadGroup, final Settings settings, final MappingFinder finder,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, true);
		
		final Set<Candidate> candidates = new HashSet<>();
		
		new PreProcessHook<RCSTransaction, Candidate>(this) {
			
			@Override
			public void preProcess() {
				if (candidates.isEmpty()) {
					final MappableTransaction mapTransaction = new MappableTransaction(getInputData());
					final Map<MappableReport, Set<Selector>> reportCandidates = finder.getCandidates(mapTransaction,
					                                                                                 MappableReport.class,
					                                                                                 persistenceUtil);
					
					if (Logger.logInfo()) {
						Logger.info("Processing '%s'->%s with '%s' candidates.", mapTransaction.getHandle(),
						            mapTransaction.toString(), reportCandidates.size());
					}
					
					for (final MappableReport mapReport : reportCandidates.keySet()) {
						if (ReportFinder.this.candidateFactory.isKnown(mapReport, mapTransaction)) {
							if (Logger.logInfo()) {
								Logger.info("Skipping candidate '%s'<->'%s'. Already processed.");
							}
						} else {
							candidates.add(ReportFinder.this.candidateFactory.getCandidate(mapReport,
							                                                               mapTransaction,
							                                                               reportCandidates.get(mapTransaction)));
						}
						
					}
				}
			}
		};
		
		new ProcessHook<RCSTransaction, Candidate>(this) {
			
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

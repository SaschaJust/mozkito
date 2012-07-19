/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreProcessHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.mapping.model.Candidate;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class ReportFinder.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ReportFinder extends Transformer<RCSTransaction, Candidate> {
	
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
					final RCSTransaction inputData = getInputData();
					final MappableTransaction mapTransaction = new MappableTransaction(inputData);
					// TODO store selectors and reuse them
					final Set<MappableReport> reportCandidates = finder.getCandidates(mapTransaction,
					                                                                  MappableReport.class,
					                                                                  persistenceUtil);
					
					if (Logger.logInfo()) {
						Logger.info("Processing '%s'->%s with '%s' candidates.", mapTransaction.getHandle(),
						            mapTransaction.toString(), reportCandidates.size());
					}
					
					for (final MappableReport mapReport : reportCandidates) {
						candidates.add(new Candidate(new Tuple<MappableEntity, MappableEntity>(mapTransaction,
						                                                                       mapReport)));
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

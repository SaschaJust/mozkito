/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.PreProcessHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.ioda.Tuple;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TransactionFinder extends AndamaTransformer<Report, Candidate> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param finder
	 * @param persistenceUtil
	 */
	public TransactionFinder(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final MappingFinder finder) {
		super(threadGroup, settings, true);
		
		final LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		
		new PreProcessHook<Report, Candidate>(this) {
			
			@Override
			public void preProcess() {
				if (candidates.isEmpty()) {
					MappableReport mapReport = new MappableReport(getInputData());
					Set<MappableTransaction> transactionCandidates = finder.getCandidates(mapReport,
					                                                                      MappableTransaction.class);
					for (MappableTransaction mapTransaction : transactionCandidates) {
						candidates.add(new Candidate(new Tuple<MappableEntity, MappableEntity>(mapReport,
						                                                                       mapTransaction)));
					}
				}
			}
		};
		
		new ProcessHook<Report, Candidate>(this) {
			
			@Override
			public void process() {
				if (!candidates.isEmpty()) {
					if (candidates.size() == 1) {
						provideOutputData(candidates.poll());
					} else {
						provideOutputData(candidates.poll(), false);
					}
				}
			}
		};
	}
	
}

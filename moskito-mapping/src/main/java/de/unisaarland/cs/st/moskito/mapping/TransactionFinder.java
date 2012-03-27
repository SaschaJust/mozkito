/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreProcessHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.ioda.Tuple;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class TransactionFinder.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TransactionFinder extends Transformer<Report, Candidate> {
	
	/**
	 * Instantiates a new transaction finder.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param finder the finder
	 * @param util the util
	 */
	public TransactionFinder(final Group threadGroup, final Settings settings, final MappingFinder finder,
	        final PersistenceUtil util) {
		super(threadGroup, settings, true);
		
		final LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		
		new PreProcessHook<Report, Candidate>(this) {
			
			@Override
			public void preProcess() {
				if (candidates.isEmpty()) {
					final MappableReport mapReport = new MappableReport(getInputData());
					final Set<MappableTransaction> transactionCandidates = finder.getCandidates(mapReport,
					                                                                            MappableTransaction.class,
					                                                                            util);
					for (final MappableTransaction mapTransaction : transactionCandidates) {
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

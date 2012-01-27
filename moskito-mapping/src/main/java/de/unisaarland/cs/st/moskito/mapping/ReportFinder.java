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
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportFinder extends AndamaTransformer<RCSTransaction, Candidate> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param finder
	 * @param persistenceUtil
	 */
	public ReportFinder(final AndamaGroup threadGroup, final AndamaSettings settings, final MappingFinder finder,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, true);
		
		final LinkedList<Candidate> candidates = new LinkedList<Candidate>();
		
		new PreProcessHook<RCSTransaction, Candidate>(this) {
			
			@Override
			public void preProcess() {
				if (candidates.isEmpty()) {
					final MappableTransaction mapTransaction = new MappableTransaction(getInputData());
					final Set<MappableReport> reportCandidates = finder.getCandidates(mapTransaction,
					                                                                  MappableReport.class,
					                                                                  persistenceUtil);
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

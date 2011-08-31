/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.ioda.Tuple;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.elements.Candidate;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableReport;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringTransactionFinder extends AndamaTransformer<Report, Candidate> {
	
	private final MappingFinder         finder;
	private final LinkedList<Candidate> candidates = new LinkedList<Candidate>();
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param finder
	 * @param persistenceUtil
	 */
	public ScoringTransactionFinder(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final MappingFinder finder) {
		super(threadGroup, settings, true);
		this.finder = finder;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.InputOutputConnectable#process(java.lang
	 * .Object)
	 */
	@Override
	public Candidate process(final Report data) throws UnrecoverableError, Shutdown {
		if (this.candidates.isEmpty()) {
			MappableReport mapReport = new MappableReport(data);
			Set<MappableTransaction> transactionCandidates = this.finder.getCandidates(mapReport,
			                                                                           MappableTransaction.class);
			for (MappableTransaction mapTransaction : transactionCandidates) {
				this.candidates.add(new Candidate(new Tuple<MappableEntity, MappableEntity>(mapReport, mapTransaction)));
			}
			
			return stage(this.candidates.poll());
		} else if (this.candidates.size() == 1) {
			return this.candidates.poll();
		} else {
			return stage(this.candidates.poll());
		}
	}
	
}

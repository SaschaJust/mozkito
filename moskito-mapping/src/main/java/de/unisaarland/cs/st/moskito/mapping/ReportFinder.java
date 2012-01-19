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
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportFinder extends AndamaTransformer<RCSTransaction, Mapping> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param finder
	 * @param persistenceUtil
	 */
	public ReportFinder(final AndamaGroup threadGroup, final AndamaSettings settings, final MappingFinder finder) {
		super(threadGroup, settings, true);
		
		final LinkedList<Mapping> mappings = new LinkedList<Mapping>();
		
		new PreProcessHook<RCSTransaction, Mapping>(this) {
			
			@Override
			public void preProcess() {
				if (mappings.isEmpty()) {
					final MappableTransaction mapTransaction = new MappableTransaction(getInputData());
					final Set<MappableReport> reportCandidates = finder.getCandidates(mapTransaction,
					                                                                  MappableReport.class);
					
					for (final MappableReport mapReport : reportCandidates) {
						mappings.add(new Mapping(mapTransaction, mapReport));
					}
				}
			}
		};
		
		new ProcessHook<RCSTransaction, Mapping>(this) {
			
			@Override
			public void process() {
				if (!mappings.isEmpty()) {
					if (mappings.size() == 1) {
						provideOutputData(mappings.poll());
					} else {
						provideOutputData(mappings.poll(), false);
					}
				}
			}
		};
	}
	
}

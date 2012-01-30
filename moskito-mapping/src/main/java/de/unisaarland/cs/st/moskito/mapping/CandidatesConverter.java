/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CandidatesConverter extends AndamaTransformer<Candidate, Mapping> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public CandidatesConverter(final AndamaGroup threadGroup, final AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		new ProcessHook<Candidate, Mapping>(this) {
			
			@Override
			public void process() {
				final Candidate data = getInputData();
				provideOutputData(new Mapping(data.getFrom(), data.getTo()), true);
			}
		};
	}
	
}

/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CandidatesConverter extends Transformer<Candidate, Mapping> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public CandidatesConverter(final Group threadGroup, final Settings settings) {
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

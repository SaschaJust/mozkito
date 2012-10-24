/**
 * 
 */
package de.unisaarland.cs.st.mozkito.mappings;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.mozkito.mappings.model.Candidate;
import de.unisaarland.cs.st.mozkito.mappings.model.Relation;

/**
 * The Class CandidatesConverter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CandidatesConverter extends Transformer<Candidate, Relation> {
	
	/**
	 * Instantiates a new candidates converter.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public CandidatesConverter(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
		
		new ProcessHook<Candidate, Relation>(this) {
			
			@Override
			public void process() {
				final Candidate data = getInputData();
				provideOutputData(new Relation(data));
			}
		};
	}
	
}

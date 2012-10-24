/**
 * 
 */
package de.unisaarland.cs.st.mozkito.mappings;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.mozkito.mappings.model.Composite;
import de.unisaarland.cs.st.mozkito.mappings.model.Relation;

/**
 * The Class CandidatesConverter.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class RelationsConverter extends Transformer<Relation, Composite> {
	
	/**
	 * Instantiates a new candidates converter.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public RelationsConverter(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
		
		new ProcessHook<Relation, Composite>(this) {
			
			@Override
			public void process() {
				final Relation data = getInputData();
				provideOutputData(new Composite(data));
			}
		};
	}
	
}

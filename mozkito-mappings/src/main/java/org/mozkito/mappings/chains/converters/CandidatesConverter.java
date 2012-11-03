/**
 * 
 */
package org.mozkito.mappings.chains.converters;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.mappings.model.Candidate;
import org.mozkito.mappings.model.ICandidate;
import org.mozkito.mappings.model.Relation;

/**
 * The Class CandidatesConverter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
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
				final ICandidate data = getInputData();
				provideOutputData(new Relation(data));
			}
		};
	}
	
}

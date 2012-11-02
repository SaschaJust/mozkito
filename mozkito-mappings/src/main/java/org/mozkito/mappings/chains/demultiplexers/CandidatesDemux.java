/**
 * 
 */
package org.mozkito.mappings.chains.demultiplexers;

import net.ownhero.dev.andama.threads.Demultiplexer;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.mappings.model.Candidate;

/**
 * The Class CandidatesDemux.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CandidatesDemux extends Demultiplexer<Candidate> {
	
	/**
	 * Instantiates a new candidates demux.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public CandidatesDemux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}

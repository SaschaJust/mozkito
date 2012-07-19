/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.threads.Demultiplexer;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.moskito.mapping.model.Candidate;

/**
 * The Class CandidatesDemux.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
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

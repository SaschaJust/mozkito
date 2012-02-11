/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.threads.Demultiplexer;
import net.ownhero.dev.andama.threads.Group;
import de.unisaarland.cs.st.moskito.mapping.elements.Candidate;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CandidatesDemux extends Demultiplexer<Candidate> {
	
	public CandidatesDemux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}

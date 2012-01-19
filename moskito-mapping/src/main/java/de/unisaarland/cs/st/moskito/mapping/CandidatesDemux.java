/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CandidatesDemux extends AndamaDemultiplexer<Mapping> {
	
	public CandidatesDemux(final AndamaGroup threadGroup, final AndamaSettings settings) {
		super(threadGroup, settings, false);
	}
	
}

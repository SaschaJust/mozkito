/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringCandidatesDemux extends AndamaDemultiplexer<MapScore> {
	
	public ScoringCandidatesDemux(final AndamaGroup threadGroup, final AndamaSettings settings) {
		super(threadGroup, settings, false);
	}
	
}

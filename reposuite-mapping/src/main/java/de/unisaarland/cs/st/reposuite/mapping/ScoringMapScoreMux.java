/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringMapScoreMux extends AndamaMultiplexer<MapScore> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public ScoringMapScoreMux(final AndamaGroup threadGroup, final AndamaSettings settings) {
		super(threadGroup, settings, false);
	}
	
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import de.unisaarland.cs.st.reposuite.mapping.model.PersistentMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringMappingMux extends AndamaMultiplexer<PersistentMapping> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public ScoringMappingMux(final AndamaGroup threadGroup, final AndamaSettings settings) {
		super(threadGroup, settings, false);
	}
	
}

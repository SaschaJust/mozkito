/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import de.unisaarland.cs.st.reposuite.mapping.model.FilteredMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringFilterMux extends AndamaMultiplexer<FilteredMapping> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public ScoringFilterMux(final AndamaGroup threadGroup, final AndamaSettings settings) {
		super(threadGroup, settings, false);
	}
	
}

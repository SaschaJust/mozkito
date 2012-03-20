/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Multiplexer;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.moskito.mapping.model.FilteredMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringFilterMux extends Multiplexer<FilteredMapping> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param parallelizable
	 */
	public ScoringFilterMux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}

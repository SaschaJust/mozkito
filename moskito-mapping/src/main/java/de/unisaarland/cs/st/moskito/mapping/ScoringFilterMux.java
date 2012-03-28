/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Multiplexer;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.moskito.mapping.model.FilteredMapping;

/**
 * The Class ScoringFilterMux.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ScoringFilterMux extends Multiplexer<FilteredMapping> {
	
	/**
	 * Instantiates a new scoring filter mux.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public ScoringFilterMux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}

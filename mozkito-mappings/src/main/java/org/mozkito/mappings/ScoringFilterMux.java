/**
 * 
 */
package org.mozkito.mappings;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Multiplexer;
import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.mappings.model.Mapping;

/**
 * The Class ScoringFilterMux.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ScoringFilterMux extends Multiplexer<Mapping> {
	
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

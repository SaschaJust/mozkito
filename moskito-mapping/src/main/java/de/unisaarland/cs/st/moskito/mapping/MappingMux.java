/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Multiplexer;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;

// TODO: Auto-generated Javadoc
/**
 * The Class MappingMux.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class MappingMux extends Multiplexer<Mapping> {
	
	/**
	 * Instantiates a new mapping mux.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 */
	public MappingMux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
	}
	
}

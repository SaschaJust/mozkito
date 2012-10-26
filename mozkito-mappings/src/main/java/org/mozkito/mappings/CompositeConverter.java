/**
 * 
 */
package org.mozkito.mappings;

import java.util.HashSet;
import java.util.Set;

import org.mozkito.mappings.model.Composite;
import org.mozkito.mappings.model.Mapping;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;

/**
 * The Class CandidatesConverter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CompositeConverter extends Transformer<Composite, Mapping> {
	
	/**
	 * Instantiates a new candidates converter.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 */
	public CompositeConverter(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
		
		new ProcessHook<Composite, Mapping>(this) {
			
			@Override
			public void process() {
				final Composite data = getInputData();
				final Set<org.mozkito.mappings.filters.Filter> filters = new HashSet<org.mozkito.mappings.filters.Filter>();
				provideOutputData(new Mapping(data, filters));
			}
		};
	}
	
}

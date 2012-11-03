/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mozkito.mappings.chains.filters;

import net.ownhero.dev.andama.threads.Filter;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Composite;
import org.mozkito.mappings.strategies.Strategy;

/**
 * The Class MappingStrategyProcessor.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class StrategyProcessor extends Filter<Composite> {
	
	/**
	 * Instantiates a new mapping strategy processor.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 * @param strategy
	 *            the strategy
	 */
	public StrategyProcessor(final Group threadGroup, final Settings settings, final Finder finder,
	        final Strategy strategy) {
		super(threadGroup, settings, false);
		new ProcessHook<Composite, Composite>(this) {
			
			@Override
			public void process() {
				final Composite composite = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("StrategyProcessor.processing", strategy.getHandle(), //$NON-NLS-1$
					                                composite.getFrom(), composite.getTo()));
				}
				
				provideOutputData(finder.rate(strategy, composite));
			}
		};
	}
	
}

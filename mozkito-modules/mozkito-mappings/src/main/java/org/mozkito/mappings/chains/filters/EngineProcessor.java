/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.mappings.chains.filters;

import net.ownhero.dev.andama.threads.Filter;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;

/**
 * The Class MappingEngineProcessor.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class EngineProcessor extends Filter<Relation> {
	
	/**
	 * Instantiates a new mapping engine processor.
	 * 
	 * @param group
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param finder
	 *            the finder
	 * @param engine
	 *            the engine
	 */
	public EngineProcessor(final Group group, final Settings settings, final Finder finder, final Engine engine) {
		super(group, settings, false);
		
		new ProcessHook<Relation, Relation>(this) {
			
			@Override
			public void process() {
				final Relation relation = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("EngineProcessor.processing", engine.getClassName(), //$NON-NLS-1$
					                                relation.getFrom(), relation.getTo()));
				}
				
				provideOutputData(finder.score(engine, relation));
			}
		};
	}
}

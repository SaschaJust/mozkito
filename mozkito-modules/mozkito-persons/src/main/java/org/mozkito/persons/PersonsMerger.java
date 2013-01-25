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
/**
 * 
 */
package org.mozkito.persons;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.persons.processing.MergingProcessor;

/**
 * The Class PersonsMerger.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PersonsMerger extends Sink<PersonContainer> {
	
	/**
	 * Instantiates a new persons merger.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param persistenceUtil the persistence util
	 * @param processor the processor
	 */
	public PersonsMerger(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil,
	        final MergingProcessor processor) {
		super(threadGroup, settings, false);
		
		new PostExecutionHook<PersonContainer, PersonContainer>(this) {
			
			@Override
			public void postExecution() {
				processor.consolidate();
				persistenceUtil.commitTransaction();
			}
		};
		
		new PreExecutionHook<PersonContainer, PersonContainer>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<PersonContainer, PersonContainer>(this) {
			
			@Override
			public void process() {
				processor.process(getInputData());
			}
		};
	}
}

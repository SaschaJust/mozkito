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
package de.unisaarland.cs.st.mozkito.persons;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.mozkito.persons.processing.MergingProcessor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersonsMerger extends Sink<PersonContainer> {
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil
	 * @param processor
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

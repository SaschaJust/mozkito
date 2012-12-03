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
package org.mozkito.mappings.chains.sinks;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Mapping;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class MappingPersister.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Persister extends Sink<Mapping> {
	
	/** The Constant PERSIST_COUNT_THRESHOLD. */
	private static final int PERSIST_COUNT_THRESHOLD = 50;
	
	/** The i. */
	private Integer          i                       = 0;
	
	/**
	 * Instantiates a new mapping persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public Persister(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<Mapping, Mapping>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<Mapping, Mapping>(this) {
			
			@Override
			public void process() {
				final Mapping mapping = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("Persister.storing", mapping)); //$NON-NLS-1$
				}
				
				if ((++Persister.this.i % Persister.PERSIST_COUNT_THRESHOLD) == 0) {
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
				
				persistenceUtil.save(mapping);
			}
		};
		
		new PostExecutionHook<Mapping, Mapping>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				persistenceUtil.shutdown();
			}
		};
	}
}

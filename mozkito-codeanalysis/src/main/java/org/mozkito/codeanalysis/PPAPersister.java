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
package org.mozkito.codeanalysis;

import java.util.concurrent.Semaphore;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class PPAPersister.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PPAPersister extends Sink<JavaChangeOperation> {
	
	/** The Constant available. */
	protected static final Semaphore AVAILABLE = new Semaphore(1, true);
	
	/** The i. */
	private Integer                  i         = 0;
	
	/**
	 * Instantiates a new pPA persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public PPAPersister(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void process() {
				final JavaChangeOperation data = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Storing " + data);
				}
				
				try {
					PPAPersister.AVAILABLE.acquire();
				} catch (final InterruptedException e) {
					PPAPersister.AVAILABLE.release();
				}
				if ((++PPAPersister.this.i % 5000) == 0) {
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
				
				persistenceUtil.save(data);
				PPAPersister.AVAILABLE.release();
			}
		};
		
		new PostExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				persistenceUtil.shutdown();
			}
		};
	}
	
}

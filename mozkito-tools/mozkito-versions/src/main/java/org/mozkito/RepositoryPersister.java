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
package org.mozkito;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.ChangeSet;

/**
 * The {@link RepositoryPersister} takes {@link ChangeSet} from the previous node and dumps the data to the database.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class RepositoryPersister extends Sink<ChangeSet> {
	
	// /** The Constant COMMIT_CACHE. */
	// private static final int COMMIT_CACHE = 100;
	
	/** The counter. */
	Integer counter = 0;
	
	/**
	 * Instantiates a new repository persister.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param persistenceUtil
	 *            the persistence util
	 * 
	 */
	public RepositoryPersister(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<ChangeSet, ChangeSet>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<ChangeSet, ChangeSet>(this) {
			
			@Override
			public void process() {
				final ChangeSet data = getInputData();
				if (Logger.logDebug()) {
					Logger.debug("Storing " + data);
				}
				
				// if (((RepositoryPersister.this.counter = RepositoryPersister.this.counter + 1) %
				// RepositoryPersister.COMMIT_CACHE) == 0) {
				// persistenceUtil.commitTransaction();
				// persistenceUtil.beginTransaction();
				// }
				
				persistenceUtil.saveOrUpdate(data);
			}
		};
		
		new PostExecutionHook<ChangeSet, ChangeSet>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				// persistenceUtil.shutdown();
			}
		};
	}
}

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
import org.mozkito.versions.model.RCSTransaction;

/**
 * The {@link RepositoryPersister} taks {@link RCSTransaction} from the previous node and dumps the data to the
 * database.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class RepositoryPersister extends Sink<RCSTransaction> {
	
	private static final int COMMIT_CACHE = 100;
	Integer                  counter      = 0;
	
	/**
	 * @see RepoSuiteSinkThread
	 * @param threadGroup
	 * @param settings
	 * @param persistenceUtil
	 */
	public RepositoryPersister(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				final RCSTransaction data = getInputData();
				if (Logger.logDebug()) {
					Logger.debug("Storing " + data);
				}
				
				if (((RepositoryPersister.this.counter = RepositoryPersister.this.counter + 1) % COMMIT_CACHE) == 0) {
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
				
				persistenceUtil.save(data);
			}
		};
		
		new PostExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				// persistenceUtil.shutdown();
			}
		};
	}
}

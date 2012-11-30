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

import java.util.LinkedList;

import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.Transaction;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class GraphReader.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphReader extends Source<Transaction> {
	
	/**
	 * Instantiates a new graph reader.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param persistenceUtil the persistence util
	 */
	public GraphReader(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		final LinkedList<Transaction> list = new LinkedList<Transaction>();
		
		new PreExecutionHook<Transaction, Transaction>(this) {
			
			@Override
			public void preExecution() {
				final Criteria<Transaction> criteria = persistenceUtil.createCriteria(Transaction.class);
				list.addAll(persistenceUtil.load(criteria));
			}
		};
		
		new ProcessHook<Transaction, Transaction>(this) {
			
			@Override
			public void process() {
				final Transaction transaction = list.poll();
				
				if (Logger.logDebug()) {
					Logger.debug("Providing " + transaction + ".");
				}
				if (transaction != null) {
					providePartialOutputData(transaction);
				} else {
					provideOutputData(null, true);
					setCompleted();
				}
			}
		};
	}
	
}

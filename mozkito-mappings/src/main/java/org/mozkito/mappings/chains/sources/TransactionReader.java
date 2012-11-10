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
package org.mozkito.mappings.chains.sources;

import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class TransactionReader.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TransactionReader extends Source<RCSTransaction> {
	
	/** The iterator. */
	private Iterator<RCSTransaction> iterator;
	
	/**
	 * Instantiates a new transaction reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public TransactionReader(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				final Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				final List<RCSTransaction> list = persistenceUtil.load(criteria);
				TransactionReader.this.iterator = list.iterator();
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (TransactionReader.this.iterator.hasNext()) {
					final RCSTransaction transaction = TransactionReader.this.iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info(Messages.getString("ReportReader.providing", transaction)); //$NON-NLS-1$
					}
					
					providePartialOutputData(transaction);
				} else {
					provideOutputData(null, true);
				}
			}
		};
	}
}

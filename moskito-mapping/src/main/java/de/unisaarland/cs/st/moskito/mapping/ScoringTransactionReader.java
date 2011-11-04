/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringTransactionReader extends AndamaSource<RCSTransaction> {
	
	private Iterator<RCSTransaction> iterator;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil
	 */
	public ScoringTransactionReader(final AndamaGroup threadGroup, final MappingSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void preExecution() {
				Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class);
				List<RCSTransaction> list = persistenceUtil.load(criteria);
				ScoringTransactionReader.this.iterator = list.iterator();
			}
		};
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (ScoringTransactionReader.this.iterator.hasNext()) {
					RCSTransaction report = ScoringTransactionReader.this.iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + report);
					}
					
					providePartialOutputData(report);
				} else {
					provideOutputData(null, true);
				}
			}
		};
	}
}

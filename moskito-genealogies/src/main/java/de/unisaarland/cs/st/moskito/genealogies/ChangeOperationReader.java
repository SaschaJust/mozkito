/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.utils.OperationCollection;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.PPAPersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class ChangeOperationReader extends AndamaSource<OperationCollection> {
	
	private Iterator<RCSTransaction> iterator;
	
	public ChangeOperationReader(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final BranchFactory branchFactory) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void preExecution() {
				
				final Criteria<RCSTransaction> criteria = branchFactory.getPersistenceUtil()
				                                                       .createCriteria(RCSTransaction.class)
				                                                       .eq("branch", branchFactory.getMasterBranch());
				
				final TreeSet<RCSTransaction> list = new TreeSet<RCSTransaction>();
				
				final List<RCSTransaction> masterTransactions = branchFactory.getPersistenceUtil().load(criteria);
				
				list.addAll(masterTransactions);
				
				ChangeOperationReader.this.iterator = list.iterator();
				
				if (Logger.logInfo()) {
					Logger.info("Added " + list.size()
					        + " RCSTransactions that were found in MASTER branch to build the change genealogy.");
				}
			}
		};
		
		new ProcessHook<OperationCollection, OperationCollection>(this) {
			
			@Override
			public void process() {
				if (ChangeOperationReader.this.iterator.hasNext()) {
					final RCSTransaction transaction = ChangeOperationReader.this.iterator.next();
					final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(branchFactory.getPersistenceUtil(),
					                                                                                               transaction);
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + transaction);
					}
					
					providePartialOutputData(new OperationCollection(changeOperations));
					if (!ChangeOperationReader.this.iterator.hasNext()) {
						setCompleted();
					}
				}
			}
		};
	}
}

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
package de.unisaarland.cs.st.moskito;

import java.util.Set;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.IRevDependencyGraph;
import de.unisaarland.cs.st.moskito.rcs.Repository;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class GraphBuilder extends Sink<RCSTransaction> {
	
	private int counter;
	
	public GraphBuilder(final Group threadGroup, final RepositorySettings settings, final Repository repository,
	        final PersistenceUtil persistenceUtil, final BranchFactory branchFactory) {
		super(threadGroup, settings, false);
		final IRevDependencyGraph revDepGraph = repository.getRevDependencyGraph();
		this.counter = 0;
		persistenceUtil.beginTransaction();
		
		new ProcessHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void process() {
				if (Logger.logDebug()) {
					Logger.debug("Updating graph for " + getInputData());
				}
				
				final RCSTransaction rcsTransaction = getInputData();
				final String hash = rcsTransaction.getId();
				
				if (!revDepGraph.hasVertex(hash)) {
					throw new UnrecoverableError("RevDependencyGraph does not contain transaction " + hash);
				}
				
				// set parents
				final String branchParentHash = revDepGraph.getBranchParent(hash);
				if (branchParentHash != null) {
					final RCSTransaction branchParent = persistenceUtil.loadById(branchParentHash, RCSTransaction.class);
					rcsTransaction.setBranchParent(branchParent);
				}
				final String mergeParentHash = revDepGraph.getMergeParent(hash);
				if (mergeParentHash != null) {
					final RCSTransaction mergeParent = persistenceUtil.loadById(mergeParentHash, RCSTransaction.class);
					rcsTransaction.setMergeParent(mergeParent);
				}
				
				// set tags
				final Set<String> tags = revDepGraph.getTags(hash);
				if (tags != null) {
					rcsTransaction.addAllTags(tags);
				}
				
				// persist branches
				final String branchName = revDepGraph.isBranchHead(hash);
				if (branchName != null) {
					final RCSBranch branch = branchFactory.getBranch(branchName);
					branch.setHead(rcsTransaction);
					persistenceUtil.saveOrUpdate(branch);
				}
				persistenceUtil.saveOrUpdate(rcsTransaction);
				if ((++GraphBuilder.this.counter % 15) == 0) {
					persistenceUtil.commitTransaction();
					persistenceUtil.beginTransaction();
				}
			}
		};
		
		new PostExecutionHook<RCSTransaction, RCSTransaction>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
			}
		};
		
	}
}

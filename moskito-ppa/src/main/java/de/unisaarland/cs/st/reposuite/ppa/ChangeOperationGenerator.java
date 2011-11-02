/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class ChangeOperationGenerator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeOperationGenerator {
	
	/** The visitors. */
	private final Set<ChangeOperationVisitor> visitors = new HashSet<ChangeOperationVisitor>();
	
	/** The repo. */
	private final Repository                  repo;
	
	/**
	 * Instantiates a new change operation generator.
	 * 
	 * @param repository
	 *            the repository
	 */
	public ChangeOperationGenerator(final Repository repository) {
		this.repo = repository;
	}
	
	/**
	 * Handle transactions and generate ChangeOperations.
	 * 
	 * @param transactions
	 *            the transactions
	 */
	public void handleTransactions(final List<RCSTransaction> transactions) {
		int size = transactions.size();
		int counter = 0;
		for (RCSTransaction transaction : transactions) {
			
			for (ChangeOperationVisitor visitor : this.visitors) {
				visitor.visit(transaction);
			}
			
			if (Logger.logInfo()) {
				Logger.info("Computing change operations for transaction `" + transaction.getId() + "` (" + (++counter)
						+ "/" + size + ")");
			}
			
			
			PPAUtils.generateChangeOperations(this.repo, transaction, this.visitors);
		}
		for (ChangeOperationVisitor visitor : this.visitors) {
			visitor.endVisit();
		}
	}
	
	/**
	 * Register visitor.
	 * 
	 * @param visitor
	 *            the visitor
	 */
	public void registerVisitor(final ChangeOperationVisitor visitor) {
		this.visitors.add(visitor);
	}
}

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
package org.mozkito.codeanalysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import org.mozkito.codeanalysis.model.JavaElementFactory;
import org.mozkito.codeanalysis.utils.PPAUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class ChangeOperationGenerator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeOperationGenerator {
	
	/** The visitors. */
	private final Set<ChangeOperationVisitor> visitors = new HashSet<ChangeOperationVisitor>();
	
	/** The repo. */
	private final Repository                  repo;
	
	/** The element factory. */
	private final JavaElementFactory          elementFactory;
	
	/** The package filter. */
	private final String[]                    packageFilter;
	
	/**
	 * Instantiates a new change operation generator.
	 * 
	 * @param repository
	 *            the repository
	 * @param elementFactory
	 *            the element factory
	 * @param packageFilter
	 *            the package filter
	 */
	public ChangeOperationGenerator(final Repository repository, final JavaElementFactory elementFactory,
	        final String[] packageFilter) {
		this.repo = repository;
		this.elementFactory = elementFactory;
		this.packageFilter = packageFilter;
	}
	
	/**
	 * Handle transactions and generate ChangeOperations.
	 * 
	 * @param rCSTransactions
	 *            the transactions
	 */
	public void handleTransactions(final List<RCSTransaction> rCSTransactions) {
		final int size = rCSTransactions.size();
		int counter = 0;
		for (final RCSTransaction rCSTransaction : rCSTransactions) {
			
			for (final ChangeOperationVisitor visitor : this.visitors) {
				visitor.visit(rCSTransaction);
			}
			
			if (Logger.logInfo()) {
				Logger.info("Computing change operations for transaction `" + rCSTransaction.getId() + "` ("
				        + (++counter) + "/" + size + ")");
			}
			
			PPAUtils.generateChangeOperations(this.repo, rCSTransaction, this.visitors, this.elementFactory,
			                                  this.packageFilter);
		}
		for (final ChangeOperationVisitor visitor : this.visitors) {
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

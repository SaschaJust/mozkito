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
package de.unisaarland.cs.st.reposuite.ppa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class ChangeOperationReader.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeOperationReader extends AndamaSource<JavaChangeOperation> implements ChangeOperationVisitor {
	
	/** The repository. */
	private final Repository            repository;
	
	/** The transactions. */
	private final List<RCSTransaction>  transactions;
	
	private final String                startWith;
	
	private boolean                     usePPA;
	
	private boolean                     consider;
	
	private int                         counter;
	
	private int                         size;
	
	private Iterator<RCSTransaction>    iterator;
	
	private Set<ChangeOperationVisitor> visitors;
	
	/**
	 * Instantiates a new change operation reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param repository
	 *            the repository
	 * @param transactions
	 *            the transactions
	 * @param startWith
	 *            the transaction is to start with. If null, start with the
	 *            first transaction
	 * @param ppa
	 */
	public ChangeOperationReader(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final Repository repository, final List<RCSTransaction> transactions, final String startWith,
	        final Boolean usePPA) {
		super(threadGroup, settings, false);
		if (usePPA == null) {
			this.usePPA = true;
		} else {
			this.usePPA = usePPA;
		}
		this.repository = repository;
		this.transactions = transactions;
		this.startWith = startWith;
		
		new PreExecutionHook<JavaChangeOperation, JavaChangeOperation>(this) {
			@Override
			public void preExecution() {
				ChangeOperationReader.this.visitors = new HashSet<ChangeOperationVisitor>();
				ChangeOperationReader.this.visitors.add(ChangeOperationReader.this);
				ChangeOperationReader.this.size = transactions.size();
				ChangeOperationReader.this.iterator = transactions.iterator();
				ChangeOperationReader.this.counter = 0;
				
				ChangeOperationReader.this.consider = true;
				if (startWith != null) {
					ChangeOperationReader.this.consider = false;
				}
			}
		};
		
		new ProcessHook<JavaChangeOperation, JavaChangeOperation>(this) {
			@Override
			public void process() {
			    // TODO Auto-generated method stub
			    
			
			if (ChangeOperationReader.this.iterator.hasNext()) {
				RCSTransaction transaction = ChangeOperationReader.this.iterator.next();
				
				if (!ChangeOperationReader.this.consider) {
					if (transaction.getId().equals(startWith)) {
						ChangeOperationReader.this.consider = true;
						ChangeOperationReader.this.size = ChangeOperationReader.this.size - ChangeOperationReader.this.counter;
						ChangeOperationReader.this.counter = 0;
					} else {
						++ChangeOperationReader.this.counter;
						return skipOutputData(transaction);
					}
				}
				
				if (Logger.logInfo()) {
					Logger.info("Computing change operations for transaction `" + transaction.getId() + "` ("
					        + (++ChangeOperationReader.this.counter) + "/" + ChangeOperationReader.this.size + ")");
				}
				if (usePPA) {
					PPAUtils.generateChangeOperations(repository, transaction, ChangeOperationReader.this.visitors);
				} else {
					PPAUtils.generateChangeOperationsNOPPA(repository, transaction, ChangeOperationReader.this.visitors);
				}
				
				// FIXME ????
				skipOutputData(transaction);
			}
			
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #endVisit()
	 */
	@Override
	public void endVisit() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		try {
			if (this.getOutputStorage().getNumReaders() < 1) {
				throw new UnrecoverableError("No readers connected to output storage! Terminating!");
			}
			this.write(change);
		} catch (InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			this.shutdown();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction transaction) {
	}
}

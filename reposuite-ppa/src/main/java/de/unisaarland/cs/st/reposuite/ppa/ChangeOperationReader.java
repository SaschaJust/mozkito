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
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		super.beforeExecution();
		
		this.visitors = new HashSet<ChangeOperationVisitor>();
		this.visitors.add(this);
		this.size = this.transactions.size();
		this.iterator = this.transactions.iterator();
		this.counter = 0;
		
		this.consider = true;
		if (this.startWith != null) {
			this.consider = false;
		}
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
	 * @see net.ownhero.dev.andama.threads.OnlyOutputConnectable#process()
	 */
	@Override
	public JavaChangeOperation process() throws UnrecoverableError, Shutdown {
		if (this.iterator.hasNext()) {
			RCSTransaction transaction = this.iterator.next();
			
			if (!this.consider) {
				if (transaction.getId().equals(this.startWith)) {
					this.consider = true;
					this.size = this.size - this.counter;
					this.counter = 0;
				} else {
					++this.counter;
					return skip(transaction);
				}
			}
			
			if (Logger.logInfo()) {
				Logger.info("Computing change operations for transaction `" + transaction.getId() + "` ("
				        + (++this.counter) + "/" + this.size + ")");
			}
			if (this.usePPA) {
				PPAUtils.generateChangeOperations(this.repository, transaction, this.visitors);
			} else {
				PPAUtils.generateChangeOperationsNOPPA(this.repository, transaction, this.visitors);
			}
			
			// FIXME ????
			return skip(transaction);
		}
		
		return null;
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

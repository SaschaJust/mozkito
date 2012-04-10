/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.blob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * The Class ArtificialBlob.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ArtificialBlob {
	
	/**
	 * Clone.
	 * 
	 * @param blob
	 *            the blob
	 * @return the artificial blob
	 */
	@NoneNull
	public static ArtificialBlob clone(final ArtificialBlob blob) {
		return new ArtificialBlob(blob.blobTransactions);
	}
	
	/** The blob transactions. */
	private final TreeSet<AtomicTransaction> blobTransactions = new TreeSet<AtomicTransaction>();
	
	/**
	 * Instantiates a new artificial blob.
	 * 
	 * @param transaction
	 *            the transaction
	 */
	@NoneNull
	public ArtificialBlob(final AtomicTransaction transaction) {
		if (!add(transaction)) {
			if (Logger.logDebug()) {
				Logger.debug("Adding transaction " + transaction.getTransaction().getId() + " failed!");
			}
		}
	}
	
	/**
	 * Instantiates a new artificial blob.
	 * 
	 * @param input
	 *            the input
	 */
	@NoneNull
	public ArtificialBlob(final Set<AtomicTransaction> input) {
		if (!addAll(input)) {
			if (Logger.logDebug()) {
				Logger.debug("Adding transactions failed!" + StringUtils.join(input, ","));
			}
		}
	}
	
	/**
	 * Adds the.
	 * 
	 * @param transaction
	 *            the transaction
	 * @return true, if successful
	 */
	@NoneNull
	public boolean add(final AtomicTransaction transaction) {
		return this.blobTransactions.add(transaction);
	}
	
	/**
	 * Adds the all.
	 * 
	 * @param blobTransactions
	 *            the blob transactions
	 * @return true, if successful
	 */
	private boolean addAll(final Collection<AtomicTransaction> blobTransactions) {
		return this.blobTransactions.addAll(blobTransactions);
		
	}
	
	/**
	 * Gets the list of change operations.
	 * 
	 * @return the change operations
	 */
	public List<JavaChangeOperation> getAllChangeOperations() {
		List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		for (AtomicTransaction t : this.blobTransactions) {
			result.addAll(t.getOperations());
		}
		return result;
	}
	
	/**
	 * Gets the transactions.
	 * 
	 * @return the transactions
	 */
	public Set<AtomicTransaction> getAtomicTransactions() {
		return this.blobTransactions;
	}
	
	/**
	 * Gets the change operation partitions.
	 * 
	 * @return the change operation partitions
	 */
	public List<List<JavaChangeOperation>> getChangeOperationPartitions() {
		List<List<JavaChangeOperation>> result = new LinkedList<List<JavaChangeOperation>>();
		for (AtomicTransaction t : this.blobTransactions) {
			result.add(new ArrayList<JavaChangeOperation>(t.getOperations()));
		}
		return result;
	}
	
	/**
	 * Gets the day window.
	 *
	 * @return the day window
	 */
	public Long getDayWindow() {
		TreeSet<DateTime> timeStamps = new TreeSet<DateTime>();
		for (RCSTransaction t : getTransactions()) {
			timeStamps.add(t.getTimestamp());
		}
		Days daysBetween = Days.daysBetween(timeStamps.first(), timeStamps.last());
		return Long.valueOf(daysBetween.getDays());
	}
	
	/**
	 * Gets the latest transaction.
	 * 
	 * @return the latest transaction
	 */
	public RCSTransaction getLatestTransaction() {
		return this.blobTransactions.last().getTransaction();
	}
	
	/**
	 * Gets the transactions.
	 * 
	 * @return the transactions
	 */
	public Set<RCSTransaction> getTransactions() {
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		for (AtomicTransaction t : this.blobTransactions) {
			result.add(t.getTransaction());
		}
		return result;
	}
	
	/**
	 * Returns the number of partitions hidden within this artificial blob.
	 * 
	 * @return the number of transaction
	 */
	public int size() {
		return this.blobTransactions.size();
	}
}

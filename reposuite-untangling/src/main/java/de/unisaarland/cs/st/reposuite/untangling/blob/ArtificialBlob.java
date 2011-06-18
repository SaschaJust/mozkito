package de.unisaarland.cs.st.reposuite.untangling.blob;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class ArtificialBlob.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ArtificialBlob {
	
	@NoneNull
	public static ArtificialBlob clone(final ArtificialBlob blob){
		return new ArtificialBlob(blob.blobTransactions);
	}
	
	private final TreeSet<BlobTransaction> blobTransactions = new TreeSet<BlobTransaction>();
	
	@NoneNull
	public ArtificialBlob(final BlobTransaction transaction) {
		if (!add(transaction)) {
			if (Logger.logDebug()) {
				Logger.debug("Adding transaction " + transaction.getTransaction().getId() + " failed!");
			}
		}
	}
	
	/**
	 * Instantiates a new artificial blob.
	 * 
	 * @param changeOperations
	 *            the change operations
	 */
	@NoneNull
	public ArtificialBlob(final Set<BlobTransaction> input) {
		if (!addAll(input)) {
			if (Logger.logDebug()) {
				Logger.debug("Adding transactions failed!" + StringUtils.join(input, ","));
			}
		}
	}
	
	@NoneNull
	public boolean add(final BlobTransaction transaction) {
		return blobTransactions.add(transaction);
	}
	
	private boolean addAll(final Collection<BlobTransaction> blobTransactions) {
		return this.blobTransactions.addAll(blobTransactions);
		
	}
	
	/**
	 * Gets the list of change operations.
	 * 
	 * @return the change operations
	 */
	public List<JavaChangeOperation> getAllChangeOperations() {
		List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		for (BlobTransaction t : blobTransactions) {
			result.addAll(t.getOperations());
		}
		return result;
	}
	
	public List<List<JavaChangeOperation>> getChangeOperationPartitions() {
		List<List<JavaChangeOperation>> result = new LinkedList<List<JavaChangeOperation>>();
		for (BlobTransaction t : blobTransactions) {
			result.add(t.getOperations());
		}
		return result;
	}
	
	public RCSTransaction getLatestTransaction() {
		return blobTransactions.last().getTransaction();
	}
	
	/**
	 * Gets the transactions.
	 * 
	 * @return the transactions
	 */
	public Set<RCSTransaction> getTransactions() {
		Set<RCSTransaction> result = new HashSet<RCSTransaction>();
		for (BlobTransaction t : blobTransactions) {
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
		return blobTransactions.size();
	}
}

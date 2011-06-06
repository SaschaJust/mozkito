package de.unisaarland.cs.st.reposuite.untangling;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Class ArtificialBlob.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ArtificialBlob {
	
	/** The change operations. */
	Map<RCSTransaction, List<JavaChangeOperation>> changeOperations;
	
	/**
	 * Instantiates a new artificial blob.
	 * 
	 * @param changeOperations
	 *            the change operations
	 */
	public ArtificialBlob(final Map<RCSTransaction, List<JavaChangeOperation>> changeOperations) {
		this.changeOperations = changeOperations;
	}
	
	/**
	 * Gets the list of change operations.
	 * 
	 * @return the change operations
	 */
	public List<JavaChangeOperation> getAllChangeOperations() {
		List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		for (List<JavaChangeOperation> operations : changeOperations.values()) {
			result.addAll(operations);
		}
		
		return result;
	}
	
	public List<List<JavaChangeOperation>> getChangeOperationPartitions() {
		List<List<JavaChangeOperation>> result = new LinkedList<List<JavaChangeOperation>>();
		result.addAll(changeOperations.values());
		return result;
	}
	
	/**
	 * Gets the transactions.
	 * 
	 * @return the transactions
	 */
	public Set<RCSTransaction> getTransactions() {
		return changeOperations.keySet();
	}
	
	/**
	 * Returns the number of partitions hidden within this artificial blob.
	 * 
	 * @return the number of transaction
	 */
	public int size() {
		return getTransactions().size();
	}
	
}

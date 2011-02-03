package de.unisaarland.cs.st.reposuite.ppa;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
	 * Handle transactions and generate ChangeOperations
	 * 
	 * @param transactions
	 *            the transactions
	 */
	public void handleTransactions(final List<RCSTransaction> transactions) {
		for (RCSTransaction transaction : transactions) {
			
			if (Logger.logInfo()) {
				Logger.info("Computing change operations for transaction `" + transaction.getId() + "`");
			}
			
			for(ChangeOperationVisitor visitor : this.visitors){
				visitor.visit(transaction);
			}
			
			Collection<JavaChangeOperation> changeOperations = PPAUtils.getChangeOperations(this.repo, transaction);
			for (JavaChangeOperation op : changeOperations) {
				for(ChangeOperationVisitor visitor : this.visitors){
					visitor.visit(op);
				}
			}
		}
		for(ChangeOperationVisitor visitor : this.visitors){
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

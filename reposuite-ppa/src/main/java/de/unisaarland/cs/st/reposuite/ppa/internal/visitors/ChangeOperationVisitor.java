package de.unisaarland.cs.st.reposuite.ppa.internal.visitors;

import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * The Interface ChangeOperationVisitor.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface ChangeOperationVisitor {
	
	/**
	 * End visit. Called after set of transactions are done.
	 */
	public void endVisit();
	
	/**
	 * Visit.
	 * 
	 * @param change
	 *            the change
	 */
	public void visit(JavaChangeOperation change);
	
	/**
	 * Visit.
	 * 
	 * @param transaction
	 *            the transaction
	 */
	public void visit(RCSTransaction transaction);
	
}

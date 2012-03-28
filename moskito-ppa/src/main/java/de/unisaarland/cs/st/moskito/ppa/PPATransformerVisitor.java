/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.Iterator;
import java.util.LinkedList;

import de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PPATransformerVisitor implements ChangeOperationVisitor {
	
	private final LinkedList<JavaChangeOperation> list = new LinkedList<JavaChangeOperation>();
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor #endVisit()
	 */
	@Override
	public void endVisit() {
	}
	
	/**
	 * @return
	 */
	public Iterator<JavaChangeOperation> getIterator() {
		return this.list.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		this.list.add(change);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction transaction) {
	}
	
}

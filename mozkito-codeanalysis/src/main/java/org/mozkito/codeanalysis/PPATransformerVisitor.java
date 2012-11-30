/**
 * 
 */
package org.mozkito.codeanalysis;

import java.util.Iterator;
import java.util.LinkedList;

import org.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.versions.model.RCSTransaction;


/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class PPATransformerVisitor implements ChangeOperationVisitor {
	
	private final LinkedList<JavaChangeOperation> list = new LinkedList<JavaChangeOperation>();
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.internal.visitors.ChangeOperationVisitor #endVisit()
	 */
	@Override
	public void endVisit() {
		// ignore
	}
	
	/**
	 * @return
	 */
	public Iterator<JavaChangeOperation> getIterator() {
		return this.list.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(org.mozkito.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		this.list.add(change);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(org.mozkito.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction rCSTransaction) {
		// ignore
	}
	
}

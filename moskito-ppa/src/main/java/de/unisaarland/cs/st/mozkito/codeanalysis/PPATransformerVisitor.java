/**
 * 
 */
package de.unisaarland.cs.st.mozkito.codeanalysis;

import java.util.Iterator;
import java.util.LinkedList;

import de.unisaarland.cs.st.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PPATransformerVisitor implements ChangeOperationVisitor {
	
	private final LinkedList<JavaChangeOperation> list = new LinkedList<JavaChangeOperation>();
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.ppa.internal.visitors.ChangeOperationVisitor #endVisit()
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
	 * @see de.unisaarland.cs.st.mozkito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.mozkito.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		this.list.add(change);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.mozkito.rcs.model.RCSTransaction)
	 */
	@Override
	public void visit(final RCSTransaction transaction) {
		// ignore
	}
	
}

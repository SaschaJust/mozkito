/**
 * 
 */
package de.unisaarland.cs.st.moskito.ppa;

import java.util.Iterator;
import java.util.LinkedList;

import de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * @author just
 * 
 */
public class PPATransformerVisitor implements ChangeOperationVisitor {
	
	private final LinkedList<JavaChangeOperation> list = new LinkedList<JavaChangeOperation>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor
	 * #endVisit()
	 */
	@Override
	public void endVisit() {
	}
	
	/**
	 * @return
	 */
	public Iterator<JavaChangeOperation> getIterator() {
		return list.iterator();
	}
	
	public int size(){
		return list.size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.moskito.ppa.internal.visitors.ChangeOperationVisitor
	 * #visit(de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		list.add(change);
	}
}

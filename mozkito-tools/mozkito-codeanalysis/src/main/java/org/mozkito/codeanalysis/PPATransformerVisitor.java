/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.codeanalysis;

import java.util.Iterator;
import java.util.LinkedList;

import org.mozkito.codeanalysis.internal.visitors.ChangeOperationVisitor;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class PPATransformerVisitor.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PPATransformerVisitor implements ChangeOperationVisitor {
	
	/** The list. */
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
	 * Gets the iterator.
	 * 
	 * @return the iterator
	 */
	public Iterator<JavaChangeOperation> getIterator() {
		return this.list.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.internal.visitors.ChangeOperationVisitor #visit(org.mozkito.versions.model.ChangeSet)
	 */
	@Override
	public void visit(final ChangeSet changeSet) {
		// ignore
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.ppa.internal.visitors.ChangeOperationVisitor #visit(org.mozkito.ppa.model.JavaChangeOperation)
	 */
	@Override
	public void visit(final JavaChangeOperation change) {
		this.list.add(change);
	}
	
}

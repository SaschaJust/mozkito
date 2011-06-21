/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
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

/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.codeanalysis.internal.visitors;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.versions.model.Transaction;


/**
 * The Interface ChangeOperationVisitor.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
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
	public void visit(Transaction transaction);
	
}

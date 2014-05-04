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
package org.mozkito.codechanges.lightweightparser.expression;

import java.util.List;

import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;

/**
 * The Class MultFnCall.
 */
public class MultFnCall implements Expression {
	
	/** The calls. */
	private final List<FunctionCall> calls;
	
	/**
	 * Instantiates a new mult fn call.
	 * 
	 * @param calls
	 *            the calls
	 */
	public MultFnCall(final List<FunctionCall> calls) {
		super();
		this.calls = calls;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		for (final FunctionCall c : this.calls) {
			c.buildFunctionModel(fM);
		}
		
		return new Wrapper();
		
	}
	
	/**
	 * Gets the last call.
	 * 
	 * @return the last call
	 */
	public FunctionCall getLastCall() {
		return this.calls.get(this.calls.size() - 1);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.expression.Expression#setAssigIDs(java.util.List)
	 */
	public void setAssigIDs(final List<String> ids) {
		if (this.calls.size() != 0) {
			this.calls.get(this.calls.size() - 1).setAssigIDs(ids);
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = "MultCall: ";
		for (final FunctionCall e : this.calls) {
			s += e.toString();
		}
		return s + " END MULT\n";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		return false;
	}
	
}

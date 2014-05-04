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

import java.util.LinkedList;
import java.util.List;

import org.mozkito.codechanges.lightweightparser.functionModel.Event;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;

/**
 * The Class EmptyExpression.
 */
public class EmptyExpression implements Expression {
	
	/** The e. */
	private static Expression e;
	
	/**
	 * Gets the empty exp.
	 * 
	 * @return the empty exp
	 */
	public static Expression getEmptyExp() {
		if (EmptyExpression.e == null) {
			EmptyExpression.e = new EmptyExpression();
		}
		return EmptyExpression.e;
	}
	
	/**
	 * Instantiates a new empty expression.
	 */
	private EmptyExpression() {
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fm) {
		return new Wrapper();
		
	}
	
	/**
	 * Gets the events.
	 * 
	 * @return the events
	 */
	public List<Event> getEvents() {
		
		return new LinkedList<Event>();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.expression.Expression#setAssigIDs(java.util.List)
	 */
	public void setAssigIDs(final List<String> ids) {
		// do nothing
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EMPTY_EXP ";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		
		return true;
	}
}

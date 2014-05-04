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
import org.mozkito.codechanges.lightweightparser.functionModel.Node;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;

/**
 * The Class Identifier.
 */
public class Identifier implements Expression {
	
	/**
	 * Make identifier.
	 * 
	 * @param name
	 *            the name
	 * @return the expression
	 */
	public static Expression makeIdentifier(final String name) {
		final Identifier i = new Identifier(name);
		if (i.notConstant()) {
			return i;
		} else {
			return EmptyExpression.getEmptyExp();
		}
	}
	
	/** The name. */
	private String name;
	
	/**
	 * Instantiates a new identifier.
	 * 
	 * @param name
	 *            the name
	 */
	private Identifier(final String name) {
		super();
		this.name = name.trim();
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		final Wrapper w = new Wrapper();
		if (this.name.equals("break")) {
			w.addBreakNode(fM.getEnd());
			fM.setEnd(new Node());
		} else if (this.name.equals("continue")) {
			w.addConNode(fM.getEnd());
			fM.setEnd(new Node());
		}
		return w;
	}
	
	/**
	 * Gets the events.
	 * 
	 * @return the events
	 */
	public List<Event> getEvents() {
		// TODO Auto-generated method stub
		return new LinkedList<Event>();
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks if is break.
	 * 
	 * @return true, if is break
	 */
	public boolean isBreak() {
		return this.name.equals("break");
	}
	
	/**
	 * Not constant.
	 * 
	 * @return true, if successful
	 */
	public boolean notConstant() {
		return !this.name.toLowerCase().equals("null") && !this.name.toLowerCase().equals("true")
		        && !this.name.toLowerCase().equals("false");
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
	 * Sets the name.
	 * 
	 * @param string
	 *            the new name
	 */
	public void setName(final String string) {
		this.name = string;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ID: " + this.name + " ";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		
		return !(this.name.equals("break") || this.name.equals("continue"));
	}
	
}

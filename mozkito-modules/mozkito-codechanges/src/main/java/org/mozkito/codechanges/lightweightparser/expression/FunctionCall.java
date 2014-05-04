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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mozkito.codechanges.lightweightparser.functionModel.Edge;
import org.mozkito.codechanges.lightweightparser.functionModel.Event;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Obj;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;

/**
 * The Class FunctionCall.
 */
public class FunctionCall implements Expression {
	
	/** The id. */
	public static long             id = 0;
	
	/** The target. */
	private final String           target;
	
	/** The args. */
	private final List<Expression> args;
	
	/** The name. */
	private final String           name;
	
	/** The ids. */
	private final List<String>     ids;
	
	/**
	 * Instantiates a new function call.
	 * 
	 * @param target
	 *            the target
	 * @param args
	 *            the args
	 * @param name
	 *            the name
	 */
	public FunctionCall(final String target, final List<Expression> args, final String name) {
		super();
		this.target = target.trim();
		this.args = args;
		this.name = name.trim();
		this.ids = new ArrayList<String>();
	}
	
	/**
	 * Adds the assig id.
	 * 
	 * @param string
	 *            the string
	 */
	public void addAssigId(final String string) {
		this.ids.add(string);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		
		final String name = this.name.replaceFirst("new ", "").trim();
		final List<Obj> objects = new LinkedList<Obj>();
		if (this.target.length() != 0) {
			objects.add(new Obj(0, this.target, name, this.args.size()));
		}
		
		for (final String s : this.ids) {
			if (this.name.startsWith("new ")) {
				objects.add(new Obj(0, s, name, this.args.size()));
			} else {
				objects.add(new Obj(-1, s, name, this.args.size()));
			}
		}
		
		int i = 1;
		for (final Expression e : this.args) {
			if (e instanceof FunctionCall) {
				final String s = "%" + FunctionCall.id;
				FunctionCall.id++;
				objects.add(new Obj(i, s, name, this.args.size()));
				((FunctionCall) e).addAssigId(s);
			}
			if (e instanceof MultFnCall) {
				final String s = "%" + FunctionCall.id;
				FunctionCall.id++;
				objects.add(new Obj(i, s, name, this.args.size()));
				final FunctionCall fc = ((MultFnCall) e).getLastCall();
				fc.addAssigId(s);
			}
			if (e instanceof Identifier) {
				objects.add(new Obj(i, ((Identifier) e).getName(), name, this.args.size()));
			} else if (!e.useless()) {
				
				e.buildFunctionModel(fM);
				
			}
			i++;
		}
		
		fM.addEdge(new Edge(new Event(name, objects)));
		
		return new Wrapper();
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.expression.Expression#setAssigIDs(java.util.List)
	 */
	public void setAssigIDs(final List<String> ids) {
		this.ids.addAll(ids);
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String s = "FunctionCall-> " + this.target.toString() + "." + this.name.toString() + "( "
		        + this.args.toString() + ") ids: " + this.ids + "\n";
		return s;
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

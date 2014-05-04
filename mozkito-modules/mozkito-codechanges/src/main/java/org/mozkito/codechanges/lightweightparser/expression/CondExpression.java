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
import java.util.List;

import org.mozkito.codechanges.lightweightparser.functionModel.Edge;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Node;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;

/**
 * The Class CondExpression.
 */
public class CondExpression implements Expression {
	
	/** The cond. */
	Expression cond;
	
	/** The first. */
	Expression first;
	
	/** The second. */
	Expression second;
	
	/**
	 * Instantiates a new cond expression.
	 * 
	 * @param cond
	 *            the cond
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 */
	public CondExpression(final Expression cond, final Expression first, final Expression second) {
		super();
		this.cond = cond;
		this.first = first;
		this.second = second;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		// TODO Auto-generated method stub
		final Wrapper w = new Wrapper();
		this.cond.buildFunctionModel(fM);
		
		final Node split = fM.getEnd();
		
		w.add(this.first.buildFunctionModel(fM));
		
		final FunctionModel temp = new FunctionModel();
		w.add(this.second.buildFunctionModel(temp));
		
		for (final Edge e : temp.getStart().getOutgoingEdges()) {
			split.addOutEdge(e);
		}
		
		if ((fM.getEnd() != split) || (temp.getEnd() != temp.getStart())) {
			final Node newEnd = new Node();
			if (fM.getEnd() != split) {
				for (final Edge e : fM.getEnd().getIncomingEdges()) {
					newEnd.addInEdge(e);
				}
				fM.getEnd().setIncomingEdges(new ArrayList<Edge>());
			}
			
			for (final Edge e : temp.getEnd().getIncomingEdges()) {
				newEnd.addInEdge(e);
			}
			
			fM.setEnd(newEnd);
		}
		
		return w;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.expression.Expression#setAssigIDs(java.util.List)
	 */
	public void setAssigIDs(final List<String> ids) {
		this.first.setAssigIDs(ids);
		this.second.setAssigIDs(ids);
		
	}
	
	/*
	 * public List<Event> getEvents() { List<Event> evList = new LinkedList<Event>(); List<Event> condList =
	 * cond.getEvents(); List<List<Event>> branchList = new LinkedList<List<Event>>(); List<Event> list1 =
	 * first.getEvents(); list1.addAll(0, condList); List<Event> list2 = second.getEvents(); list1.addAll(0, condList);
	 * branchList.add(list1); branchList.add(list2); evList.add(new EBranch(branchList)); return evList; }
	 */
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String s = "CExp: " + this.cond.toString() + "-> (" + this.first.toString() + " OR "
		        + this.second.toString() + ") ";
		return s;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		return this.cond.useless() && this.first.useless() && this.second.useless();
	}
	
}

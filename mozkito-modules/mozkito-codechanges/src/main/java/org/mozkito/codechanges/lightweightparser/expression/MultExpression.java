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
 * The Class MultExpression.
 */
public class MultExpression implements Expression {
	
	/** The exp list. */
	private final List<Expression> expList;
	
	/**
	 * Instantiates a new mult expression.
	 * 
	 * @param expList
	 *            the exp list
	 */
	public MultExpression(final List<Expression> expList) {
		super();
		this.expList = expList;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		for (final Expression e : this.expList) {
			e.buildFunctionModel(fM);
			// System.out.println(fM);
		}
		return new Wrapper();
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
		final String s = "MultExpression: " + this.expList.toString() + "\n";
		return s;
	}
	
	/*
	 * public Wrapper buildFunctionModel(FunctionModel fM) { Wrapper w = new Wrapper(); Body forwards = new Body(); Body
	 * backwards = new Body(); List<Expression> expList = new LinkedList<Expression>(); for(Expression e: this.expList){
	 * if(!e.useless()) expList.add(e); } if(expList.isEmpty()) return w; else if(expList.size() == 1){
	 * w.add(expList.get(0).buildFunctionModel(fM)); } else{ for(Expression e: expList){ forwards.addStatement(e);
	 * backwards.addStatementFront(e); } if(!forwards.useless()){ FunctionModel m1 = new FunctionModel(); FunctionModel
	 * m2 = new FunctionModel(); w.add(forwards.buildFunctionModel(m1)); backwards.buildFunctionModel(m2);
	 * fM.addObjects(m1.getObjects()); fM.addObjects(m2.getObjects()); Node start = m1.getStart(); for(Edge e:
	 * m2.getStart().getOutgoingEdges()){ start.addOutEdge(e); } Node end = m1.getEnd(); for(Edge e:
	 * m2.getEnd().getIncomingEdges()){ end.addInEdge(e); } for(Edge e: m1.getStart().getOutgoingEdges()){
	 * fM.getEnd().addOutEdge(e); } fM.setEnd(end); } } return w; }
	 */
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		boolean useless = true;
		for (final Expression e : this.expList) {
			useless &= e.useless();
		}
		return useless;
	}
	
}

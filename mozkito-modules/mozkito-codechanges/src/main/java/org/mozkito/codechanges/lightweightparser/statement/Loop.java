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
package org.mozkito.codechanges.lightweightparser.statement;

import java.util.ArrayList;
import java.util.List;

import org.mozkito.codechanges.lightweightparser.functionModel.Edge;
import org.mozkito.codechanges.lightweightparser.functionModel.Event;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.LoopNode;
import org.mozkito.codechanges.lightweightparser.functionModel.Node;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;
import org.mozkito.codechanges.lightweightparser.structure.Body;

/**
 * The Class Loop.
 */
public class Loop implements Statement {
	
	/** The once. */
	List<Statement> once;
	
	/** The condition. */
	Statement       condition;
	
	/** The body. */
	Body            body;
	
	/**
	 * Instantiates a new loop.
	 * 
	 * @param once
	 *            the once
	 * @param cond
	 *            the cond
	 * @param body
	 *            the body
	 */
	public Loop(final List<Statement> once, final Statement cond, final Body body) {
		super();
		this.once = once;
		this.condition = cond;
		this.body = body;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		
		for (final Statement s : this.once) {
			s.buildFunctionModel(fM);
		}
		
		final LoopNode beforeCondition = new LoopNode();
		for (final Edge e : fM.getEnd().getIncomingEdges()) {
			beforeCondition.addNormalInEdge(e);
		}
		fM.getEnd().setIncomingEdges(new ArrayList<Edge>());
		
		if (fM.getEnd() == fM.getStart()) {
			fM.setStart(beforeCondition);
		}
		
		fM.setEnd(beforeCondition);
		this.condition.buildFunctionModel(fM);
		
		final Node afterCondition = fM.getEnd();
		fM.addEdge(new Edge(Event.getEpsilon()));
		
		final Wrapper w = this.body.buildFunctionModel(fM);
		
		final Node last = fM.getEnd();
		if (last != beforeCondition) {
			for (final Edge e : last.getIncomingEdges()) {
				beforeCondition.addInEdge(e);
			}
		}
		
		final Node newEnd = new Node();
		
		for (final Node n : w.breakNodes) {
			new Edge(Event.getEpsilon(), n, newEnd);
		}
		for (final Node n : w.continueNodes) {
			new Edge(Event.getEpsilon(), n, beforeCondition);
		}
		
		new Edge(Event.getEpsilon(), afterCondition, newEnd);
		fM.setEnd(newEnd);
		
		return new Wrapper();
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "\nLOOP: once(" + this.once.toString() + ") , always(" + this.condition.toString() + "), Body: \n"
		        + this.body.toString() + " END LOOP";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		boolean useless = this.body.useless();
		for (final Statement s : this.once) {
			useless &= s.useless();
		}
		
		useless &= this.condition.useless();
		
		return useless;
	}
	
}

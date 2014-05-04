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

import org.mozkito.codechanges.lightweightparser.expression.Expression;
import org.mozkito.codechanges.lightweightparser.functionModel.Edge;
import org.mozkito.codechanges.lightweightparser.functionModel.Event;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Node;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;
import org.mozkito.codechanges.lightweightparser.structure.Body;

/**
 * The Class Block.
 */
public class Block implements Statement {
	
	/** The condition. */
	private final Expression condition;
	
	/** The body. */
	private final Body       body;
	
	/**
	 * Instantiates a new block.
	 * 
	 * @param condition
	 *            the condition
	 * @param body
	 *            the body
	 */
	public Block(final Expression condition, final Body body) {
		super();
		this.condition = condition;
		this.body = body;
	}
	
	/**
	 * Builds the fm branch.
	 * 
	 * @param fM
	 *            the f m
	 * @param w
	 *            the w
	 * @return the node
	 */
	public Node buildFMBranch(final FunctionModel fM,
	                          final Wrapper w) {
		
		this.condition.buildFunctionModel(fM);
		final Node temp = fM.getEnd();
		fM.addEdge(new Edge(Event.getEpsilon()));
		w.add(this.body.buildFunctionModel(fM));
		
		return temp;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		
		this.condition.buildFunctionModel(fM);
		final Node temp = fM.getEnd();
		fM.addEdge(new Edge(Event.getEpsilon()));
		final Wrapper w = this.body.buildFunctionModel(fM);
		
		if ((temp != fM.getEnd()) && ((temp == fM.getStart()) || (temp.getIncomingEdges().size() > 0))) {
			new Edge(Event.getEpsilon(), temp, fM.getEnd());
		}
		
		return w;
		
	}
	
	/**
	 * Gets the body.
	 * 
	 * @return the body
	 */
	public Body getBody() {
		return this.body;
	}
	
	/**
	 * Gets the condition.
	 * 
	 * @return the condition
	 */
	public Expression getCondition() {
		return this.condition;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BLOCK: " + this.condition.toString() + "\nBody: " + this.body.toString() + "END BLOCK\n";
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		return this.condition.useless() && this.body.useless();
	}
	
}

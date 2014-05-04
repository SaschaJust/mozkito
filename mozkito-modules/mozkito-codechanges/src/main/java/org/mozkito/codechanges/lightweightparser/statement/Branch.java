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

import java.util.LinkedList;

import org.mozkito.codechanges.lightweightparser.functionModel.Edge;
import org.mozkito.codechanges.lightweightparser.functionModel.Event;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Node;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;

/**
 * The Class Branch.
 */
public class Branch implements Statement {
	
	/** The list. */
	private final LinkedList<Block> list;
	
	/**
	 * Instantiates a new branch.
	 * 
	 * @param list
	 *            the list
	 */
	public Branch(final LinkedList<Block> list) {
		super();
		this.list = list;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#buildFunctionModel(org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel)
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		final Wrapper w = new Wrapper();
		if (this.list.isEmpty()) {
			return w;
		}
		
		final Node newEnd = new Node();
		Node prev = fM.getEnd();
		
		if (this.list.size() == 1) {
			w.add(this.list.get(0).buildFunctionModel(fM));
		} else {
			for (final Block b : this.list) {
				final FunctionModel temp = new FunctionModel();
				Node n = b.buildFMBranch(temp, w);
				if (n == temp.getStart()) {
					n = prev;
				}
				
				for (final Edge e : temp.getStart().getOutgoingEdges()) {
					prev.addOutEdge(e);
				}
				
				if ((temp.getEnd() == fM.getStart()) || (temp.getEnd().getIncomingEdges().size() != 0)) {
					new Edge(Event.getEpsilon(), temp.getEnd(), newEnd);
				}
				
				prev = n;
			}
			if ((prev == fM.getStart()) || (prev.getIncomingEdges().size() > 0)) {
				new Edge(Event.getEpsilon(), prev, newEnd);
			}
			fM.setEnd(newEnd);
		}
		return w;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String str = "BRANCH \n";
		boolean first = true;
		for (final Block b : this.list) {
			if (first) {
				str += b.toString();
				first = false;
			} else {
				str += "NBRANCH \n" + b.toString();
			}
			
		}
		str += "END BRANCH\n";
		return str;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.codechanges.lightweightparser.statement.Statement#useless()
	 */
	public boolean useless() {
		boolean useless = true;
		for (final Block b : this.list) {
			useless &= b.useless();
		}
		return useless;
	}
	
}

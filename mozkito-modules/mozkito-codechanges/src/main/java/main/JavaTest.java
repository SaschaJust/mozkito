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

package main;

import java.util.List;
import java.util.Stack;

import org.mozkito.codechanges.lightweightparser.Project;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Node;
import org.mozkito.codechanges.lightweightparser.structure.Function;

/**
 * The Class JavaTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class JavaTest {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final Project proj = new Project(true);
		// proj.parse("aspectJ", ".*\\.java", null);
		proj.parse("/Users/just/Development/mozkito", ".*\\.(java)", null);
		
		// build the function models
		proj.makeModels();
		final List<Function> functions = proj.getFunctions();
		
		// access each function model
		for (final Function f : functions) {
			final FunctionModel model = f.getModel();
			model.getStart(); // returns the start node of the model
			model.getEdgeSet(); // returns all edges in the model
			model.getNodeSet(); // returns all nodes in the model
			
			// alternatively the model can be traversed by accessing the outgoing edges
			// of each node: (the following example only terminates for non-cyclic models)
			
			final Stack<Node> stack = new Stack<Node>();
			stack.add(model.getStart());
			
			// while (!stack.isEmpty()) {
			// final Node n = stack.pop();
			// for (final Edge e : n.getOutgoingEdges()) {
			// stack.add(e.getTo());
			// }
			// }
		}
		
	}
}

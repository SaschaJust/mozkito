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

import org.mozkito.codechanges.lightweightparser.Project;

/**
 * The Class Main.
 */
public class Main {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		
		final Project proj = new Project(true);
		System.out.println("parsing project");
		// proj.parse("../../defective/cyrus", ".*\\.(c)", null);
		proj.parse("/Users/just/Development/mozkito", ".*\\.(java)", null);
		
		System.out.println("building models");
		// build the function models
		proj.makeModels();
		/*
		 * List<Function> functions = proj.getFunctions(); System.out.println("traversing models"); //access each
		 * function model for(Function f: functions){ System.out.println(f.getName()); FunctionModel model =
		 * f.getModel(); //System.out.println(model.getStart()); //returns the start node of the model
		 * model.getEdgeSet(); //returns all edges in the model model.getNodeSet(); //returns all nodes in the model }
		 */
		System.out.println("making constraints");
		proj.makeConstraints();
		
		System.out.println("writing files");
		proj.writeFiles("testabc");
	}
	
}

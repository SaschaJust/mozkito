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
package org.mozkito.codechanges.lightweightparser;

import java.io.File;
import java.util.List;

import org.mozkito.codechanges.lightweightparser.constraints.ConstraintKeeper;
import org.mozkito.codechanges.lightweightparser.parser.BodyParser;
import org.mozkito.codechanges.lightweightparser.parser.ControlStructureParser;
import org.mozkito.codechanges.lightweightparser.parser.InputReader;
import org.mozkito.codechanges.lightweightparser.parser.Parser;
import org.mozkito.codechanges.lightweightparser.parser.TopLevel;
import org.mozkito.codechanges.lightweightparser.structure.Function;
import org.mozkito.codechanges.lightweightparser.structure.FunctionCollector;

/**
 * The Class Project. This class represents a project for the purposes of extracting function models and sequential
 * constraints from the project's source. It is used to parse the project's source code and store relevant information
 * in an abstract representation. Function models for each function in the project can be created, and then sequential
 * constraints can be extracted.
 * 
 * <pre>
 * Example usage:
 * Creating bri, gra, and met files for a project with two source files (sourceA.c and sourceB.c)
 * 		Project proj = new Project(false);
 * 		proj.parse("sourceA.c");
 * 		proj.parse("sourceB.c");
 * 		proj.makeModels();
 * 		proj.makeConstraints();
 * 		proj.writeFiles("output/testProject");
 * 
 * Parsing all java files in all directories and subdirectories of a the directory aspectJ and then accessing function models:
 * 		Project proj = new Project(true);
 * 		proj.parse("aspectJ", ".*\\.java", null);
 * 		
 * 		//build the function models
 * 		proj.makeModels();
 * 		List<Function> functions = proj.getFunctions();
 * 		
 * 		//access each function model
 * 		for(Function f: functions){
 * 			FunctionModel model = f.getModel();
 * 			model.getStart(); //returns the start node of the model
 * 			model.getEdgeSet(); //returns all edges in the model
 * 			model.getNodeSet(); //returns all nodes in the model
 * 
 * 			//alternatively the model can be traversed by accessing the outgoing edges
 * 			//of each node: (the following example only terminates for non-cyclic models)
 * 
 * 			Stack<Node> stack = new Stack<Node>();
 * 			stack.add(model.getStart());
 * 
 * 			while(!stack.isEmpty()){
 * 			Node n = stack.pop();
 * 			for(Edge e: n.getOutgoingEdges()){
 * 					stack.add(e.getTo());
 * 				}
 * 			}
 * 		}
 * 
 * 		}
 * 
 * </pre>
 * 
 * 
 * 
 */
public class Project {
	
	/** The input reader instance. */
	private final InputReader       input;
	
	/** The top level parser. */
	private final TopLevel          topLevelParser;
	
	/** The function collector. */
	private final FunctionCollector functionCollector;
	
	/** The constraint keeper. */
	private final ConstraintKeeper  constraintKeeper;
	
	/**
	 * Instantiates a new project.
	 * 
	 * @param objectOriented
	 *            true if the input language is object oriented
	 */
	public Project(final boolean objectOriented) {
		final BodyParser bp = new BodyParser();
		final ControlStructureParser csp = new ControlStructureParser();
		
		this.topLevelParser = new TopLevel();
		this.functionCollector = new FunctionCollector();
		this.constraintKeeper = new ConstraintKeeper();
		this.input = new InputReader();
		
		bp.setParsers(this.input, this.topLevelParser, csp, this.functionCollector, objectOriented);
		this.topLevelParser.setParsers(this.input, bp, this.functionCollector, this.constraintKeeper);
		csp.setParsers(this.input, bp, objectOriented);
		
	}
	
	/**
	 * Gets all functions that have been parsed.
	 * 
	 * @return the list of functions
	 */
	public List<Function> getFunctions() {
		return this.functionCollector.getFunctions();
	}
	
	/**
	 * Creates sequential constraints for all functions. Should only be called once function models have been built
	 * (ie., after makeModels() has been called)
	 */
	public void makeConstraints() {
		this.functionCollector.makeConstraints();
	}
	
	/**
	 * Makes function models for all functions present in the function collector. Should only be called once all files
	 * in the project have been parsed.
	 */
	public void makeModels() {
		this.functionCollector.buildFunctionModels();
	}
	
	/**
	 * Parses all files and directories in the list. Only files whose name matches regexName and whose path matches
	 * regexPath are parsed. Only directories whose path matches regexPath are parsed.
	 * 
	 * Directories are parsed recursively, meaning all subdirectories are also parsed.
	 * 
	 * Abstract representations of all functions that were identified in the files are created and added to the function
	 * collector
	 * 
	 * @param files
	 *            the paths to the files or directories to be parsed
	 * @param regexName
	 *            the regular expression for file names, or null in which case the expression is set to ".*"
	 * @param regexPath
	 *            the regular expression for file and directory paths, or null in which case the expression is set to
	 *            ".*"
	 */
	public void parse(final List<String> files,
	                  final String regexName,
	                  final String regexPath) {
		final Parser parser = new Parser(this.input, this.topLevelParser, regexName, regexPath);
		
		for (final String s : files) {
			final File file = new File(s);
			parser.parse(file);
		}
	}
	
	/**
	 * Parses a single file
	 * 
	 * Abstract representations of all functions that were identified in the file are created and added to the function
	 * collector.
	 * 
	 * @param path
	 *            the path of the file that is to be parsed
	 */
	public void parse(final String path) {
		final Parser parser = new Parser(this.input, this.topLevelParser, null, null);
		final File file = new File(path);
		if (file.isFile()) {
			parser.parse(file);
		} else {
			System.out.println(path + " is not a valid file path");
		}
	}
	
	/**
	 * Parses the file or directory with the given path. Only files whose name matches regexName and whose path matches
	 * regexPath are parsed. Only directories whose path matches regexPath are parsed.
	 * 
	 * Directories are parsed recursively, meaning all subdirectories are also parsed.
	 * 
	 * Abstract representations of all functions that were identified in the files are created and added to the function
	 * collector
	 * 
	 * @param path
	 *            the paths to the file or directory to be parsed
	 * @param regexName
	 *            the regular expression for file names, or null in which case the expression is set to ".*"
	 * @param regexPath
	 *            the regular expression for file and directory paths, or null in which case the expression is set to
	 *            ".*"
	 */
	public void parse(final String path,
	                  final String regexName,
	                  final String regexPath) {
		final Parser parser = new Parser(this.input, this.topLevelParser, regexName, regexPath);
		final File file = new File(path);
		parser.parse(file);
		
	}
	
	/**
	 * Writes the bri, gra and met files. Three files are created:<br>
	 * fileName.bri, <br>
	 * fileName.met,<br>
	 * fileName.gra
	 * 
	 * @param fileName
	 *            the name and path of the file
	 */
	public void writeFiles(final String fileName) {
		this.functionCollector.writeBriFile(fileName);
		this.functionCollector.writeMetFile(fileName);
		this.constraintKeeper.writeGraFile(fileName);
	}
	
}

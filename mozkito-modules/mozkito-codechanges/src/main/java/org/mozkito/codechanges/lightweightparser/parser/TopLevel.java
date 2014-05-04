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
package org.mozkito.codechanges.lightweightparser.parser;

import org.mozkito.codechanges.lightweightparser.constraints.ConstraintKeeper;
import org.mozkito.codechanges.lightweightparser.structure.Body;
import org.mozkito.codechanges.lightweightparser.structure.Function;
import org.mozkito.codechanges.lightweightparser.structure.FunctionCollector;

/**
 * The Class TopLevel.
 */
public class TopLevel {
	
	/** The Constant CLASS_PATTERN. */
	public static final String CLASS_PATTERN     = "(.*\\s+)?(class|struct)\\s+[^\\(\\)]*\\{";
	
	/** The Constant FUNCTION_PATTERN. */
	public static final String FUNCTION_PATTERN  = ".*\\**[\\w:~]+\\s*\\(.*\\).*\\{";
	
	/** The control structure. */
	private static String      CONTROL_STRUCTURE = "(if)|(while)|(else(\\s*if)?)|(catch)|(for(\\s*each)?)";
	
	/** The input. */
	private InputReader        input;
	
	/** The body parser. */
	private BodyParser         bodyParser;
	
	/** The function collector. */
	private FunctionCollector  functionCollector;
	
	/** The constraint keeper. */
	private ConstraintKeeper   constraintKeeper;
	
	/**
	 * Removes unnecessary clutter from a string containing a function signature.
	 * 
	 * @param funDef
	 *            , the string to be cleaned up
	 * @return the cleaned string
	 */
	private String cleanUpDef(final String funDef) {
		String localFunDef = funDef;
		// replace the keyword throws and any listed exceptions
		localFunDef = localFunDef.replaceAll(" throws? .*\\{", " {");
		
		/*
		 * this next clean up is for javaScript code, it removes the colon and function keyword that come after the
		 * function name
		 */
		localFunDef = localFunDef.replaceFirst(":\\s*function", "");
		return localFunDef;
	}
	
	/**
	 * This is the method that should be called to start parsing from a newly created InputReader. It takes input from
	 * the InputReader and parses it. Any functions that are found in the file that the InputReader is reading will be
	 * parsed and stored as objects of the class structure.Function in the FunctionCollector.
	 */
	public void parse() {
		String curr;
		while (this.input.hasNext()) {
			curr = this.input.next().trim();
			parseTopLevel(curr, "");
		}
	}
	
	/**
	 * Parses a class. Any functions that are found in this class are parsed and stored in the FunctionCollector. If the
	 * class contains subclasses, these are parsed as well.
	 * 
	 * @param classDef
	 *            a string containing the class signature
	 * @param className
	 *            the name of the class that the class being parsed is in (an empty string if there is no class)
	 */
	public void parseClass(final String classDef,
	                       final String className) {
		String localClassName = className;
		if (localClassName == null) {
			localClassName = "";
		}
		
		String name = classDef;
		
		final int classIndex = classDef.indexOf("class");
		final int structIndex = classDef.indexOf("struct");
		if (classIndex >= 0) {
			final String nameStart = classDef.substring(classIndex + 5).trim();
			final int space = nameStart.indexOf(' ');
			if (space >= 0) {
				name = nameStart.substring(0, space);
			} else {
				name = nameStart.substring(0, nameStart.length() - 1);
			}
		} else if (structIndex >= 0) {
			final String nameStart = classDef.substring(structIndex + 6).trim();
			if (nameStart.indexOf(' ') > 0) {
				name = nameStart.substring(0, nameStart.indexOf(' '));
			}
		}
		
		if (!this.input.hasNext()) {
			return;
		}
		
		String curr = this.input.next().trim();
		while (this.input.hasNext() && !curr.matches(".*\\}")) {
			parseTopLevel(curr, localClassName + "." + name);
			curr = this.input.next().trim();
		}
		
	}
	
	/**
	 * Takes a string containing a function signature, rechecks to make sure it is really a function signature, if it
	 * is, the function is parsed.
	 * 
	 * @param funDef
	 *            the string containing the function signature
	 * @param className
	 *            the name of the class that the function is in (an empty string if there is no class)
	 * @return an Object of the class structure.Function representing the parsed function , or null if funDef did not
	 *         contain a function signature
	 */
	public Function parseFunction(final String funDef,
	                              final String className) {
		String localClassName = className;
		if (localClassName == null) {
			localClassName = "";
		}
		
		final String path = this.input.getPath();
		final int begin = this.input.getLineNumber();
		this.input.getLocation();
		final String localFunDef = cleanUpDef(funDef);
		
		if (!localFunDef.matches(TopLevel.FUNCTION_PATTERN)) {
			return null;
		}
		
		final int endArgs = localFunDef.lastIndexOf(')');
		int startArgs = endArgs;
		
		int numBrackets = 1;
		char c;
		while (numBrackets > 0) {
			startArgs--;
			if (startArgs < 0) {
				return null;
			}
			c = localFunDef.charAt(startArgs);
			switch (c) {
				case ')':
					numBrackets++;
					break;
				case '(':
					numBrackets--;
					break;
			}
		}
		
		// String argString = funDef.substring(startArgs +1, endArgs);
		// List<Parameter> args = parseParameters(argString);
		
		String rest = localFunDef.substring(0, startArgs).trim();
		String name = rest;
		if (rest.length() < 10000) {
			name = rest.replaceAll(".*,", "");
		}
		
		// String type = "";
		
		int nameBegin = rest.length() - 1;
		
		while (nameBegin >= 0) {
			if (Character.isWhitespace(rest.charAt(nameBegin))) {
				break;
			}
			nameBegin--;
		}
		
		if (nameBegin >= 0) {
			name = rest.substring(nameBegin);
			rest = localFunDef.substring(0, nameBegin).trim();
			/*
			 * type = rest; int typeBegin = rest.lastIndexOf(' '); if(typeBegin >= 0){ type = rest.substring(typeBegin);
			 * }
			 */
		}
		
		final int classIndex = name.indexOf("::");
		if (classIndex >= 0) {
			localClassName += "." + name.substring(0, classIndex);
			name = name.substring(classIndex + 2).trim();
		}
		
		if (name.trim().matches(TopLevel.CONTROL_STRUCTURE)) {
			return null;
		}
		final Body body = this.bodyParser.getBody();
		final int end = body.getEndLine();
		
		return new Function(body, name.trim(), path, begin, end, this.constraintKeeper);
	}
	
	/**
	 * This method takes a string and checks if this string contains a class or function declaration, if it does, then
	 * the class or function is parsed. In the case of a function, an object of the class structure.Function is created
	 * and stored in the FunctionCollector
	 * 
	 * @param curr
	 *            the curr
	 * @param className
	 *            , the name of the class that is currently being parsed (an empty string if there is no class)
	 */
	private void parseTopLevel(final String curr,
	                           final String className) {
		String localClassName = className;
		// System.out.println(curr);
		if (localClassName == null) {
			localClassName = "";
		}
		
		if ((curr.length() == 0) || (curr.charAt(curr.length() - 1) != '{')) {
			return;
		}
		if (curr.matches(TopLevel.CLASS_PATTERN)) {
			// parse a class
			parseClass(curr, localClassName);
		} else if (curr.matches(TopLevel.FUNCTION_PATTERN)) {
			// parse a function
			final Function fun = parseFunction(curr, localClassName);
			if (fun != null) {
				// if a function was parsed, add it to the FunctionCollector
				this.functionCollector.addFunction(fun);
			}
			
		}
		
	}
	
	/**
	 * Sets the parsers.
	 * 
	 * @param input
	 *            the input
	 * @param bp
	 *            the bp
	 * @param fc
	 *            the fc
	 * @param constraintKeeper
	 *            the constraint keeper
	 */
	public void setParsers(final InputReader input,
	                       final BodyParser bp,
	                       final FunctionCollector fc,
	                       final ConstraintKeeper constraintKeeper) {
		this.input = input;
		this.bodyParser = bp;
		this.functionCollector = fc;
		this.constraintKeeper = constraintKeeper;
	}
	
}

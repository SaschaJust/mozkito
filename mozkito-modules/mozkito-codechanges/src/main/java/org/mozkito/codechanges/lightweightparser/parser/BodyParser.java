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

import java.util.ArrayList;
import java.util.List;

import org.mozkito.codechanges.lightweightparser.expression.EmptyExpression;
import org.mozkito.codechanges.lightweightparser.expression.Expression;
import org.mozkito.codechanges.lightweightparser.expression.Identifier;
import org.mozkito.codechanges.lightweightparser.expression.MultExpression;
import org.mozkito.codechanges.lightweightparser.statement.Block;
import org.mozkito.codechanges.lightweightparser.statement.Statement;
import org.mozkito.codechanges.lightweightparser.structure.Body;
import org.mozkito.codechanges.lightweightparser.structure.Function;
import org.mozkito.codechanges.lightweightparser.structure.FunctionCollector;

// TODO: Auto-generated Javadoc
/**
 * The Class BodyExtracter.
 */
public class BodyParser {
	
	/** The Constant DOWHILE_PATTERN. */
	private static final String    DOWHILE_PATTERN    = "(.*\\s)?do[\\{\\s].*";
	
	/** The if pattern. */
	public static String           IF_PATTERN         = "[^\\(]*if\\s*\\(.*\\).*";
	
	/** The for pattern. */
	public static String           FOR_PATTERN        = "[^\\(]*for(each)?\\s*\\(.*";
	
	/** The while pattern. */
	public static String           WHILE_PATTERN      = "[^\\(]*while\\s*\\(.*";
	
	/** The assignment pattern. */
	public static String           ASSIGNMENT_PATTERN = "\\s*[^=]+=[^=].*";
	
	/** The switch pattern. */
	public static String           SWITCH_PATTERN     = "(.*\\s+)?switch\\s*\\(.*\\)\\s*\\{";
	
	/** The operator. */
	public static String           OPERATOR           = "[*/^%><+=!&|]";
	
	/** The id. */
	public static String           ID                 = "[^//\\\\]*";
	
	/** The catch pattern. */
	private static String          CATCH_PATTERN      = "\\s*((catch)|(synchronized))\\s*\\(.*\\).*\\{";
	
	/** The not array. */
	private static String          NOT_ARRAY          = "[^\\[\\]]*";
	
	/** The new. */
	public static String           NEW                = ".*(=|\\s)new\\s+.*\\(\\.*\\)\\s*\\{";
	
	/** The input. */
	private InputReader            input;
	
	/** The top level. */
	private TopLevel               topLevel;
	
	/** The cs parser. */
	private ControlStructureParser csParser;
	
	/** The obj oriented. */
	private boolean                objOriented;
	
	/** The function collector. */
	private FunctionCollector      functionCollector;
	
	/**
	 * Parses a body. (for example, the body of a function, or the body of a loop etc.) The method should be called once
	 * an opening bracket ({) has already been parsed (to identify the beginning of a body) and will continue parsing
	 * input from the InputReader until the matching closing bracket (}) is reached
	 * 
	 * @return the parsed body
	 */
	public Body getBody() {
		final Body body = new Body();
		
		String curr; // stores the current line
		
		while (this.input.hasNext()) {
			curr = this.input.next().trim();
			if (curr.length() == 0) {
				continue;
			}
			
			final char last = curr.charAt(curr.length() - 1);
			if (last == '}') {
				break; // the matching closing bracket is found, stop parsing the body
			}
			
			try {
				final Statement s = parseStatement(curr); // parse the statement in the current line
				if ((s != null) && !(s instanceof EmptyExpression)) {
					// if the parsed statement is not empty, add it to the body
					body.addStatement(s);
				}
			} catch (final Exception e) {
				// continue
			}
		}
		
		body.setEndLine(this.input.getLineNumber());
		return body;
	}
	
	/**
	 * Takes the next token from the InputReader and tries to parse it as a statement.
	 * 
	 * @return the parsed statement, null if the statement was a function or class signature
	 */
	public Statement getStatement() {
		
		final String curr = this.input.next();
		return parseStatement(curr);
	}
	
	/**
	 * Parses the assignment.
	 * 
	 * @param assig
	 *            the assig
	 * 
	 * @return the statement
	 */
	private Expression parseAssignment(final String assig) {
		
		int prev = 0;
		int i = assig.indexOf('=');
		final List<String> ids = new ArrayList<String>();
		
		while (i > 0) {
			if ((i > (assig.length() - 2)) || (i == 0)) {
				break;
			}
			
			final char c = assig.charAt(i + 1);
			final char d = assig.charAt(i - 1);
			if ((c == '=') || (d == '=') || (d == '!') || (d == '<') || (d == '>')) {
				break;
			}
			
			final String s = assig.substring(prev, i).trim();
			final String end = "" + s.charAt(s.length() - 1);
			String str = s;
			
			if (!str.matches(BodyParser.ID)) {
				break;
			}
			
			if (!end.matches(BodyParser.OPERATOR)) {
				int j = -1;
				int z;
				for (z = s.length() - 1; z > 0; z--) {
					if (Character.isWhitespace(s.charAt(z))) {
						j = z;
						break;
					}
				}
				if (j > 0) {
					;
				}
				str = s.substring(j + 1);
				
				str = str.replaceAll("->", ".");
				if (str.matches(BodyParser.NOT_ARRAY)) {
					ids.add(str);
				}
			}
			prev = i + 1;
			i = assig.indexOf('=', prev + 1);
			
		}
		
		final String expression = assig.substring(prev);
		
		final ExpressionParser p = new ExpressionParser(expression, this.objOriented);
		final Expression exp = p.parseExp();
		
		if (ids.isEmpty()) {
			return exp;
		}
		if ((exp instanceof EmptyExpression) || (exp instanceof Identifier)) {
			return EmptyExpression.getEmptyExp();
		}
		
		exp.setAssigIDs(ids);
		return exp;
		
	}
	
	/**
	 * Parses the assigs.
	 * 
	 * @param assigs
	 *            the assigs
	 * 
	 * @return the list< statement>
	 */
	public List<Statement> parseAssigs(final String assigs) {
		final List<String> strings = separateAssignments(assigs);
		final List<Statement> statements = new ArrayList<Statement>();
		for (final String s : strings) {
			final Statement statement = parseAssignment(s);
			if (!(statement instanceof EmptyExpression)) {
				statements.add(statement);
			}
		}
		return statements;
	}
	
	/**
	 * Parses the assigs exp.
	 * 
	 * @param assigs
	 *            the assigs
	 * @return the expression
	 */
	public Expression parseAssigsExp(final String assigs) {
		final List<String> strings = separateAssignments(assigs);
		final List<Expression> expressions = new ArrayList<Expression>();
		for (final String s : strings) {
			final Expression exp = parseAssignment(s);
			if (!(exp.useless())) {
				expressions.add(exp);
			}
		}
		if (expressions.size() == 1) {
			return expressions.get(0);
		}
		return new MultExpression(expressions);
	}
	
	/**
	 * Receives a string and tries to parse it as a statement. If the statement is a function signature, then the
	 * function is parsed and stored in the FunctionCollector. If the statement is the beginning of a loop, the whole
	 * loop is parsed and returned, if it is the beginning of an if(-else-if) statement then the whole block of if and
	 * else statements is parsed and returned.
	 * 
	 * 
	 * @param curr
	 *            , the statement to be parsed
	 * 
	 * @return the parsed statement, null if the statement was a function or class signature
	 */
	private Statement parseStatement(final String curr) {
		// System.out.println(curr);
		if ((curr.length() == 0) || (curr.length() > 10000)) {
			return null;
		}
		final char last = curr.charAt(curr.length() - 1);
		
		if (curr.matches(BodyParser.IF_PATTERN)) {
			final Statement s = this.csParser.parseIf(curr);
			return s;
			
		} else if (curr.matches(BodyParser.FOR_PATTERN)) {
			final Statement s = this.csParser.parseFor(curr);
			return s;
			
		} else if (curr.matches(BodyParser.WHILE_PATTERN)) {
			final Statement s = this.csParser.parseWhile(curr);
			return s;
			
		} else if (curr.matches(BodyParser.DOWHILE_PATTERN)) {
			final Statement s = this.csParser.parseDoWhile(curr);
			return s;
			
		} else if (curr.matches(BodyParser.SWITCH_PATTERN)) {
			final Statement s = this.csParser.parseSwitch(curr);
			return s;
			
		} else if (curr.matches(TopLevel.FUNCTION_PATTERN) && !curr.matches(BodyParser.CATCH_PATTERN)) {
			int count = 0;
			for (int i = 0; i < curr.length(); i++) {
				final char c = curr.charAt(i);
				if (c == '(') {
					count++;
				} else if (c == ')') {
					count--;
				}
			}
			if ((count > 0) || curr.matches(BodyParser.NEW)) {
				getBody();
				
				this.input.putBack(curr.substring(0, curr.length() - 1));
				return null;
				// return new Block(EmptyExpression.getEmptyExp(),b);
			} else if (count == 0) {
				final Function f = this.topLevel.parseFunction(curr, "");
				if (f != null) {
					this.functionCollector.addFunction(f);
				}
			}
			
		} else if (curr.matches(TopLevel.CLASS_PATTERN)) {
			this.topLevel.parseClass(curr, "");
			
		} else if (last == '{') {
			int count = 0;
			for (int i = 0; i < curr.length(); i++) {
				final char c = curr.charAt(i);
				if (c == '(') {
					count++;
				} else if (c == ')') {
					count--;
				}
			}
			final Body b = getBody();
			if (count > 0) {
				
				this.input.putBack(curr.substring(0, curr.length() - 1));
				return null;
			}
			return new Block(EmptyExpression.getEmptyExp(), b);
		} else if (curr.matches(BodyParser.ASSIGNMENT_PATTERN)) {
			// Statement s = BodyParser.parseAssignment(curr);
			final Statement s = parseAssigsExp(curr);
			return s;
		} else {
			final Statement s = (new ExpressionParser(curr, this.objOriented).parseExp());
			return s;
			
		}
		return null;
	}
	
	/**
	 * Separate assignments.
	 * 
	 * @param assig
	 *            the assig
	 * 
	 * @return the list< string>
	 */
	private List<String> separateAssignments(final String assig) {
		final List<String> strings = new ArrayList<String>();
		String curr = "";
		int i = 0;
		int brackets = 0;
		while (i < assig.length()) {
			final char c = assig.charAt(i);
			if ((c == ',') && (brackets == 0)) {
				strings.add(curr);
				curr = "";
				i++;
				continue;
			} else if (c == '(') {
				brackets++;
			} else if (c == ')') {
				brackets--;
			}
			curr += c;
			i++;
			
		}
		strings.add(curr);
		return strings;
	}
	
	/**
	 * Sets the parsers.
	 * 
	 * @param input
	 *            the input
	 * @param tl
	 *            the tl
	 * @param csp
	 *            the csp
	 * @param fc
	 *            the fc
	 * @param objectOriented
	 *            the object oriented
	 */
	public void setParsers(final InputReader input,
	                       final TopLevel tl,
	                       final ControlStructureParser csp,
	                       final FunctionCollector fc,
	                       final boolean objectOriented) {
		this.input = input;
		this.topLevel = tl;
		this.csParser = csp;
		this.objOriented = objectOriented;
		this.functionCollector = fc;
		
	}
	
}

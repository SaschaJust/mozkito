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
import java.util.LinkedList;
import java.util.List;

import org.mozkito.codechanges.lightweightparser.expression.EmptyExpression;
import org.mozkito.codechanges.lightweightparser.expression.Expression;
import org.mozkito.codechanges.lightweightparser.expression.Identifier;
import org.mozkito.codechanges.lightweightparser.expression.Return;
import org.mozkito.codechanges.lightweightparser.statement.Block;
import org.mozkito.codechanges.lightweightparser.statement.Branch;
import org.mozkito.codechanges.lightweightparser.statement.Loop;
import org.mozkito.codechanges.lightweightparser.statement.Statement;
import org.mozkito.codechanges.lightweightparser.structure.Body;

/**
 * The Class ControlStructureParser.
 * 
 * this class is responsible for parsing control structures and creating the corresponding abstract representations
 */
public class ControlStructureParser {
	
	/** The else pattern. */
	public static String ELSE_PATTERN    = "\\s*else.*";
	
	/** The elseif pattern. */
	public static String ELSEIF_PATTERN  = "\\s*else\\s*if.*";
	
	/** The foreach pattern. */
	public static String FOREACH_PATTERN = "\\((.*[^:])?:([^:].*)?\\)\\s*[;\\{]";
	
	/** The forin pattern. */
	public static String FORIN_PATTERN   = "\\(.* in .*\\)\\s*[;\\{]";
	
	/** The foras pattern. */
	public static String FORAS_PATTERN   = "\\(.* as .*\\)\\s*[;\\{]";
	
	/** The input. */
	private InputReader  input;
	
	/** The body parser. */
	private BodyParser   bodyParser;
	
	/** The obj oriented. */
	private boolean      objOriented;
	
	/**
	 * Takes a string and returns the position of the closing bracket that corresponds to the first opening bracket
	 * found in the string (ie. finds the end of a condition that is surrounded by round brackets)
	 * 
	 * @param condition
	 *            , the string
	 * 
	 * @return the index of the closing bracket that corresponds to the first opening bracket
	 */
	private int findCondEnd(final String condition) {
		
		int i = 0;
		int brackets = 0;
		char c;
		boolean found = false;
		while ((i < condition.length()) && !found) {
			c = condition.charAt(i);
			switch (c) {
				case '(':
					brackets++;
					break;
				case ')':
					brackets--;
					if (brackets == 0) {
						found = true;
					}
			}
			i++;
		}
		return i;
	}
	
	/**
	 * takes a string containing either an opening bracket ({), indicating the start of a block, or an instruction
	 * (...;) indicating this is the only instruction of a block and parses the block
	 * 
	 * @param command
	 *            the start of the block (containing either '{' or "...;")
	 * 
	 * @return the body
	 */
	private Body getBody(final String command) {
		Body body = new Body();
		if (command.length() == 1) {
			// the block is surrounded by brackets, or is empty
			switch (command.charAt(command.length() - 1)) {
				case ';': // block is empty
					break;
				case '{':
					body = this.bodyParser.getBody();
					break;
			}
		} else {
			// block contains exactly one instruction
			
			this.input.putBack(command);
			final Statement state = this.bodyParser.getStatement();
			if (state != null) {
				body.addStatement(state);
			}
		}
		
		return body;
	}
	
	/**
	 * takes a string containing the condition of a control structure and returns the body of this control structure.
	 * 
	 * @param condition
	 *            the condition
	 * @return the body
	 */
	private Body getBody_filterCondition(final String condition) {
		
		final int i = findCondEnd(condition);
		final String command = condition.substring(i).trim();
		final Body b = getBody(command);
		return b;
	}
	
	/**
	 * Parses a do-while loop.
	 * 
	 * @param s
	 *            a string containing the "do" keyword
	 * @return the statement
	 */
	public Statement parseDoWhile(final String s) {
		
		Body body = null;
		
		final int doIndex = s.indexOf("do") + 2;
		final String command = s.substring(doIndex, s.length()).trim();
		
		body = getBody(command);
		
		String condition = this.input.next().trim();
		if (!condition.matches(BodyParser.WHILE_PATTERN) || (condition.charAt(condition.length() - 1) != ';')) {
			return null;
		}
		
		final int whileIndex = s.indexOf("while");
		condition = condition.substring(whileIndex + 5);
		condition = condition.substring(condition.indexOf('('));
		condition = condition.substring(1, findCondEnd(condition) - 1);
		
		Expression exp = (new ExpressionParser(condition, this.objOriented)).parseExp();
		
		if (exp.useless()) {
			exp = EmptyExpression.getEmptyExp();
		}
		
		return new Loop(body.getStatements(), exp, body);
	}
	
	/**
	 * Parses an else statement.
	 * 
	 * @param s
	 *            , a string containing the "else" keyword and condition if it is an else if statement
	 * @return a block representing the body of the else
	 */
	private Block parseElse(final String s) {
		
		Body body = null;
		int i;
		String condition = s;
		String command;
		Expression exp = null;
		
		if (s.matches(ControlStructureParser.ELSEIF_PATTERN)) {
			final int ifIndex = s.indexOf("if");
			condition = s.substring(ifIndex + 2);
			i = findCondEnd(condition);
			command = condition.substring(i, condition.length()).trim();
			condition = condition.substring(condition.indexOf('('));
			final String str = condition.substring(1, findCondEnd(condition) - 1);
			exp = (new ExpressionParser(str, this.objOriented)).parseExp();
		} else {
			i = s.indexOf("else") + 4;
			command = s.substring(i, s.length()).trim();
		}
		
		body = getBody(command);
		
		if (exp == null) {
			exp = EmptyExpression.getEmptyExp();
		}
		
		return new Block(exp, body);
	}
	
	/**
	 * Parses a For-loop.
	 * 
	 * @param s
	 *            string containing the "for" keyword and start of the condition
	 * @return a loop statement representing the parsed for loop
	 */
	public Statement parseFor(final String s) {
		
		String declaration;
		final int forIndex = s.indexOf("for");
		final int foreachIndex = s.indexOf("foreach");
		
		if (foreachIndex >= 0) {
			declaration = s.substring(foreachIndex + 7).trim();
		} else {
			declaration = s.substring(forIndex + 3).trim();
		}
		String condition = "";
		String incrementer;
		Expression cond;
		Expression incr;
		String end;
		
		if (declaration.matches(ControlStructureParser.FOREACH_PATTERN)) {
			return parseForEach(declaration, ":");
		} else if (declaration.matches(ControlStructureParser.FORIN_PATTERN)) {
			return parseForEach(declaration, " in ");
		} else if (declaration.matches(ControlStructureParser.FORAS_PATTERN)) {
			return parseForEach(declaration, " as ");
		} else {
			final ExpressionParser p = new ExpressionParser("", this.objOriented);
			
			if (!this.input.hasNext()) {
				return null;
			}
			condition = this.input.next().trim();
			
			if (!this.input.hasNext()) {
				return null;
			}
			incrementer = " (" + this.input.next().trim();
			
			end = incrementer;
			
			final int condEnd = findCondEnd(incrementer);
			incrementer = incrementer.substring(2, condEnd);
			
			p.reset(condition);
			cond = p.parseExp();
			
			p.reset(incrementer);
			incr = p.parseExp();
		}
		
		declaration = declaration.substring(1);
		final List<Statement> assig = this.bodyParser.parseAssigs(declaration);
		final Body body = getBody_filterCondition(end);
		
		body.addStatement(incr);
		
		if (cond.useless()) {
			cond = EmptyExpression.getEmptyExp();
		}
		
		final List<Statement> once = new ArrayList<Statement>();
		once.addAll(assig);
		return new Loop(once, cond, body);
	}
	
	/**
	 * Parses a foreach loop.
	 * 
	 * @param declaration
	 *            , the declaration and condition of the loop
	 * @param p
	 *            , the type of foreach loop (:, in, as)
	 * 
	 * @return the statement
	 */
	private Statement parseForEach(final String declaration,
	                               final String p) {
		
		final String end = declaration;
		final int j = declaration.indexOf(p);
		final String localDeclaration = declaration.substring(declaration.indexOf('(') + 1, j);
		
		final int e = findCondEnd(end.substring(end.indexOf('('))) - 1;
		
		String set = "";
		if (e > (j + p.length())) {
			set = end.substring(j + p.length(), e);
		}
		
		if (p.equals(" as ")) {
			set = localDeclaration;
		}
		
		final Expression exp = (new ExpressionParser(set, this.objOriented)).parseExp();
		final Body body = getBody_filterCondition(end);
		
		final List<Statement> once = new ArrayList<Statement>();
		
		if (!exp.useless()) {
			once.add(exp);
		}
		
		return new Loop(once, EmptyExpression.getEmptyExp(), body);
		
	}
	
	/**
	 * Parses an if-statement.
	 * 
	 * @param s
	 *            a string containing the "if" keyword and the condition
	 * @return the statement
	 */
	public Statement parseIf(final String s) {
		
		final ExpressionParser p = new ExpressionParser("", this.objOriented);
		final int ifIndex = s.indexOf("if");
		String condition = s.substring(ifIndex + 2);
		final Body body = getBody_filterCondition(condition);
		
		condition = condition.substring(condition.indexOf('('));
		condition = condition.substring(1, findCondEnd(condition) - 1);
		
		p.reset(condition);
		final Expression exp = p.parseExp();
		
		final LinkedList<Block> list = new LinkedList<Block>();
		list.add(new Block(exp, body));
		
		while (this.input.hasNext()) {
			final String nextLine = this.input.next().trim();
			if (nextLine.matches(ControlStructureParser.ELSE_PATTERN)) {
				list.add(parseElse(nextLine));
			} else {
				this.input.putBack(nextLine);
				break;
			}
		}
		
		return new Branch(list);
	}
	
	/**
	 * Parses a switch-case statement.
	 * 
	 * @param curr
	 *            a string containing the "switch" keyword
	 * @return the statement
	 */
	public Statement parseSwitch(final String curr) {
		
		final int switchIndex = curr.indexOf("switch");
		String condition = curr.substring(switchIndex + 6);
		
		condition = condition.substring(condition.indexOf('('));
		condition = condition.substring(1, findCondEnd(condition) - 1);
		
		final Expression exp = (new ExpressionParser(condition, this.objOriented)).parseExp();
		
		final Body body = this.bodyParser.getBody();
		final LinkedList<Block> list = new LinkedList<Block>();
		
		final List<Statement> statements = body.getStatements();
		Body currBody = new Body();
		list.add(new Block(exp, new Body()));
		
		for (final Statement s : statements) {
			
			if ((s instanceof Identifier) && ((Identifier) s).isBreak()) {
				if (!currBody.isEmpty()) {
					list.add(new Block(EmptyExpression.getEmptyExp(), currBody));
					currBody = new Body();
				}
			} else if (s instanceof Return) {
				currBody.addStatement(s);
				list.add(new Block(EmptyExpression.getEmptyExp(), currBody));
				currBody = new Body();
			} else {
				if (s instanceof Block) {
					final Block b = (Block) s;
					if (!b.getCondition().equals(EmptyExpression.getEmptyExp())) {
						for (final Statement st : b.getBody().getStatements()) {
							if (!((st instanceof Identifier) && ((Identifier) st).isBreak())) {
								((Identifier) st).setName("");
							}
						}
					}
				}
				currBody.addStatement(s);
			}
		}
		
		if (!currBody.isEmpty()) {
			list.add(new Block(EmptyExpression.getEmptyExp(), currBody));
		}
		
		return new Branch(list);
	}
	
	/**
	 * Parses a while loop.
	 * 
	 * @param s
	 *            a string containing the while keyword and condition of the loop
	 * @return loop statement
	 */
	public Statement parseWhile(final String s) {
		final ExpressionParser p = new ExpressionParser("", this.objOriented);
		final int whileIndex = s.indexOf("while");
		String condition = s.substring(whileIndex + 5);
		Body body = null;
		
		body = getBody_filterCondition(condition);
		
		condition = condition.substring(condition.indexOf('('));
		condition = condition.substring(1, findCondEnd(condition) - 1);
		p.reset(condition);
		Expression exp = p.parseExp();
		
		if (exp.useless()) {
			exp = EmptyExpression.getEmptyExp();
		}
		
		return new Loop(new ArrayList<Statement>(), exp, body);
	}
	
	/**
	 * Sets the parsers.
	 * 
	 * @param input
	 *            the input
	 * @param bp
	 *            the bp
	 * @param objectOriented
	 *            the object oriented
	 */
	public void setParsers(final InputReader input,
	                       final BodyParser bp,
	                       final boolean objectOriented) {
		this.input = input;
		this.bodyParser = bp;
		this.objOriented = objectOriented;
		
	}
	
}

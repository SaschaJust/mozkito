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

import org.mozkito.codechanges.lightweightparser.expression.CondExpression;
import org.mozkito.codechanges.lightweightparser.expression.EmptyExpression;
import org.mozkito.codechanges.lightweightparser.expression.Expression;
import org.mozkito.codechanges.lightweightparser.expression.FunctionCall;
import org.mozkito.codechanges.lightweightparser.expression.Identifier;
import org.mozkito.codechanges.lightweightparser.expression.MultExpression;
import org.mozkito.codechanges.lightweightparser.expression.MultFnCall;
import org.mozkito.codechanges.lightweightparser.expression.Return;

/**
 * The Class ExpressionParser. This class takes a string, parses it and tries to identify expressions which are then
 * stored in an abstract form
 */
public class ExpressionParser {
	
	/** The assume this. */
	public boolean       assumeThis;
	
	/** The function name. */
	public static String FUNCTION_NAME = "\\s*(new\\s+)?\\**[\\p{Sc}\\w\\:\\[\\]]+(\\s*\\.\\s*\\**[\\p{Sc}\\w\\:]+)*\\s*";
	
	/** The continue call. */
	public static String CONTINUE_CALL = "\\s*\\.\\s*[\\p{Sc}\\w\\*\\.]+\\(.*\\).*";
	
	/** The ignore word. */
	public static String IGNORE_WORD   = "\\s*throw \\s*";
	
	/** The return. */
	public static String RETURN        = "(.*\\s+)?return \\s*";
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		
		final ExpressionParser ep = new ExpressionParser("(ASTNode) elem.getOriginalValue();", true);
		System.out.println(ep.parseExp());
	}
	
	/** The return no space. */
	private final String RETURN_NO_SPACE = "(.*\\s+)?return\\s*";
	
	/** The type. */
	public static String TYPE            = "<\\s*[\\w\\.\\s*]+>.*";
	
	/** The identifier. */
	public static String IDENTIFIER      = ".*\\**[\\p{Alpha}\\p{Sc}_][\\p{Sc}\\w]*\\s*";
	
	/** The cast. */
	private final String CAST            = "\\s*\\([\\w\\*\\.]+\\)\\s*[\\p{Sc}\\w\\*\\.]+\\s*;?";
	
	/** The id. */
	private static long  id              = 0;
	
	/** The expression to be parsed. */
	String               exp;
	
	/** The position of the char, in exp, being parsed currently. */
	int                  index;
	
	/** The exp list. */
	List<Expression>     expList;                                                                // a list of the
	                                                                                              // subexpressions that
	                                                                                              // were already parsed
	                                                                                              // and identified
	                                                                                              
	/** The num exp. */
	int                  numExp;                                                                 // the number of
	                                                                                              // subexpressions that
	                                                                                              // have been parsed
	                                                                                              // and identified
	                                                                                              
	/** The s. */
	String               s;                                                                      // a helper string
	                                                                                              // that contains the
	                                                                                              // subexpression
	                                                                                              // currently being
	                                                                                              // parsed
	                                                                                              
	/**
	 * Instantiates a new expression parser.
	 * 
	 * @param exp
	 *            the expression to be parsed
	 * @param objectOriented
	 *            the object oriented
	 */
	public ExpressionParser(final String exp, final boolean objectOriented) {
		this.assumeThis = objectOriented;
		this.exp = exp.replace("->", ".");
		this.index = 0;
		this.expList = new ArrayList<Expression>();
		this.numExp = 0;
		this.s = "";
		if (exp.matches(this.CAST)) {
			this.exp = exp.substring(exp.indexOf(')') + 1);
		}
	}
	
	/**
	 * Parses a function call and all function calls using the return value to call another function (ex. foo().blub();,
	 * both the call to foo and call to blub will be parsed )
	 * 
	 * @param name
	 *            , caller and target
	 * @param prevCalls
	 *            , the list of function calls already parsed
	 */
	private void continueCall(final String name,
	                          final List<FunctionCall> prevCalls) {
		String localName = name;
		this.index++;
		// parse the arguments of the function call
		final List<Expression> args = parseArguments();
		
		// determine the name and target of the function:
		final int lastDot = localName.lastIndexOf('.');
		String target = "";
		
		if (localName.trim().startsWith("new ")) {
			// name = name.replaceFirst("new ", "").trim();
			if (lastDot >= 0) {
				localName = localName.substring(lastDot + 1);
				localName = "new " + localName;
			}
			
		} else if (lastDot >= 0) {
			// there is a dot separating target and name
			target = localName.substring(0, lastDot);
			if ((target.indexOf('[') > 0) || (target.indexOf(']') > 0)) {
				target = "";
			}
			localName = localName.substring(lastDot + 1);
		} else if (localName.contains("::")) {
			final int scope = localName.lastIndexOf("::");
			localName = localName.substring(scope + 2);
		} else {
			// if the :: operator is not present in the name then the target is assumed to be "this"
			if (this.assumeThis) {
				target = "this";
			}
		}
		
		localName = localName.trim();
		final FunctionCall fc = new FunctionCall(target, args, localName);
		prevCalls.add(fc); // add the function call to the list of already parsed calls
		
		if ((this.exp.length() > this.index) && this.exp.substring(this.index).matches(ExpressionParser.CONTINUE_CALL)) {
			// the return value of the just parsed function call is being used as the caller
			// to the next call, so continue parsing
			final String nextTarget = "#" + ExpressionParser.id;
			ExpressionParser.id++;
			fc.addAssigId(nextTarget);
			final int bracket = this.exp.substring(this.index).indexOf('('); // the start of the next calls arguments
			final String newName = this.exp.substring(this.index, bracket + this.index).trim(); // the name of the next
			                                                                                    // call
			this.index += bracket;
			continueCall(nextTarget + newName, prevCalls);
			
		}
		
	}
	
	/**
	 * Parses the arguments of a function call.
	 * 
	 * @return a list containing the arguments
	 */
	private List<Expression> parseArguments() {
		final List<Expression> args = new ArrayList<Expression>();
		final ExpressionParser expParser = new ExpressionParser("", this.assumeThis);
		char c;
		String arg = ""; // stores the current argument
		
		int brackets = 1; // keeps track of opening vs. closing brackets
		                  // is set to one because the opening arguments bracket has already been parsed
		
		// separate and parse the arguments:
		while (this.index < this.exp.length()) {
			
			c = this.exp.charAt(this.index);
			
			if (c == ')') {
				brackets--;
				if (brackets == 0) {
					// done parsing arguments
					break;
				}
			}
			
			switch (c) {
				case ',':
					if (brackets == 1) {
						// end of an argument, parse this argument
						expParser.reset(arg);
						args.add(expParser.parseExp());
						arg = ""; // reset current argument
					} else {
						arg += c;
					}
					break;
				case '(':
					brackets++;
					// TODO Sascha: I introduced this break here. Seems like brackets should not count towards
					// arguments. This might however be intended. In that case we should add <code>arg += c</code> here.
					break;
				default:
					arg += c;
					
			}
			this.index++;
		}
		
		// parse the last argument:
		if (arg.trim().length() != 0) {
			expParser.reset(arg);
			args.add(expParser.parseExp());
		}
		
		this.index++;
		
		return args;
	}
	
	/**
	 * Parses an expression surrounded by brackets.
	 */
	private void parseBrackets() {
		
		String s = ""; // stores the expression that is surrounded by brackets
		int brackets = 1;// keeps track of opening vs. closing brackets
		                 // is set to one because the opening bracket has already been parsed
		
		char c;
		this.index++;
		
		// extract the expression and store it in s
		while (this.index < this.exp.length()) {
			c = this.exp.charAt(this.index);
			if (c == ')') {
				brackets--;
				if (brackets == 0) {
					// found the closing bracket, done extracting expression
					break;
				}
			} else if (c == '(') {
				brackets++;
			}
			s += c;
			this.index++;
		}
		
		// parse the expression
		final Expression e = new ExpressionParser(s, this.assumeThis).parseExp();
		// check if the expression is the caller of a function (ex. (caller).function();)
		if ((this.index + 1) < this.exp.length()) {
			final String functionCall = this.exp.substring(this.index + 1);
			if (e instanceof Identifier) {
				if (functionCall.matches(ExpressionParser.CONTINUE_CALL)) {
					// expression is the target of a function call, add the expression string to the current global
					// expression
					// that is being parsed to allow it to be identified as the caller of a function by the continueCall
					// method
					this.s += ((Identifier) e).getName() + ".";
					this.index += functionCall.indexOf('.') + 1;
					return;
				}
			}
			
			if ((e instanceof FunctionCall) || (e instanceof MultFnCall)) {
				FunctionCall f;
				if (e instanceof MultFnCall) {
					f = ((MultFnCall) e).getLastCall();
				} else {
					f = (FunctionCall) e;
				}
				if (functionCall.matches(ExpressionParser.CONTINUE_CALL)) {
					final int dot = functionCall.indexOf('.');
					final int nameEnd = this.exp.indexOf('(', dot + this.index);
					final int nameStart = this.index + dot;
					
					this.expList.add(e);
					final String nextTarget = "#" + ExpressionParser.id;
					ExpressionParser.id++;
					f.addAssigId(nextTarget);
					this.index = nameEnd;
					this.index++;
					parseFunctionCall(nextTarget + this.exp.substring(nameStart, nameEnd));
					return;
				}
				
			}
		}
		
		// if the expression is not empty, add it to the global subexpression list
		if (!(e instanceof EmptyExpression)) {
			this.expList.add(e);
		}
		
	}
	
	/**
	 * Parses a conditional expression (..?..:..)
	 * 
	 */
	private void parseChoice() {// Expression cond) {
		Expression cond;
		if (this.expList.isEmpty()) {
			cond = EmptyExpression.getEmptyExp();
		} else if (this.expList.size() == 1) {
			cond = this.expList.get(0);
		} else {
			cond = new MultExpression(this.expList);
		}
		this.expList = new ArrayList<Expression>();
		this.numExp = 0;
		String s = ""; // the current expression (the exp either before or after the colon)
		String first = ""; // the first expression (the exp before the colon)
		int brackets = 0; // keeps count of the opening vs. closing brackets
		char c;
		this.index++;
		
		// the following loop extracts the expression before and after the colon
		while (this.index < this.exp.length()) {
			c = this.exp.charAt(this.index);
			
			if (c == ')') {
				brackets--;
				if (brackets < 0) {
					// the end of the second expression has been reached
					break;
				}
			} else if (c == '(') {
				brackets++;
			} else if (c == ':') {
				if (((this.index + 1) < this.exp.length()) && (this.exp.charAt(this.index + 1) == ':')) {
					// curr char is not a colon, but part of the :: operator
					s += "::";
					this.index++; // skip the next ':' since its already been added to s
				}
				if (((this.index + 1) < this.exp.length()) && (brackets == 0) && (first.length() == 0)) {
					// curr char is the separating colon
					first = s; // the first expression is stored
					s = ""; // current expression is reset
					this.index++;
					continue;
				}
			}
			
			s += c;
			this.index++;
		}
		
		final ExpressionParser p = new ExpressionParser(first, this.assumeThis);
		final Expression f = p.parseExp(); // parse the first expression (before the colon)
		p.reset(s);
		final Expression e = p.parseExp(); // parse the second expression (after the colon)
		
		final Expression exp = new CondExpression(cond, f, e);
		this.expList.add(exp);
		
	}
	
	/**
	 * Parses the string exp.
	 * 
	 * @return an Object of type expression.Expression representing the parsed expression
	 */
	public Expression parseExp() {
		
		this.s = ""; // the current subexpression
		
		while (this.index < this.exp.length()) {
			
			if (this.s.matches(ExpressionParser.IGNORE_WORD)) {
				this.s = ""; // reset s to delete the word to be ignored
			}
			if (this.s.matches(ExpressionParser.RETURN)) {
				// subexpression is a return statement
				final String rest = this.exp.substring(this.index); // rest = the expression string after the keyword
				                                                    // "return"
				final Expression e = new Return(new ExpressionParser(rest, this.assumeThis).parseExp());
				return e;
			}
			final char c = this.exp.charAt(this.index);
			
			switch (c) {
				case '(':
					if (this.s.matches(this.RETURN_NO_SPACE)) {
						final String rest = this.exp.substring(this.index); // rest = the expression string after the
						                                                    // keyword "return"
						final Expression e = new Return(new ExpressionParser(rest, this.assumeThis).parseExp());
						return e;
					}
					// the current subexpression could be a function call
					String p = this.s.trim();
					final int i = p.lastIndexOf(' ');
					if (i >= 0) {
						p = p.substring(i + 1);
					}
					if (this.s.matches(ExpressionParser.FUNCTION_NAME)) {
						// it is a function call
						// the string s contains the target and caller
						parseFunctionCall(this.s);
						this.s = "";
						this.numExp++;
					} else if (p.matches(ExpressionParser.FUNCTION_NAME)) {
						// it is a function call
						// s contains noise (more than just the caller and target)
						// p is the substring of s containing just caller and target
						parseFunctionCall(p);
						this.s = "";
						this.numExp++;
					} else {
						// not a function call, but just an expression in brackets
						this.s = ""; // reset the current subexpression
						parseBrackets(); // parse the expression in the brackets
					}
					break;
				case '+':
				case '%':
				case '>':
				case '|':
				case '=':
				case '/':
				case ',':
				case '-':
				case '^':
					// the current char is a binary operator, operators separate subexpressions
					// a new subexpression needs to be started
					this.s = ""; // clear the old expression
					this.numExp++;
					break;
				case '*':
					if ((this.index + 1) >= this.exp.length()) {
						break;
					}
					if ((!Character.isJavaIdentifierStart(this.exp.charAt(this.index + 1)) && (this.exp.charAt(this.index + 1) != '*'))
					        || ((this.s.length() >= 1) && Character.isJavaIdentifierPart(this.s.charAt(this.s.length() - 1)))) {
						// the next char is not the start of an identifier
						// => the '*' is likely a binary operator separating subexpressions
						this.s = ""; // clear the old expression
						// index++;
						this.numExp++;
						break;
					} else {
						// '*' is likely no a binary operator
						this.s += " "; // c; //add '*' to the current subexpression
					}
					break;
				case '&':
					if ((this.index + 1) >= this.exp.length()) {
						break;
					}
					if (!Character.isJavaIdentifierStart(this.exp.charAt(this.index + 1))
					        || ((this.s.length() >= 1) && Character.isJavaIdentifierPart(this.s.charAt(this.s.length() - 1)))) {
						// the next char is not the start of an identifier
						// => the '&' is likely a binary operator separating subexpressions
						if (this.exp.charAt(this.index + 1) == '&') {
							this.index++; // skip the following '&'
						}
						this.s = ""; // clear the old expression
						// index++;
						this.numExp++;
						break;
					} else {
						// '&' is likely not a binary operator
						this.s += " "; // c; //add '&' to the current subexpression
					}
					break;
				
				case '<':
					if (this.exp.substring(this.index).matches(ExpressionParser.TYPE)) {
						// the '<' is the opening bracket of a type specification
						// skip over the type specification:
						final int stop = this.exp.substring(this.index).indexOf('>');
						this.index += stop;
					} else {
						// the '<' is likely a binary operator
						this.s = "";
						this.numExp++;
					}
					/*
					 * case '!': if(index +1 < exp.length() && exp.charAt(index +1) != '='){ //'!' is not a part of a
					 * binary operator break; } else{ //!= is a binary operator s = ""; numExp++; break; }
					 */
					break;
				case '!':
					break;
				case ';': // end of expression
					break;
				case '?':
					// subexpression is a conditional expression (..?..:..)
					parseChoice();
					this.s = "";
					this.numExp++;
					break;
				case '.':
					if (!this.expList.isEmpty() && !(this.expList.get(this.expList.size() - 1) instanceof Identifier)) {
						this.numExp++;
					}
					// TODO again, I added a break here. please investigate.
					break;
				default:
					this.s += c;
					
			}
			
			this.index++;
			
		}
		
		// the complete expression has been parsed
		
		if (this.expList.isEmpty()) {
			// no expressions have been found
			if ((this.s.trim().length() > 0) && (this.s.trim().indexOf(' ') < 0)
			        && this.s.matches(ExpressionParser.IDENTIFIER) && (this.numExp == 0)) {
				// the last subexpression is an identifier, and this is the only subexpression
				this.expList.add(Identifier.makeIdentifier(this.s.trim()));
			} else {
				return EmptyExpression.getEmptyExp();
			}
		}
		
		// System.out.println(numExp);
		final List<Expression> temp = new ArrayList<Expression>(); // stores the relevant expressions
		int i;
		// filter out all useless expressions
		for (i = 0; i < this.expList.size(); i++) {
			if ((i == (this.expList.size() - 1)) && temp.isEmpty()) {
				// if all expressions have been useless, add the last one to temp
				temp.add(this.expList.get(i));
			} else if (!this.expList.get(i).useless()) {
				// if the expression is not useless, add it to temp
				temp.add(this.expList.get(i));
			}
		}
		
		/*
		 * the following two cases need to be distinguished in case this expression assigned to an identifier, in the
		 * first case, the identifier would be an object associated with the expression because there is only one
		 * subexpression (ie. i = foo();), in the second case this is not so (ie. i = foo() + number;)
		 */
		if ((temp.size() == 1) && ((this.numExp <= 1) || (temp.get(0) instanceof EmptyExpression))) {
			// if only one expression is relevant and there was only one subexpression then return this expression
			
			return temp.get(0);
		} else {
			return new MultExpression(temp);
		}
		
	}
	
	/**
	 * Parses a function call and all function calls in which the return value is the target of another function (ex.
	 * foo().blub();, both the call to foo and call to blub will be parsed )
	 * 
	 * @param name
	 *            , caller and target
	 */
	private void parseFunctionCall(final String name) {
		
		final List<FunctionCall> list = new ArrayList<FunctionCall>(); // the list in which the parsed calls are stored
		
		continueCall(name, list); // parse the current function call and any adjacent ones
		
		Expression e;
		if (list.size() > 1) {
			e = new MultFnCall(list);
		} else {
			e = list.get(0);
		}
		
		this.expList.add(e);
		this.index--;
		
	}
	
	/**
	 * Resets the expression parser to parse a new string.
	 * 
	 * @param exp
	 *            the expression to be parsed
	 */
	public void reset(final String exp) {
		this.exp = exp.replace("->", ".");
		this.index = 0;
		this.expList = new ArrayList<Expression>();
		this.numExp = 0;
		this.s = "";
		if (exp.matches(this.CAST)) {
			this.exp = exp.substring(exp.indexOf(')') + 1);
		}
	}
	
}

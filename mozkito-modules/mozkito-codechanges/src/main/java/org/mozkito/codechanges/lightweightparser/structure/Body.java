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
package org.mozkito.codechanges.lightweightparser.structure;

import java.util.LinkedList;

import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Wrapper;
import org.mozkito.codechanges.lightweightparser.statement.Statement;

// TODO: Auto-generated Javadoc
/**
 * The Class Body.
 */
public class Body {
	
	/** The statements. */
	LinkedList<Statement> statements;
	
	/** The end line. */
	int                   endLine = 0;
	
	/**
	 * Instantiates a new body.
	 */
	public Body() {
		
		this.statements = new LinkedList<Statement>();
	}
	
	/**
	 * Adds a statement to the end of the current body if it is not useless (defined by the useless() method).
	 * 
	 * @param s
	 *            the statement to be added
	 */
	public void addStatement(final Statement s) {
		
		if (!s.useless()) {
			this.statements.add(s);
		}
		
	}
	
	/**
	 * Adds a statement to the front of the current body if it is not useless.
	 * 
	 * @param s
	 *            the statement to be added
	 */
	public void addStatementFront(final Statement s) {
		if (!s.useless()) {
			this.statements.addFirst(s);
		}
	}
	
	/**
	 * Takes a function model and adds all the statements contained in the body to the model.
	 * 
	 * @param fM
	 *            the function model which the body is to be added to
	 * 
	 * @return Wrapper containing any break or continue nodes that were created by building the model for this body
	 */
	public Wrapper buildFunctionModel(final FunctionModel fM) {
		final Wrapper w = new Wrapper();
		for (final Statement s : this.statements) {
			w.add(s.buildFunctionModel(fM));
		}
		return w;
		
	}
	
	/**
	 * Gets the end line.
	 * 
	 * @return the lines
	 */
	public int getEndLine() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.endLine;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the statements.
	 * 
	 * @return the statements
	 */
	public LinkedList<Statement> getStatements() {
		return this.statements;
	}
	
	/**
	 * Checks if the body contains no statements.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		
		return this.statements.isEmpty();
	}
	
	/**
	 * Sets the end line.
	 * 
	 * @param endLine
	 *            the lines to set
	 */
	public void setEndLine(final int endLine) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.endLine = endLine;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "";
		for (final Statement s : this.statements) {
			str += s.toString();
		}
		str += "\n";
		return str;
	}
	
	/**
	 * Check if this body is useless. A body is deemed useless if all the statements it contains are useless
	 * 
	 * @return true, if useless
	 */
	public boolean useless() {
		boolean useless = true;
		for (final Statement s : this.statements) {
			useless &= s.useless();
		}
		return useless;
	}
	
}

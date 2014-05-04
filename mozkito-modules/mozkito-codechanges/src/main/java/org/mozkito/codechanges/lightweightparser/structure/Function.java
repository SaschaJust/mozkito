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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.mozkito.codechanges.lightweightparser.constraints.Constraint;
import org.mozkito.codechanges.lightweightparser.constraints.ConstraintKeeper;
import org.mozkito.codechanges.lightweightparser.functionModel.FunctionModel;
import org.mozkito.codechanges.lightweightparser.functionModel.Node;

/**
 * The Class Function.
 */
public class Function {
	
	/** The name of the function. */
	private final String           name;
	
	/** The body of the function. */
	private final Body             body;
	
	/** The location. */
	private final String           location;
	
	/** The id of the function. The function will not have an id until after sequential constraints have been mined */
	private int                    id;
	
	/** The function model for this function, does not exist until buildFunctionModel() has been called. */
	private FunctionModel          fM;
	
	/**
	 * The set of sequential constraints for this function, does not exist until buildFunctionModel() and
	 * makeConstraints() have been called.
	 */
	private final Set<Constraint>  cSet;
	
	/** The c k. */
	private final ConstraintKeeper cK;
	
	/** The begin. */
	private final int              begin;
	
	/** The end. */
	private final int              end;
	
	/**
	 * Instantiates a new function.
	 * 
	 * @param body
	 *            the body
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 * @param begin
	 *            the begin
	 * @param end
	 *            the end
	 * @param cK
	 *            the constraint keeper
	 */
	public Function(final Body body, final String name, final String path, final int begin, final int end,
	        final ConstraintKeeper cK) {
		super();
		this.body = body;
		this.name = name;
		this.cSet = new HashSet<Constraint>();
		this.location = path + ":" + begin;
		this.begin = begin;
		this.end = end;
		this.cK = cK;
		Node.count = 0;
		
	}
	
	/**
	 * Builds the function model.
	 */
	public void buildFunctionModel() {
		this.fM = new FunctionModel();
		this.body.buildFunctionModel(this.fM);
	}
	
	/**
	 * Writes the dot representation of the function model to a file called graph.dot
	 * 
	 * Should only be called after buildFunctionModel() has been called.
	 */
	public void dotToFile() {
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter("graph.dot"));
			out.write(this.fM.toDot());
			out.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the begin.
	 * 
	 * @return the begin
	 */
	public int getBegin() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.begin;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
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
	 * Gets the bri string.
	 * 
	 * @return the bri string
	 */
	public String getBriString() {
		String s = "o" + this.id + ": ";
		for (final Constraint c : this.cSet) {
			s += " " + c.getID();
		}
		return s + "\n";
	}
	
	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public int getEnd() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.end;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the source code location. (file path and line number)
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Gets the met string.
	 * 
	 * @return the met string
	 */
	public String getMetString() {
		return "o" + this.id + ": " + this.name + ":" + this.location + "\n";
	}
	
	/**
	 * Gets the function model.
	 * 
	 * Should only be called after the function model for this function has been built.
	 * 
	 * @return the function model
	 */
	public FunctionModel getModel() {
		return this.fM;
	}
	
	/**
	 * Gets the name of the function.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks if this function has at least one sequential constraints.
	 * 
	 * Should only be called once constraints have been mined.
	 * 
	 * @return true, if the function has at least one seq. constraint
	 */
	public boolean hasConstraints() {
		return !this.cSet.isEmpty();
	}
	
	/**
	 * Mines sequential constraints from the function model and stores them in cSet.
	 * 
	 * Should only be called after buildFunctionModel() has been called.
	 */
	public void makeConstraints() {
		final Set<Constraint> temp = this.fM.collectConstraints();
		for (final Constraint c : temp) {
			final Constraint constraint = this.cK.addConstraint(c);
			if (constraint != null) {
				this.cSet.add(constraint);
			}
		}
		
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(final int id) {
		this.id = id;
		
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
		String s = "Function: " + this.name + "\n";
		s += this.body.toString();
		return s;
		
	}
	
}

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
package org.mozkito.codechanges.lightweightparser.constraints;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Class ConstraintKeeper. This class keeps a map of all sequential constraints that have been created. It assigns
 * an id to every unique sequential constraint that is added.
 */
public class ConstraintKeeper {
	
	/** The map from constraint strings (e.g., foo()@0 bar()@0 ) to constraint objects. */
	private final HashMap<String, Constraint> map;
	
	/** The id that was given to the last new constraint. */
	private long                              currID;
	
	/**
	 * Instantiates a new constraint keeper.
	 */
	public ConstraintKeeper() {
		this.map = new HashMap<String, Constraint>();
		this.currID = 0;
	}
	
	/**
	 * Adds a constraint to the constraint keeper. If a constraint with the same constraint string already exists then
	 * the old constraint is returned. If no such constraint exists then an id is assigned to the constraint and it is
	 * added to the map.
	 * 
	 * If the object position of the second function in the constraint is "-1" then no null is returned.
	 * 
	 * @param c
	 *            the constraint to be added
	 * 
	 * @return a constraint with the same constraint string as the argument and an id, or null
	 */
	public Constraint addConstraint(final Constraint c) {
		
		if (c.second.endsWith("-1")) {
			return null;
		}
		
		final String key = c.both;
		if (!this.map.containsKey(key)) {
			this.currID++;
			c.setID(this.currID);
			this.map.put(key, c);
			return c;
		} else {
			return this.map.get(key);
		}
	}
	
	/**
	 * Gets the constraint object that corresponds to a specific constraint string.
	 * 
	 * @param first
	 *            the first function of the sequential constraint
	 * @param second
	 *            the second function of the constraint
	 * @return a constraint with the specified constraint string and an id, or null if no such constraint exists
	 */
	public Constraint getConstraint(final String first,
	                                final String second) {
		
		return this.map.get(first + second);
	}
	
	/**
	 * Gets the constraints.
	 * 
	 * @return the constraints
	 */
	public Collection<Constraint> getConstraints() {
		return this.map.values();
	}
	
	/**
	 * Write gra file.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public void writeGraFile(final String fileName) {
		final String localFileName = fileName + ".gra";
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(localFileName));
			final Collection<Constraint> values = this.map.values();
			final SortedSet<Constraint> sortedVal = new TreeSet<Constraint>(values);
			for (final Constraint c : sortedVal) {
				out.write(c.getGraString());
			}
			out.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}

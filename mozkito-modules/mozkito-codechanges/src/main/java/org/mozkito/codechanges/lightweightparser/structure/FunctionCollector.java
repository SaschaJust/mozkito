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
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FunctionCollector. A class which collects all the functions that are parsed.
 */
public class FunctionCollector {
	
	/** The functions. */
	private List<Function> functions;
	
	/**
	 * Instantiates a new function collector.
	 */
	public FunctionCollector() {
		this.functions = new ArrayList<Function>();
	}
	
	/**
	 * Adds a function.
	 * 
	 * @param f
	 *            the function
	 */
	public void addFunction(final Function f) {
		
		this.functions.add(f);
	}
	
	/**
	 * Builds the function model for each function in the function collector.
	 */
	public void buildFunctionModels() {
		for (final Function f : this.functions) {
			f.buildFunctionModel();
		}
		
	}
	
	/**
	 * Gets the functions.
	 * 
	 * @return the functions
	 */
	public List<Function> getFunctions() {
		
		return this.functions;
	}
	
	/**
	 * Mines sequential constraints from all functions in the function collector The set of functions that the function
	 * collector hold is filtered to contain only those that have at least one sequential constraint (each of these
	 * functions is also assigned an id).
	 */
	public void makeConstraints() {
		int id = 1;
		final List<Function> relevantFunctions = new ArrayList<Function>();
		for (final Function f : this.functions) {
			// System.out.println(f.getMetString());
			f.makeConstraints();
			if (f.hasConstraints()) {
				relevantFunctions.add(f);
				f.setId(id);
				id++;
			}
		}
		this.functions = relevantFunctions;
	}
	
	/**
	 * Write bri file.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public void writeBriFile(final String fileName) {
		final String localFileName = fileName + ".bri";
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(localFileName));
			for (final Function f : this.functions) {
				out.write(f.getBriString());
			}
			out.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Write met file.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public void writeMetFile(final String fileName) {
		final String localFileName = fileName + ".met";
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(localFileName));
			for (final Function f : this.functions) {
				out.write(f.getMetString());
			}
			out.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}

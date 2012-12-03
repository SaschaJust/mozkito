/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mozkito.callgraph.model;

import java.io.Serializable;

/**
 * The Class MinerCallGraphEdge.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class CallGraphEdge implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1917607287240246932L;
	
	/** The occurrence. */
	private int               occurrence       = 0;
	
	/**
	 * Instantiates a new miner call graph edge.
	 * 
	 */
	public CallGraphEdge() {
		++this.occurrence;
	}
	
	/**
	 * Increases occurrence by one.
	 * 
	 * @return the modified occurrence value
	 */
	public int addOccurrence() {
		return ++this.occurrence;
	}
	
	/**
	 * Increases the occurrence by value.
	 * 
	 * @param value
	 *            the value
	 * @return the modified occurrence value
	 */
	public int addOccurrence(final int value) {
		this.occurrence += value;
		return this.occurrence;
	}
	
	/**
	 * Gets the occurrence.
	 * 
	 * @return the occurrence
	 */
	public int getOccurrence() {
		return this.occurrence;
	}
	
	/**
	 * Gets the weight (1 / occurrence).
	 * 
	 * @return the weight
	 */
	public double getWeight() {
		return (1d / this.occurrence);
	}
	
}

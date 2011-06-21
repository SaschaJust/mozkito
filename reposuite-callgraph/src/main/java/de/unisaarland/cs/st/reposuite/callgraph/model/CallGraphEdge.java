/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.callgraph.model;

import java.io.Serializable;

/**
 * The Class MinerCallGraphEdge.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class CallGraphEdge implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1917607287240246932L;
	private int occurrence = 0;
	
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

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
/**
 * 
 * CodeRegion.java
 * 
 * @author Nicolas Bettenburg � 2009-2010, all rights reserved.
 ******************************************************************** 
 *         This file is part of infoZilla. * * InfoZilla is non-free software:
 *         you may not redistribute it * and/or modify it without the permission
 *         of the original author. * * InfoZilla is distributed in the hope that
 *         it will be useful, * but WITHOUT ANY WARRANTY; without even the
 *         implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *         PURPOSE. *
 ******************************************************************** 
 * 
 */

package de.unisaarland.cs.st.reposuite.infozilla.filters.sourcecode;

/**
 * The <code>CodeRegion</code> class represents source code structural elements.
 * Each <code>CodeRegion</code> records the <code>start</code> and <code>end</code> 
 * positions in the original input text, as well as the type of the source code element detected.
 * @author Nicolas Bettenburg
 * @see de.unisaarland.cs.st.reposuite.infozilla.filters.sourcecode.JavaSourceCodeFilter
 * @see de.unisaarland.cs.st.reposuite.infozilla.filters.chain.FilterChain
 * @see de.unisaarland.cs.st.reposuite.infozilla.Ressources.Java_CodeDB.txt
 * @see de.unisaarland.cs.st.reposuite.infozilla.Ressources.Java_Keywords.txt
 */
public class CodeRegion implements Comparable<CodeRegion> {
	
	/** Stores the start position of the source code region in the original input text	 */
	public int    start = 0;
	
	/** Stores the end position of the source code region in the original input text */
	public int    end   = 0;
	
	/** Stores the textual representation of the source code region */
	public String text;
	
	/** Stores the type of source code region as defined in de.unisaarland.cs.st.reposuite.infozilla.Ressources.Java_CodeDB.txt */
	public String keyword;
	
	/**
	 * Standard Constructor
	 * @param start start position of code region
	 * @param end end position of code region
	 * @param keyword type of code region
	 * @param text textual representation
	 */
	public CodeRegion(int start, int end, String keyword, String text) {
		super();
		this.start = start;
		this.end = end;
		this.keyword = keyword;
		this.text = text;
	}
	
	/**
	 * Copy Constructor
	 * @param that another <code>CodeRegion</code> object to copy from.
	 */
	public CodeRegion(CodeRegion that) {
		super();
		this.start = Integer.valueOf(that.start);
		this.end = Integer.valueOf(that.end);
		this.keyword = new String(that.keyword);
		this.text = new String(that.text);
	}
	
	public int compareTo(CodeRegion that) {
		if (this.start < that.start) return -1;
		if (this.start > that.start) return +1;
		return 0;
	}
}

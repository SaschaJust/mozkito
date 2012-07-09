/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 * CodeRegion.java
 * 
 * @author Nicolas Bettenburg � 2009-2010, all rights reserved.
 ******************************************************************** 
 *         This file is part of infoZilla. * * InfoZilla is non-free software: you may not redistribute it * and/or
 *         modify it without the permission of the original author. * * InfoZilla is distributed in the hope that it
 *         will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS
 *         FOR A PARTICULAR PURPOSE. *
 ******************************************************************** 
 * 
 */

package de.unisaarland.cs.st.moskito.infozilla.filters.sourcecode;

/**
 * The <code>CodeRegion</code> class represents source code structural elements. Each <code>CodeRegion</code> records
 * the <code>start</code> and <code>end</code> positions in the original input text, as well as the type of the source
 * code element detected.
 * 
 * @author Nicolas Bettenburg
 * @see de.unisaarland.cs.st.moskito.infozilla.filters.sourcecode.JavaSourceCodeFilter
 * @see de.unisaarland.cs.st.moskito.infozilla.filters.chain.FilterChain
 * @see de.unisaarland.cs.st.moskito.infozilla.Ressources.Java_CodeDB.txt
 * @see de.unisaarland.cs.st.moskito.infozilla.Ressources.Java_Keywords.txt
 */
public class CodeRegion implements Comparable<CodeRegion> {
	
	/** Stores the start position of the source code region in the original input text. */
	public int    start = 0;
	
	/** Stores the end position of the source code region in the original input text. */
	public int    end   = 0;
	
	/** Stores the textual representation of the source code region. */
	public String text;
	
	/**
	 * Stores the type of source code region as defined in
	 * de.unisaarland.cs.st.moskito.infozilla.Ressources.Java_CodeDB.txt
	 */
	public String keyword;
	
	/**
	 * Copy Constructor.
	 * 
	 * @param that
	 *            another <code>CodeRegion</code> object to copy from.
	 */
	public CodeRegion(final CodeRegion that) {
		super();
		this.start = Integer.valueOf(that.start);
		this.end = Integer.valueOf(that.end);
		this.keyword = new String(that.keyword);
		this.text = new String(that.text);
	}
	
	/**
	 * Standard Constructor.
	 * 
	 * @param start
	 *            start position of code region
	 * @param end
	 *            end position of code region
	 * @param keyword
	 *            type of code region
	 * @param text
	 *            textual representation
	 */
	public CodeRegion(final int start, final int end, final String keyword, final String text) {
		super();
		this.start = start;
		this.end = end;
		this.keyword = keyword;
		this.text = text;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final CodeRegion that) {
		if (this.start < that.start) {
			return -1;
		}
		if (this.start > that.start) {
			return +1;
		}
		return 0;
	}
}

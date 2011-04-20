/**
 * 
 * Filter.java
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

package de.unisaarland.cs.st.reposuite.infozilla.filters;

import java.util.List;

/**
 * This interface describes the method interface for every infoZilla Filter.
 * @author Nicolas Bettenburg
 *
 */
public interface Filter {
	
	public List<?> runFilter(String inputText);
	
	public String getOutputText();
	
}

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
package de.unisaarland.cs.st.reposuite.untangling.blob;

/**
 * The Interface CombineOperator.
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface CombineOperator<T> {
	
	/**
	 * Can be combined.
	 * 
	 * @param t1
	 *            the t1
	 * @param t2
	 *            the t2
	 * @return true, if successful
	 */
	public boolean canBeCombined(T t1,
	                             T t2);
}

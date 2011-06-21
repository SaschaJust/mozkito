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
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.Collection;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface Intercepted<T> {
	
	/**
	 * @param id
	 * @param t
	 */
	public T add(String id,
	             T t);
	
	/**
	 * @param id
	 * @return
	 */
	public T get(String id);
	
	/**
	 * @return
	 */
	public Collection<T> interceptorTargets();
	
	/**
	 * @param from
	 * @param to
	 */
	public void replace(T from,
	                    T to);
}

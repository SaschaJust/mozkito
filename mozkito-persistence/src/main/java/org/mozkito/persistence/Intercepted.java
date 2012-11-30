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
 */
package org.mozkito.persistence;

import java.util.Collection;

/**
 * The Interface Intercepted.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface Intercepted<T> {
	
	/**
	 * Adds the.
	 * 
	 * @param id
	 *            the id
	 * @param t
	 *            the t
	 * @return the t
	 */
	T add(String id,
	      T t);
	
	/**
	 * Gets the.
	 * 
	 * @param id
	 *            the id
	 * @return the t
	 */
	T get(String id);
	
	/**
	 * Interceptor targets.
	 * 
	 * @return the collection
	 */
	Collection<T> interceptorTargets();
	
	/**
	 * Replace.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	void replace(T from,
	             T to);
}

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
package de.unisaarland.cs.st.moskito.persistence;

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

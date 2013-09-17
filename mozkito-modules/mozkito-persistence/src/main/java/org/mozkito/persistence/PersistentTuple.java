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

package org.mozkito.persistence;

/**
 * The Interface PersistentTuple.
 * 
 * @param <T>
 *            the generic type
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface PersistentTuple<T> extends Annotated {
	
	/**
	 * Gets the new value.
	 * 
	 * @return the new value
	 */
	T getNewValue();
	
	/**
	 * Gets the old value.
	 * 
	 * @return the old value
	 */
	T getOldValue();
	
	/**
	 * Sets the new value.
	 * 
	 * @param newValue
	 *            the new new value
	 */
	void setNewValue(T newValue);
	
	/**
	 * Sets the old value.
	 * 
	 * @param oldValue
	 *            the new old value
	 */
	void setOldValue(T oldValue);
}

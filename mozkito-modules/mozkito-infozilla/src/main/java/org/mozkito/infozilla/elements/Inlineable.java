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
package org.mozkito.infozilla.elements;

import org.joda.time.DateTime;

import org.mozkito.persons.model.Person;

/**
 * The Interface Inlineable.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface Inlineable extends Attachable {
	
	/**
	 * Gets the end position.
	 * 
	 * @return the end position
	 */
	Integer getEndPosition();
	
	/**
	 * Gets the posted by.
	 * 
	 * @return the posted by
	 */
	Person getPostedBy();
	
	/**
	 * Gets the posted on.
	 * 
	 * @return the posted on
	 */
	DateTime getPostedOn();
	
	/**
	 * Gets the start position.
	 * 
	 * @return the start position
	 */
	Integer getStartPosition();
	
	/**
	 * Sets the posted by.
	 * 
	 * @param author
	 *            the new posted by
	 */
	void setPostedBy(Person author);
	
	/**
	 * Sets the posted on.
	 * 
	 * @param timestamp
	 *            the new posted on
	 */
	void setPostedOn(DateTime timestamp);
}

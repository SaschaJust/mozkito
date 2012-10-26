/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
/**
 * 
 */
package org.mozkito.issues.tracker.model;

import org.joda.time.DateTime;
import org.mozkito.persistence.model.Person;


/**
 * The Interface TextElement.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface TextElement {
	
	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public Person getAuthor();
	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText();
	
	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public DateTime getTimestamp();
}

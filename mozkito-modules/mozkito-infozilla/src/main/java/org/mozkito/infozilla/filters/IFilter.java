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

package org.mozkito.infozilla.filters;

import java.awt.Color;
import java.util.List;

import org.joda.time.DateTime;

import org.mozkito.infozilla.elements.FilterStatistics;
import org.mozkito.infozilla.elements.Inlineable;
import org.mozkito.persons.model.Person;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 * @param <T>
 */
public interface IFilter<T extends Inlineable> {
	
	/**
	 * Filter.
	 * 
	 * @param text
	 *            the text
	 * @param author
	 *            the author
	 * @param timestamp
	 *            the timestamp
	 * @return the list
	 */
	List<T> filter(String text,
	               Person author,
	               DateTime timestamp);
	
	/**
	 * Gets the stats.
	 * 
	 * @return the stats
	 */
	FilterStatistics getStats();
	
	/**
	 * Highlight color.
	 * 
	 * @return the color
	 */
	Color highlightColor();
	
}

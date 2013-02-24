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

import java.util.List;

/**
 * This interface describes the method interface for every infoZilla InfozillaFilter.
 * 
 * @author Nicolas Bettenburg, Sascha Just
 * 
 */
public abstract class InfozillaFilter {
	
	/**
	 * Gets the output text.
	 * 
	 * @return the output text
	 */
	public abstract String getOutputText();
	
	/**
	 * Run filter.
	 * 
	 * @param inputText
	 *            the input text
	 * @return the list
	 */
	public abstract List<?> runFilter(String inputText);
	
}

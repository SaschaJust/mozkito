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
package org.mozkito.infozilla.filters.sourcecode;

import org.mozkito.infozilla.filters.Filter;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.source.SourceCode;

/**
 * The Class SourceCodeFilter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public abstract class SourceCodeFilter extends Filter<SourceCode> {
	
	/**
	 * @param enhancedReport
	 */
	public SourceCodeFilter(final EnhancedReport enhancedReport) {
		super(enhancedReport);
	}
	
}

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

package org.mozkito.infozilla;

import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Report;

/**
 * The Interface IFilterManager.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface IFilterManager {
	
	/**
	 * Gets the enhanced report.
	 * 
	 * @return the enhanced report
	 */
	EnhancedReport getEnhancedReport();
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	Report getReport();
	
	/**
	 * Parses the report.
	 * 
	 * @return the enhanced report
	 */
	EnhancedReport parse();
	
}
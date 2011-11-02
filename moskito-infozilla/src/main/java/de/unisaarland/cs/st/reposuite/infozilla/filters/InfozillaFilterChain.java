/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.filters;

import java.util.List;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.infozilla.model.EnhancedReport;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class InfozillaFilterChain {
	
	/**
	 * @param filter
	 */
	public void addFilter(final InfozillaFilter filter) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the enumerations
	 */
	public List<?> getEnumerations() {
		return null;
	}
	
	/**
	 * @return the inputText
	 */
	public String getInputText() {
		return null;
	}
	
	/**
	 * @return the outputText
	 */
	public String getOutputText() {
		return null;
	}
	
	/**
	 * @return the patches
	 */
	public List<?> getPatches() {
		return null;
	}
	
	/**
	 * @return the regions
	 */
	public List<?> getRegions() {
		return null;
	}
	
	/**
	 * @return the traces
	 */
	public List<?> getTraces() {
		return null;
	}
	
	/**
	 * @param report
	 * @return
	 */
	public EnhancedReport parse(final Report report) {
		return null;
	}
	
	/**
	 * @param inputText the inputText to set
	 */
	public void setInputText(final String inputText) {
	}
}

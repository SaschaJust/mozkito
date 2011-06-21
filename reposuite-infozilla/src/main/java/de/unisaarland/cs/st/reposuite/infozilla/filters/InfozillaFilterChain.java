/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
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

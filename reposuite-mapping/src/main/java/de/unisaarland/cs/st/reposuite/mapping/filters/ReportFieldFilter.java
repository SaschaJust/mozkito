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
package de.unisaarland.cs.st.reposuite.mapping.filters;

import java.util.Set;

import de.unisaarland.cs.st.reposuite.mapping.model.RCSBugMapping;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class ReportFieldFilter extends MappingFilter {
	
	/**
	 * 
	 */
	public ReportFieldFilter() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Set<MappingFilter> filter(final RCSBugMapping mapping,
	                                 final Set<MappingFilter> triggeringFilters) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

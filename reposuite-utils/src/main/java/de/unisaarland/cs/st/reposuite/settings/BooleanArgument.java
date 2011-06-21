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
package de.unisaarland.cs.st.reposuite.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class BooleanArgument extends RepoSuiteArgument {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * 
	 */
	public BooleanArgument(final RepoSuiteSettings settings, final String name, final String description, final String defaultValue,
	                       final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	@Override
	public Boolean getValue() {
		if (stringValue == null) {
			return null;
		}
		if (stringValue.trim().equals("")) {
			return true;
		}
		return Boolean.parseBoolean(stringValue);
	}
}

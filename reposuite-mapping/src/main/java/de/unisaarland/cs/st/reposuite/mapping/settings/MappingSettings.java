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
package de.unisaarland.cs.st.reposuite.mapping.settings;

import de.unisaarland.cs.st.reposuite.bugs.tracker.settings.TrackerSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingSettings extends TrackerSettings {
	
	/**
	 * @param isRequired
	 * @return
	 */
	public MappingArguments setMappingArgs(final boolean isRequired) {
		MappingArguments mappingArguments = new MappingArguments(this, isRequired);
		return mappingArguments;
	}
	
}

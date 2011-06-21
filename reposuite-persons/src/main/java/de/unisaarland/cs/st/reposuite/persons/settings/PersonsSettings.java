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
package de.unisaarland.cs.st.reposuite.persons.settings;

import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class PersonsSettings extends RepositorySettings {
	
	/**
	 * @param isRequired
	 * @return
	 */
	public PersonsArguments setPersonsArgs(final boolean isRequired) {
		PersonsArguments personsArguments = new PersonsArguments(this, isRequired);
		return personsArguments;
	}
	
}

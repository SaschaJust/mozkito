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
package de.unisaarland.cs.st.moskito.mapping.settings;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingSettings extends Settings {
	
	/**
	 * Add the settings set for the database.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public DatabaseArguments setDatabaseArgs(final Requirement requirement,
	                                         final String unit) throws ArgumentRegistrationException {
		final DatabaseArguments minerDatabaseArguments = new DatabaseArguments(getRootArgumentSet(), requirement, unit);
		return minerDatabaseArguments;
	}
	
	/**
	 * @param isRequired
	 * @return
	 * @throws ArgumentRegistrationException
	 */
	public MappingArguments setMappingArgs(final ArgumentSet<?> set,
	                                       final Requirement requirement) throws ArgumentRegistrationException {
		final MappingArguments mappingArguments = new MappingArguments(set, requirement);
		return mappingArguments;
	}
	
}

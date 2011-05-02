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

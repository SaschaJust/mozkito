/**
 * 
 */
package de.unisaarland.cs.st.moskito.untangling.settings;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UntanglingSettings extends RepositorySettings {
	
	public UntanglingArguments setUntanglingArgs(final Requirement requirement) throws ArgumentRegistrationException {
		final UntanglingArguments arguments = new UntanglingArguments(getRootArgumentSet(), requirement);
		return arguments;
	}
}

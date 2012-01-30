/**
 * 
 */
package de.unisaarland.cs.st.moskito.untangling.settings;

import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class UntanglingSettings extends RepositorySettings {
	
	public UntanglingArguments setUntanglingArgs(final boolean isRequired) {
		final UntanglingArguments arguments = new UntanglingArguments(this, isRequired);
		return arguments;
	}
}

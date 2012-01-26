/**
 * 
 */
package de.unisaarland.cs.st.moskito.untangling.settings;

import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

/**
 * @author just
 * 
 */
public class UntanglingSettings extends RepositorySettings {
	
	public UntanglingArguments setUntanglingArgs(final boolean isRequired) {
		final UntanglingArguments arguments = new UntanglingArguments(this, isRequired);
		return arguments;
	}
}

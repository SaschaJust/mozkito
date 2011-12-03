package de.unisaarland.cs.st.moskito.genealogies.settings;

import net.ownhero.dev.andama.settings.AndamaSettings;


public class GenealogySettings extends AndamaSettings {
	
	/**
	 * Add the repository argument set.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the repository settings are
	 *            required.
	 * @return the genealogy arguments
	 */
	public GenealogyArguments setGenealogyArgs(final boolean isRequired) {
		return new GenealogyArguments(this, isRequired, "ppa");
	}
	
}

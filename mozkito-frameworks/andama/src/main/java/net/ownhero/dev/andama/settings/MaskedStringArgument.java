/**
 * 
 */
package net.ownhero.dev.andama.settings;

import net.ownhero.dev.andama.settings.dependencies.Requirement;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MaskedStringArgument extends StringArgument {
	
	/**
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 */
	public MaskedStringArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final Requirement requirements) {
		super(argumentSet, name, description, defaultValue, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#toString()
	 */
	@Override
	public String toString(final int indentation) {
		return String.format("%-" + indentation + "s", "") + "Argument [required=" + required() + ", description="
		        + getDescription() + ", name=" + getName() + ", value=********* (masked)]";
	}
	
}

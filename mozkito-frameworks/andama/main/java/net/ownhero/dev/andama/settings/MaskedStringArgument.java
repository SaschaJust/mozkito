/**
 * 
 */
package net.ownhero.dev.andama.settings;

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
	public MaskedStringArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#toString()
	 */
	@Override
	public String toString() {
		return "RepoSuiteArgument [isRequired=" + isRequired() + ", description=" + getDescription() + ", name="
		        + getName() + ", stringValue=********* (masked)]";
	}
	
}

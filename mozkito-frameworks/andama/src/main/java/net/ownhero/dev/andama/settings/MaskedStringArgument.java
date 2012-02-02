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
	public MaskedStringArgument(final AndamaArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(argumentSet, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#toString()
	 */
	@Override
	public String toString() {
		return "Argument [required=" + required() + ", description=" + getDescription() + ", name=" + getName()
		        + ", value=********* (masked)]";
	}
	
}

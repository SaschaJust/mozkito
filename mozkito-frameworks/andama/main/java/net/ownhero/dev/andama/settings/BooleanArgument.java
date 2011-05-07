package net.ownhero.dev.andama.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class BooleanArgument extends AndamaArgument<Boolean> {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * 
	 */
	public BooleanArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#getValue()
	 */
	@Override
	public Boolean getValue() {
		if (this.actualValue == null) {
			return null;
		}
		
		return Boolean.parseBoolean(this.actualValue);
	}
}

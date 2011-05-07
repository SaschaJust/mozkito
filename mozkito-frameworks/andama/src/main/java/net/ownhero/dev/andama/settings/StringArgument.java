package net.ownhero.dev.andama.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class StringArgument extends AndamaArgument<String> {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public StringArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public String getValue() {
		return this.actualValue;
	}
}

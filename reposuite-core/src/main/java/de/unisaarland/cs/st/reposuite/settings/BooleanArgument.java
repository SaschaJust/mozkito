package de.unisaarland.cs.st.reposuite.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class BooleanArgument extends RepoSuiteArgument {
	
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
	public BooleanArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	@Override
	public Boolean getValue() {
		return Boolean.parseBoolean(stringValue);
	}
}

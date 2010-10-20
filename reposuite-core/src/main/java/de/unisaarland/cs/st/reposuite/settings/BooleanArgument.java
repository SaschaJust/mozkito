package de.unisaarland.cs.st.reposuite.settings;

public class BooleanArgument extends RepoSuiteArgument {
	
	public BooleanArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) throws DuplicateArgumentException {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	@Override
	public Boolean getValue() {
		return Boolean.parseBoolean(stringValue);
	}
}

package de.unisaarland.cs.st.reposuite.settings;

public class StringArgument extends RepoSuiteArgument {
	
	public StringArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) throws DuplicateArgumentException {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	@Override
	public String getValue() {
		return stringValue;
	}
}

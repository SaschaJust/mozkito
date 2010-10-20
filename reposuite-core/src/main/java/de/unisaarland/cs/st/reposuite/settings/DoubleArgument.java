package de.unisaarland.cs.st.reposuite.settings;

import org.apache.log4j.Logger;

public class DoubleArgument extends RepoSuiteArgument {
	
	public DoubleArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) throws DuplicateArgumentException {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	@Override
	public Double getValue() {
		if (stringValue == null) {
			return null;
		}
		try {
			return new Double(stringValue);
		} catch (NumberFormatException e) {
			Logger.getLogger(DoubleArgument.class).error(
			        "Value given for argument `" + getName() + "` could not be interpreted as a Double value. Abort!");
			throw new RuntimeException();
		}
	}
}

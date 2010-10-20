package de.unisaarland.cs.st.reposuite.settings;

import org.apache.log4j.Logger;

public class LongArgument extends RepoSuiteArgument {
	
	public LongArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) throws DuplicateArgumentException {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	@Override
	public Long getValue() {
		if (stringValue == null) {
			return null;
		}
		try {
			return new Long(stringValue);
		} catch (NumberFormatException e) {
			Logger.getLogger(LongArgument.class).error(
			        "Value given for argument `" + getName() + "` could not be interpreted as a Long value. Abort!");
			throw new RuntimeException();
		}
	}
}

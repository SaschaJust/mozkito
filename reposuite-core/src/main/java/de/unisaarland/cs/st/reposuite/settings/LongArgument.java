package de.unisaarland.cs.st.reposuite.settings;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class LongArgument extends RepoSuiteArgument {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArguments
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public LongArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public Long getValue() {
		if (this.stringValue == null) {
			return null;
		}
		try {
			return new Long(this.stringValue);
		} catch (NumberFormatException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Value given for argument `" + getName()
				        + "` could not be interpreted as a Long value. Abort!");
			}
			throw new RuntimeException();
		}
	}
}

package de.unisaarland.cs.st.reposuite.settings;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DoubleArgument extends RepoSuiteArgument {
	
	/**
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public DoubleArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public Double getValue() {
		if (this.stringValue == null) {
			return null;
		}
		try {
			return new Double(this.stringValue);
		} catch (NumberFormatException e) {
			if (Logger.logError()) {
				Logger.error("Value given for argument `" + getName()
				        + "` could not be interpreted as a Double value. Abort!");
			}
			throw new RuntimeException();
		}
	}
}

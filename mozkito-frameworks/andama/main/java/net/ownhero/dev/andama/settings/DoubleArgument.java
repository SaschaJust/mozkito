package net.ownhero.dev.andama.settings;

import net.ownhero.dev.andama.exceptions.Shutdown;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DoubleArgument extends AndamaArgument<Double> {
	
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
	public DoubleArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public Double getValue() {
		if (this.actualValue == null) {
			return null;
		}
		
		try {
			return new Double(this.actualValue);
		} catch (NumberFormatException e) {
			throw new Shutdown("Value given for argument `" + getName()
			        + "` could not be interpreted as a Double value. Abort!");
		}
	}
}

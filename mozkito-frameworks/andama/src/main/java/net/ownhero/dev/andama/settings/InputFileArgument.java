package net.ownhero.dev.andama.settings;

import java.io.File;

import net.ownhero.dev.andama.exceptions.Shutdown;

/**
 * The Class FileArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class InputFileArgument extends AndamaArgument<File> {
	
	// FIXME write test cases
	/**
	 * Constructor for FileArgument. Besides the obvious and general
	 * RepoSuiteArgument parameters, FileArguments can be configures using two
	 * special parameters: <code>overwrite</code> and <code>mustExist</code>.
	 * 
	 * @param settings
	 *            The RepoSuiteSetting instance this argument will register for
	 * @param name
	 *            Name of the Argument
	 * @param description
	 *            The help string description
	 * @param defaultValue
	 *            The default value given as string will be interpreted as path
	 * @param isRequired
	 *            Set to <code>true</code> if this argument will be required
	 * @param overwrite
	 *            Set to <code>true</code> if you want the RepoSuite tool to
	 *            attempt overwriting the file located at given path if
	 *            possible.
	 * @param mustExist
	 *            Set to true if you want to ensure that the file at given
	 *            location must already exist.
	 */
	public InputFileArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public File getValue() {
		if (this.actualValue == null) {
			return null;
		}
		
		File file = new File(this.actualValue.trim());
		
		if (file.isDirectory()) {
			throw new Shutdown("The file `" + this.actualValue + "` specified for argument `" + getName()
			        + "` is a directory. Expected file. Abort.");
		}
		if (!file.exists() && this.isRequired()) {
			throw new Shutdown("The file `" + this.actualValue + "` specified for argument `" + getName()
			        + "` does not exists but is required!");
		}
		
		if (!file.exists() && !this.isRequired()) {
			throw new Shutdown("The file `" + this.actualValue + "` specified for argument `" + getName()
			        + "` does not exists and is not required! Ignoring file argument!");
		}
		
		return file;
	}
}

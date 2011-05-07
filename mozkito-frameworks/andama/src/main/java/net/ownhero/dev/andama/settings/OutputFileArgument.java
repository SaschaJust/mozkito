package net.ownhero.dev.andama.settings;

import java.io.File;
import java.io.IOException;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.utils.Logger;

/**
 * The Class FileArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class OutputFileArgument extends AndamaArgument<File> {
	
	// FIXME write test cases
	private boolean overwrite   = false;
	private boolean selfWritten = false;
	
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
	public OutputFileArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final boolean overwrite) {
		super(settings, name, description, defaultValue, isRequired);
		this.overwrite = overwrite;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public File getValue() {
		// FIME seprate input and output files. Fix the mustExist and overwrite
		// conbinations!
		if (this.actualValue == null) {
			return null;
		}
		File file = new File(this.actualValue.trim());
		
		if (file.isDirectory()) {
			throw new Shutdown("The file `" + this.actualValue + "` specified for argument `" + getName()
			        + "` is a directory. Expected file. Abort.");
		}
		if (file.exists() && (!this.overwrite) && (!this.selfWritten)) {
			if (this.isRequired()) {
				throw new Shutdown("The file `" + this.actualValue + "` specified for argument `" + getName()
				        + "` exists already. Please remove file or choose different argument value.");
			} else {
				if (Logger.logWarn()) {
					Logger.warn("The file `" + this.actualValue + "` specified for argument `" + getName()
					        + "` exists already and cannot be overwritten. Ignoring argument!.");
				}
				return null;
			}
			
		} else if (file.exists() && (this.overwrite)) {
			
			if (Logger.logDebug()) {
				if (Logger.logDebug()) {
					Logger.debug("Attempt overwriting file `" + file.getAbsolutePath() + "` ...");
				}
			}
			
			if (!file.delete()) {
				throw new Shutdown("Could not delete file `" + file.getAbsolutePath() + "`. Abort.");
			}
			
			try {
				if (!file.createNewFile()) {
					throw new Shutdown("Could not re-create file `" + file.getAbsolutePath() + "`. Abort.");
				}
			} catch (IOException e) {
				throw new Shutdown(e.getMessage(), e);
			}
		} else if (!this.selfWritten) {
			// file does not exist so far
			try {
				if (!file.createNewFile()) {
					if (Logger.logError()) {
						Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
					}
					if (this.isRequired()) {
						throw new Shutdown();
					} else {
						return null;
					}
				}
			} catch (IOException e) {
				if (Logger.logError()) {
					Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
					Logger.error(e.getMessage());
				}
				if (this.isRequired()) {
					throw new Shutdown();
				} else {
					return null;
				}
			}
		}
		this.selfWritten = true;
		return file;
	}
}

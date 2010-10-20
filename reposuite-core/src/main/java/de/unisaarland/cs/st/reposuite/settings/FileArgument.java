package de.unisaarland.cs.st.reposuite.settings;

import java.io.File;
import java.io.IOException;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class FileArgument extends RepoSuiteArgument {
	
	private boolean overwrite = false;
	private boolean mustExist = false;
	
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
	 * @throws DuplicateArgumentException
	 */
	public FileArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired, boolean overwrite, boolean mustExist) {
		super(settings, name, description, defaultValue, isRequired);
		this.overwrite = overwrite;
		this.mustExist = mustExist;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public File getValue() {
		if (stringValue == null) {
			return null;
		}
		File file = new File(stringValue.trim());
		
		if (file.isDirectory()) {
			Logger.error("The file `" + stringValue + "` specified for argument `" + getName()
			        + "` is a directory. Expected file. Abort.");
			throw new RuntimeException();
		}
		if (file.exists() && (!overwrite)) {
			Logger.error("The file `" + stringValue + "` specified for argument `" + getName()
			        + "` exists already. Please remove file or choose different argument value.");
			throw new RuntimeException();
		} else if (file.exists() && (overwrite)) {
			
			if (RepoSuiteSettings.debug) {
				Logger.debug("Attempt overwriting file `" + file.getAbsolutePath() + "` ...");
			}
			
			if (!file.delete()) {
				Logger.error("Could not delete file `" + file.getAbsolutePath() + "`. Abort.");
				throw new RuntimeException();
			}
			try {
				if (!file.createNewFile()) {
					Logger.error("Could not re-create file `" + file.getAbsolutePath() + "`. Abort.");
					throw new RuntimeException();
				}
			} catch (IOException e) {
				Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
				Logger.error(e.getMessage());
				
			}
		} else {
			//file does not exist so far
			if (mustExist) {
				Logger.error("Specified file `" + file.getAbsolutePath() + "` for argument `" + getName()
				        + "` must exist but does not exist. Abort.");
				throw new RuntimeException();
			} else {
				try {
					if (!file.createNewFile()) {
						Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
						throw new RuntimeException();
					}
				} catch (IOException e) {
					Logger.error("Could not create file `" + file.getAbsolutePath() + "`. Abort.");
					Logger.error(e.getMessage());
				}
			}
		}
		return file;
	}
}

package de.unisaarland.cs.st.reposuite.settings;

import java.io.File;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DirectoryArgument extends RepoSuiteArgument {
	
	private boolean create = false;
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @param create
	 *            Attempts to create directory if not exist
	 */
	public DirectoryArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired, boolean create) {
		super(settings, name, description, defaultValue, isRequired);
		
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
		
		if (!file.exists()) {
			if (!create) {
				Logger.error("The file `" + stringValue + "` specified for argument `" + getName()
				        + "` does not exist.");
				throw new RuntimeException();
			} else {
				if (!file.mkdirs()) {
					Logger.error("The file `" + stringValue + "` specified for argument `" + getName()
					        + "` does not exist and cannot be created.");
					throw new RuntimeException();
				}
			}
		}
		if (!file.isDirectory()) {
			Logger.error("The directory `" + stringValue + "` specified for argument `" + getName()
			        + "` is not a directory. Please remove file or choose different argument value.");
			throw new RuntimeException();
		}
		return file;
	}
}

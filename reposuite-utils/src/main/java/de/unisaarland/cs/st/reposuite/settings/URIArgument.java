package de.unisaarland.cs.st.reposuite.settings;

import java.net.URI;
import java.net.URISyntaxException;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class URIArgument extends RepoSuiteArgument {
	
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
	 * @throws DuplicateArgumentException
	 */
	public URIArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public URI getValue() {
		if (this.stringValue == null) {
			return null;
		}
		
		try {
			return new URI(this.stringValue);
		} catch (URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error("When parsing URI string `" + this.stringValue + "` for argument `" + getName()
				        + "`, the following error occurred: " + e.getMessage());
			}
			throw new RuntimeException();
		}
	}
}

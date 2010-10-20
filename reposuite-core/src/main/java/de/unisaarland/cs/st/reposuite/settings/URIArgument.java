package de.unisaarland.cs.st.reposuite.settings;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class URIArgument extends RepoSuiteArgument {
	
	/**
	 * This is similar to FileArgument but requires the file to be a directory
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
	        boolean isRequired) throws DuplicateArgumentException {
		super(settings, name, description, defaultValue, isRequired);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public URI getValue() {
		if (stringValue == null) {
			return null;
		}
		
		try {
			return new URI(stringValue);
		} catch (URISyntaxException e) {
			Logger.getLogger(URIArgument.class).error(
			        "When parsing URI string `" + stringValue + "` for argument `" + getName()
			                + "`, the following error occurred: " + e.getMessage());
			throw new RuntimeException();
		}
	}
}

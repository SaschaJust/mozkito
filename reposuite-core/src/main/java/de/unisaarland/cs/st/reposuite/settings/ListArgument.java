package de.unisaarland.cs.st.reposuite.settings;

import java.util.HashSet;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ListArgument extends RepoSuiteArgument {
	
	private String delimiter;
	
	/**
	 * General Arguments as described in RepoSuiteArgument. The string value
	 * will be split using delimiter `,` to receive the list of values.
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public ListArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		delimiter = ",";
	}
	
	/**
	 * 
	 * General Arguments as described in RepoSuiteArgument
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @param delimiter
	 *            The string value will be split using this delimiter to receive
	 *            the list of values
	 * @throws DuplicateArgumentException
	 */
	public ListArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired, String delimiter) {
		super(settings, name, description, defaultValue, isRequired);
		this.delimiter = delimiter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public HashSet<String> getValue() {
		if (stringValue == null) {
			return null;
		}
		HashSet<String> result = new HashSet<String>();
		for (String s : stringValue.split(delimiter)) {
			result.add(s.trim());
		}
		return result;
	}
}

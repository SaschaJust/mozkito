package net.ownhero.dev.andama.settings;

import java.util.HashSet;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ListArgument extends AndamaArgument<HashSet<String>> {
	
	private final String delimiter;
	
	/**
	 * General Arguments as described in RepoSuiteArgument. The string value
	 * will be split using delimiter `,` to receive the list of values.
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws DuplicateArgumentException
	 */
	public ListArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
		super(settings, name, description, defaultValue, isRequired);
		this.delimiter = ",";
	}
	
	/**
	 * 
	 * General Arguments as described in RepoSuiteArgument
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
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
	public ListArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final String delimiter) {
		super(settings, name, description, defaultValue, isRequired);
		this.delimiter = delimiter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public HashSet<String> getValue() {
		if (this.actualValue == null) {
			return null;
		}
		
		HashSet<String> result = new HashSet<String>();
		
		for (String s : this.actualValue.split(this.delimiter)) {
			result.add(s.trim());
		}
		
		return result;
	}
}

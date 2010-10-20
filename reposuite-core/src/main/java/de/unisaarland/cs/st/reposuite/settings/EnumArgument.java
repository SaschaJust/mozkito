package de.unisaarland.cs.st.reposuite.settings;

import java.util.HashSet;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class EnumArgument extends RepoSuiteArgument {
	
	private HashSet<String> possibleValues;
	
	/**
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 * 
	 */
	public EnumArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired, String[] possibleValues) {
		super(settings, name, description, defaultValue, isRequired);
		this.possibleValues = new HashSet<String>();
		for (String s : possibleValues) {
			this.possibleValues.add(s);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public String getValue() {
		return stringValue;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#setStringValue
	 * (java.lang.String)
	 */
	@Override
	protected void setStringValue(String value) {
		if (!possibleValues.contains(value)) {
			StringBuilder ss = new StringBuilder();
			ss.append("Value set for argument `");
			ss.append(getName());
			ss.append("` is invalid.");
			ss.append(System.getProperty("line.separator"));
			ss.append("Please choose one of the following possible values:");
			ss.append(System.getProperty("line.separator"));
			for (String s : possibleValues) {
				ss.append("\t");
				ss.append(s);
				ss.append(System.getProperty("line.separator"));
			}
			Logger.error(ss.toString());
			System.exit(-1);
		}
		super.setStringValue(value);
	}
}

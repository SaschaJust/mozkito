package de.unisaarland.cs.st.reposuite.settings;

import java.util.HashSet;

import org.apache.log4j.Logger;

public class EnumArgument extends RepoSuiteArgument {
	
	private HashSet<String> possibleValues;
	
	public EnumArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired, String[] possibleValues) throws DuplicateArgumentException {
		super(settings, name, description, defaultValue, isRequired);
		this.possibleValues = new HashSet<String>();
		for (String s : possibleValues) {
			this.possibleValues.add(s);
		}
	}
	
	@Override
	public String getValue() {
		return stringValue;
	}
	
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
			Logger.getLogger(EnumArgument.class).error(ss.toString());
			System.exit(-1);
		}
		super.setStringValue(value);
	}
}

package net.ownhero.dev.andama.settings;

import java.util.HashSet;

import net.ownhero.dev.andama.exceptions.Shutdown;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class EnumArgument extends AndamaArgument<String> {
	
	private final HashSet<String> possibleValues;
	
	/**
	 * 
	 * @see de.unisaarland.cs.st.reposuite.settings.AndamaArgument
	 * 
	 */
	public EnumArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired, final String[] possibleValues) {
		super(settings, name, description, defaultValue, isRequired);
		this.possibleValues = new HashSet<String>();
		for (String s : possibleValues) {
			this.possibleValues.add(s);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	public String getValue() {
		return this.actualValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#setStringValue
	 * (java.lang.String)
	 */
	@Override
	protected void setValue(String value) {
		value = value.toUpperCase();
		
		if (!this.possibleValues.contains(value)) {
			StringBuilder ss = new StringBuilder();
			ss.append("Value `" + value + "` set for argument `");
			ss.append(getName());
			ss.append("` is invalid.");
			ss.append(System.getProperty("line.separator"));
			ss.append("Please choose one of the following possible values:");
			ss.append(System.getProperty("line.separator"));
			
			for (String s : this.possibleValues) {
				ss.append("\t");
				ss.append(s);
				ss.append(System.getProperty("line.separator"));
			}
			
			throw new Shutdown(ss.toString());
		}
		super.setValue(value);
	}
}

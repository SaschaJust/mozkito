package de.unisaarland.cs.st.reposuite.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class RepoSuiteArgument {
	
	private boolean  isRequired;
	private String   description;
	private String   name;
	protected String stringValue;
	
	/**
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
	 */
	public RepoSuiteArgument(RepoSuiteSettings settings, String name, String description, String defaultValue,
	        boolean isRequired) {
		this.name = name;
		this.description = description;
		this.isRequired = isRequired;
		if (defaultValue != null) {
			stringValue = defaultValue;
		}
		settings.addArgument(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RepoSuiteArgument other = (RepoSuiteArgument) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return The description of the argument (as printed in help string).
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return The name of the argument (as printed in help string).
	 */
	public String getName() {
		return name;
	}
	
	public abstract Object getValue();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	/**
	 * @return <code>true</code> if the argument is set to be required
	 */
	public boolean isRequired() {
		return isRequired;
	}
	
	/**
	 * Sets the argument to be required
	 * 
	 * @param required
	 */
	public void setRequired(boolean required) {
		isRequired = required;
	}
	
	/**
	 * Sets the string value for the argument.
	 * 
	 * @param value
	 */
	protected void setStringValue(String value) {
		stringValue = value;
	}
	
}

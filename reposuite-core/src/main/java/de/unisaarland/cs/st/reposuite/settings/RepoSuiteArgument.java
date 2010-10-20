package de.unisaarland.cs.st.reposuite.settings;

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
	        boolean isRequired) throws DuplicateArgumentException {
		this.name = name;
		this.description = description;
		this.isRequired = isRequired;
		if (defaultValue != null) {
			stringValue = defaultValue;
		}
		settings.addArgument(this);
	}
	
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
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract Object getValue();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	public boolean isRequired() {
		return isRequired;
	}
	
	public void setRequired(boolean required) {
		isRequired = required;
	}
	
	protected void setStringValue(String value) {
		stringValue = value;
	}
	
}

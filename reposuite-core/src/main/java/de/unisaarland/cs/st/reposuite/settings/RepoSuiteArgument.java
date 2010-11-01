package de.unisaarland.cs.st.reposuite.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class RepoSuiteArgument {
	
	private String       defaultValue;
	private final String description;
	private boolean      isRequired;
	private final String name;
	protected String     stringValue;
	
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
			this.stringValue = defaultValue;
			this.defaultValue = defaultValue;
		}
		settings.addArgument(this);
	}
	
	/*
	 * (non-Javadoc)
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
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return The description of the argument (as printed in help string).
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the simple class name
	 */
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @return The name of the argument (as printed in help string).
	 */
	public String getName() {
		return this.name;
	}
	
	public abstract Object getValue();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}
	
	/**
	 * @return <code>true</code> if the argument is set to be required
	 */
	public boolean isRequired() {
		return this.isRequired;
	}
	
	/**
	 * Sets the argument to be required
	 * 
	 * @param required
	 */
	public void setRequired(boolean required) {
		this.isRequired = required;
	}
	
	/**
	 * Sets the string value for the argument.
	 * 
	 * @param value
	 */
	protected void setStringValue(String value) {
		this.stringValue = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RepoSuiteArgument [isRequired=" + this.isRequired + ", description=" + this.description + ", name="
		        + this.name + ", defaultValue=" + this.defaultValue + ", stringValue=" + this.stringValue + "]";
	}
	
}

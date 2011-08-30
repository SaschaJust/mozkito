/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class AndamaArgument<T> implements Comparable<AndamaArgument<T>> {
	
	private String       defaultValue;
	private final String description;
	private boolean      isRequired;
	private final String name;
	protected String     stringValue;
	private boolean      wasSet;
	
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
	public AndamaArgument(final AndamaSettings settings, final String name, final String description,
	        final String defaultValue, final boolean isRequired) {
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
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final AndamaArgument<T> arg0) {
		return this.name.compareTo(arg0.name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		AndamaArgument<?> other = (AndamaArgument<?>) obj;
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
	 * @return
	 */
	public String getDefaultValue() {
		return this.defaultValue;
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
	
	public abstract T getValue();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null)
		                                                ? 0
		                                                : this.name.hashCode());
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
	public void setRequired(final boolean required) {
		this.isRequired = required;
	}
	
	/**
	 * Sets the string value for the argument.
	 * 
	 * @param value
	 */
	protected void setStringValue(final String value) {
		this.stringValue = value;
		this.wasSet = true;
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
	
	/**
	 * @return
	 */
	public boolean wasSet() {
		return this.wasSet;
	}
	
}

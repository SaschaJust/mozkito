/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package net.ownhero.dev.hiari.settings.exceptions;

import java.util.List;

import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class SettingsParseError.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class SettingsParseError extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6569706686166731951L;
	
	/** The argument. */
	private IArgument<?, ?>   argument         = null;
	
	/**
	 * Instantiates a new settings parse error.
	 */
	public SettingsParseError() {
		super();
	}
	
	/**
	 * Instantiates a new settings parse error.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public SettingsParseError(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new settings parse error.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param argument
	 *            the argument
	 */
	public SettingsParseError(final String arg0, final IArgument<?, ?> argument) {
		super(arg0);
		this.argument = argument;
	}
	
	/**
	 * Instantiates a new settings parse error.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param argument
	 *            the argument
	 * @param arg1
	 *            the arg1
	 */
	public SettingsParseError(final String arg0, final IArgument<?, ?> argument, final Throwable arg1) {
		super(arg0, arg1);
		this.argument = argument;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		if (this.argument != null) {
			final StringBuilder builder = new StringBuilder();
			builder.append(super.getMessage());
			builder.append(FileUtils.lineSeparator).append(this.argument.getName());
			final List<Requirement> requirements = this.argument.getRequirements().getFailedChecks();
			builder.append(FileUtils.lineSeparator).append("Total dependencies: ")
			       .append(this.argument.getRequirements());
			
			if (requirements != null) {
				builder.append(FileUtils.lineSeparator).append("Unresolved dependencies: ")
				       .append(JavaUtils.collectionToString(requirements));
			}
			
			return builder.toString();
		}
		return super.getMessage();
	}
	
}

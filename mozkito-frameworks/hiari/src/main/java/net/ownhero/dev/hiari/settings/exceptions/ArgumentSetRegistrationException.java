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

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IArgumentSetOptions;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.utilities.io.FileUtils;

/**
 * The Class ArgumentRegistrationException.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ArgumentSetRegistrationException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long              serialVersionUID = 1258725690431585182L;
	
	/** The argument. */
	private final ArgumentSet<?, ?>        argument;
	
	/** The options. */
	private final ArgumentSetOptions<?, ?> options;
	
	/** The message. */
	private final String                   message;
	
	/**
	 * Instantiates a new argument set registration exception.
	 * 
	 * @param message
	 *            the message
	 * @param argument
	 *            the argument
	 * @param options
	 *            the options
	 */
	public ArgumentSetRegistrationException(@NotNull final String message, final ArgumentSet<?, ?> argument,
	        final ArgumentSetOptions<?, ?> options) {
		super(message);
		this.message = message + ": " + (argument != null
		                                                 ? argument.getTag()
		                                                 : options != null
		                                                                  ? options.getTag()
		                                                                  : "");
		
		this.argument = argument;
		this.options = options;
	}
	
	/**
	 * Instantiates a new argument registration exception.
	 * 
	 * @param message
	 *            the message
	 * @param argument
	 *            the argument
	 * @param options
	 *            the options
	 * @param t
	 *            the t
	 */
	public ArgumentSetRegistrationException(@NotNull final String message, final ArgumentSet<?, ?> argument,
	        final ArgumentSetOptions<?, ?> options, final Throwable t) {
		super(message, t);
		this.message = message + ": " + (argument != null
		                                                 ? argument.getTag()
		                                                 : options != null
		                                                                  ? options.getTag()
		                                                                  : "");
		this.argument = argument;
		this.options = options;
	}
	
	/**
	 * Gets the argument.
	 * 
	 * @return the argument
	 */
	public final ArgumentSet<?, ?> getArgumentSet() {
		return this.argument;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		// PRECONDITIONS
		
		try {
			final StringBuilder builder = new StringBuilder();
			builder.append(this.message);
			
			if (this.argument != null) {
				builder.append(FileUtils.lineSeparator).append("ArgumentSet: ").append(this.argument); //$NON-NLS-1$
			}
			
			if (this.options != null) {
				builder.append(FileUtils.lineSeparator).append("Options: ").append(this.options); //$NON-NLS-1$
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the options.
	 * 
	 * @return the options
	 */
	public final IArgumentSetOptions<?, ?> getOptions() {
		return this.options;
	}
	
}

/**
 * 
 */
package net.ownhero.dev.hiari.settings.exceptions;

import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.IArgumentOptions;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * The Class ArgumentRegistrationException.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ArgumentRegistrationException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long            serialVersionUID = 1258725690431585182L;
	
	/** The argument. */
	private final IArgument<?, ?>        argument;
	
	/** The options. */
	private final IArgumentOptions<?, ?> options;
	
	private final String                 message;
	
	/**
	 * Instantiates a new argument registration exception.
	 * 
	 * @param message
	 *            the message
	 * @param argument
	 *            the argument
	 * @param options
	 *            the options
	 */
	public ArgumentRegistrationException(@NotNull final String message, final IArgument<?, ?> argument,
	        final IArgumentOptions<?, ?> options) {
		super(message + ": " + argument.getTag());
		
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
	 * @param cause
	 *            the cause
	 */
	public ArgumentRegistrationException(@NotNull final String message, final IArgument<?, ?> argument,
	        final IArgumentOptions<?, ?> options, @NotNull final Throwable cause) {
		super(message + ": " + argument.getTag(), cause);
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
	public final IArgument<?, ?> getArgumentSet() {
		return this.argument;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		// PRECONDITIONS
		
		// PRECONDITIONS
		
		try {
			final StringBuilder builder = new StringBuilder();
			builder.append(this.message);
			
			if (this.argument != null) {
				builder.append(FileUtils.lineSeparator).append("Argument: ").append(this.argument); //$NON-NLS-1$
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
	public final IArgumentOptions<?, ?> getOptions() {
		return this.options;
	}
}

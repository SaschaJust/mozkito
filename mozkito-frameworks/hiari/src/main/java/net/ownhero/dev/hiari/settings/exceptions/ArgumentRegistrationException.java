/**
 * 
 */
package net.ownhero.dev.hiari.settings.exceptions;

import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.IArgumentOptions;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
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
		super(message);
		
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
	@NoneNull
	public ArgumentRegistrationException(@NotNull final String message, final IArgument<?, ?> argument,
	        final IArgumentOptions<?, ?> options, @NotNull final Throwable cause) {
		super(message, cause);
		
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
	
	/**
	 * Gets the options.
	 * 
	 * @return the options
	 */
	public final IArgumentOptions<?, ?> getOptions() {
		return this.options;
	}
	
}

/**
 * 
 */
package net.ownhero.dev.hiari.settings.exceptions;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IArgumentSetOptions;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

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
	
	/**
	 * Instantiates a new argument set registration exception.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param <X>
	 *            the generic type
	 * @param message
	 *            the message
	 * @param argument
	 *            the argument
	 * @param options
	 *            the options
	 * @param configurator
	 *            the configurator
	 */
	public ArgumentSetRegistrationException(@NotNull final String message, final ArgumentSet<?, ?> argument,
	        final ArgumentSetOptions<?, ?> options) {
		super(message);
		
		this.argument = argument;
		this.options = options;
	}
	
	/**
	 * Instantiates a new argument registration exception.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param <X>
	 *            the generic type
	 * @param message
	 *            the message
	 * @param argument
	 *            the argument
	 * @param options
	 *            the options
	 * @param configurator
	 *            the configurator
	 * @param t
	 *            the t
	 */
	public ArgumentSetRegistrationException(@NotNull final String message, final ArgumentSet<?, ?> argument,
	        final ArgumentSetOptions<?, ?> options, final Throwable t) {
		super(message, t);
		
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
	
	/**
	 * Gets the options.
	 * 
	 * @return the options
	 */
	public final IArgumentSetOptions<?, ?> getOptions() {
		return this.options;
	}
	
}

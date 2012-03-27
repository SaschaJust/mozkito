/**
 * 
 */
package net.ownhero.dev.hiari.settings.exceptions;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IArgumentSetOptions;
import net.ownhero.dev.ioda.FileUtils;
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
	
	private final String                   message;
	
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
		this.message = message + ": " + argument.getTag();
		
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
		this.message = message + ": " + argument.getTag();
		
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
			
			if (this.options != null) {
				builder.append(FileUtils.lineSeparator).append("ArgumentSet: ").append(this.argument); //$NON-NLS-1$
			}
			
			if (this.options != null) {
				builder.append(FileUtils.lineSeparator).append("Options: ").append(this.options); //$NON-NLS-1$
			}
			
			return super.toString();
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

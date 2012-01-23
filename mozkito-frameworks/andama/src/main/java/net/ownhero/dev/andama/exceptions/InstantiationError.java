/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.lang.reflect.Constructor;

import net.ownhero.dev.andama.utils.AndamaUtils;

/**
 * @author just
 * 
 */
public class InstantiationError extends UnrecoverableError {
	
	private static final long    serialVersionUID = 7635237323775156093L;
	private static final String  defaultMessage   = "";
	private final Class<?>       clazz;
	private final Constructor<?> constructor;
	private final Object[]       arguments;
	
	/**
	 * @param cause
	 * @param clazz
	 * @param constructor
	 * @param arguments
	 */
	public InstantiationError(final InstantiationException cause, final Class<?> clazz,
	        final Constructor<?> constructor, final Object... arguments) {
		this(defaultMessage, cause, clazz, constructor, arguments);
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param clazz
	 * @param constructor
	 * @param arguments
	 */
	public InstantiationError(final String message, final InstantiationException cause, final Class<?> clazz,
	        final Constructor<?> constructor, final Object... arguments) {
		super(message, cause);
		this.clazz = clazz;
		this.constructor = constructor;
		this.arguments = arguments;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.exceptions.UnrecoverableError#analyzeFailureCause
	 * ()
	 */
	@Override
	public String analyzeFailureCause() {
		final StringBuilder builder = new StringBuilder();
		
		return builder.append(AndamaUtils.lineSeparator).toString();
	}
	
	/**
	 * @return
	 */
	public Object[] getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}
	
	/**
	 * @return
	 */
	public Constructor<?> getConstructor() {
		return this.constructor;
	}
}

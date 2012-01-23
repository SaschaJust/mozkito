/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

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
	static final private int     contextSize      = 3;
	
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
		
		final Throwable cause = getCause();
		cause.printStackTrace();
		
		if ((getClazz().getModifiers() & Modifier.ABSTRACT) != 0) {
			builder.append("The class is abstract.");
		} else if (getClazz().isInterface()) {
			builder.append("The class is an interface.");
		} else if (getClazz().isAnnotation()) {
			builder.append("The class is an annotation.");
		} else if (getClazz().isArray()) {
			builder.append("The class is an array.");
		} else if (getClazz().isPrimitive()) {
			builder.append("The class is a primitive.");
		} else {
			
			if (cause.getCause() != null) {
				final int lineNumber = 10;
				if (getConstructor() == null) {
					
					builder.append("The instantiation of the class failed within the default constructor. ");
					builder.append("The error was caused by an exception (");
					builder.append(cause.getCause().getClass().getCanonicalName())
					       .append(") in the default constructor in line ");
					builder.append(lineNumber).append(".");
					
				} else {
					builder.append("The constructor used to instantiate the class does not match the given arguments: ");
				}
				final File file = new File(".");
				builder.append(getSourceCode(file, lineNumber, contextSize));
			} else {
				if (getConstructor() == null) {
					builder.append("The class does not have a default constructor");
				} else {
					builder.append("The constructor used to instantiate the class does not match the given arguments: ");
					this.constructor.getParameterTypes();
					
				}
			}
			
		}
		
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

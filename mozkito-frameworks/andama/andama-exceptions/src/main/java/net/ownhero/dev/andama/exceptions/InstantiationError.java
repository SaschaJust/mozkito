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
package net.ownhero.dev.andama.exceptions;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import net.ownhero.dev.ioda.FileUtils;

/**
 * The Class InstantiationError.
 * 
 * @author just
 */
public class InstantiationError extends UnrecoverableError {
	
	/** The Constant serialVersionUID. */
	private static final long    serialVersionUID = 7635237323775156093L;
	
	/** The Constant defaultMessage. */
	private static final String  defaultMessage   = "";
	
	/** The clazz. */
	private final Class<?>       clazz;
	
	/** The constructor. */
	private final Constructor<?> constructor;
	
	/** The arguments. */
	private final Object[]       arguments;
	
	/** The Constant contextSize. */
	static final private int     contextSize      = 3;
	
	/**
	 * Instantiates a new instantiation error.
	 * 
	 * @param cause
	 *            the cause
	 * @param clazz
	 *            the clazz
	 * @param constructor
	 *            the constructor
	 * @param arguments
	 *            the arguments
	 */
	public InstantiationError(final IllegalArgumentException cause, final Class<?> clazz,
	        final Constructor<?> constructor, final Object... arguments) {
		this(defaultMessage, cause, clazz, constructor, arguments);
	}
	
	/**
	 * Instantiates a new instantiation error.
	 * 
	 * @param cause
	 *            the cause
	 * @param clazz
	 *            the clazz
	 * @param constructor
	 *            the constructor
	 * @param arguments
	 *            the arguments
	 */
	public InstantiationError(final InstantiationException cause, final Class<?> clazz,
	        final Constructor<?> constructor, final Object... arguments) {
		this(defaultMessage, cause, clazz, constructor, arguments);
	}
	
	/**
	 * Instantiates a new instantiation error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param clazz
	 *            the clazz
	 * @param constructor
	 *            the constructor
	 * @param arguments
	 *            the arguments
	 */
	public InstantiationError(final String message, final IllegalArgumentException cause, final Class<?> clazz,
	        final Constructor<?> constructor, final Object... arguments) {
		super(message, cause);
		this.clazz = clazz;
		this.constructor = constructor;
		this.arguments = arguments;
	}
	
	/**
	 * Instantiates a new instantiation error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param clazz
	 *            the clazz
	 * @param constructor
	 *            the constructor
	 * @param arguments
	 *            the arguments
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
	 * @see net.ownhero.dev.andama.exceptions.UnrecoverableError#analyzeFailureCause ()
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
					builder.append("The constructor used to instantiate the class does not match the given arguments: ")
					       .append(FileUtils.lineSeparator);
					builder.append("Valid: ").append(getConstructorString(this.constructor))
					       .append(FileUtils.lineSeparator);
					builder.append("Used:  ")
					       .append(getConstructorString(this.constructor.getName(),
					                                    getModifierString(this.constructor.getModifiers()),
					                                    getArguments())).append(FileUtils.lineSeparator);
				}
			}
			
		}
		
		return builder.append(FileUtils.lineSeparator).toString();
	}
	
	/**
	 * Gets the arguments.
	 * 
	 * @return the arguments
	 */
	public Object[] getArguments() {
		return this.arguments;
	}
	
	/**
	 * Gets the clazz.
	 * 
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}
	
	/**
	 * Gets the constructor.
	 * 
	 * @return the constructor
	 */
	public Constructor<?> getConstructor() {
		return this.constructor;
	}
}

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.mozkito.utilities.io.FileUtils;

/**
 * The Class NoSuchConstructorError.
 * 
 * @author just
 */
public class NoSuchConstructorError extends UnrecoverableError {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8912323102598389733L;
	
	/** The default message. */
	static private String     defaultMessage   = "";
	
	/** The arguments. */
	private final Class<?>[]  arguments;
	
	/** The clazz. */
	private final Class<?>    clazz;
	
	/**
	 * Instantiates a new no such constructor error.
	 * 
	 * @param cause
	 *            the cause
	 * @param clazz
	 *            the clazz
	 * @param arguments
	 *            the arguments
	 */
	public NoSuchConstructorError(final NoSuchMethodException cause, final Class<?> clazz, final Class<?>... arguments) {
		this(defaultMessage, cause, clazz, arguments);
	}
	
	/**
	 * Instantiates a new no such constructor error.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param clazz
	 *            the clazz
	 * @param arguments
	 *            the arguments
	 */
	public NoSuchConstructorError(final String message, final NoSuchMethodException cause, final Class<?> clazz,
	        final Class<?>... arguments) {
		super(message, cause);
		this.clazz = clazz;
		this.arguments = arguments;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.exceptions.UnrecoverableError#analyzeFailureCause ()
	 */
	@Override
	public String analyzeFailureCause() {
		final StringBuilder builder = new StringBuilder();
		builder.append("The class ").append(getClazz().getCanonicalName()).append(" does not have a constructor: ")
		       .append(getClazz().getSimpleName()).append('(');
		
		builder.append(')').append(FileUtils.lineSeparator);
		if ((getClazz().getModifiers() & Modifier.ABSTRACT) != 0) {
			builder.append("The class is abstract.");
		} else if (getClazz().isInterface()) {
			builder.append("The class is an interface.");
		} else if (getClazz().isAnnotation()) {
			builder.append("The class is an annotation.");
		} else {
			for (final Constructor<?> constructor : getClazz().getConstructors()) {
				final int modifiers = constructor.getModifiers();
				builder.append("   ");
				
				builder.append(getModifierString(modifiers));
				
				builder.append(getClazz().getSimpleName()).append('(');
				StringBuilder sb = new StringBuilder();
				
				for (final Type type : constructor.getGenericParameterTypes()) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(type);
				}
				builder.append(sb);
				builder.append(")");
				sb = new StringBuilder();
				for (final Type exceptionType : constructor.getGenericExceptionTypes()) {
					if (sb.length() > 0) {
						sb.append(", ");
					} else {
						sb.append(" throws ");
					}
					sb.append(exceptionType);
				}
				builder.append(sb);
				
				builder.append(';').append(FileUtils.lineSeparator);
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
	
}

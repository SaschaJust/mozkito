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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import net.ownhero.dev.ioda.FileUtils;

/**
 * The Class UnrecoverableError.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class UnrecoverableError extends Error {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8156028538555027087L;
	
	/**
	 * Format.
	 * 
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the unrecoverable error
	 */
	public static UnrecoverableError format(final String formatString,
	                                        final Object... args) {
		return new UnrecoverableError(String.format(formatString, args));
	}
	
	/**
	 * Format.
	 * 
	 * @param t
	 *            the t
	 * @param formatString
	 *            the format string
	 * @param args
	 *            the args
	 * @return the unrecoverable error
	 */
	public static UnrecoverableError format(final Throwable t,
	                                        final String formatString,
	                                        final Object... args) {
		return new UnrecoverableError(String.format(formatString, args), t);
	}
	
	/**
	 * Instantiates a new unrecoverable error.
	 */
	public UnrecoverableError() {
		super();
	}
	
	/**
	 * Instantiates a new unrecoverable error.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public UnrecoverableError(final String arg0) {
		super(arg0);
	}
	
	/**
	 * Instantiates a new unrecoverable error.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public UnrecoverableError(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}
	
	/**
	 * Instantiates a new unrecoverable error.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public UnrecoverableError(final Throwable arg0) {
		super(arg0);
	}
	
	/**
	 * Analyze failure cause.
	 * 
	 * @return the string
	 */
	public String analyzeFailureCause() {
		return null;
	}
	
	/**
	 * Gets the argument string.
	 * 
	 * @param types
	 *            the types
	 * @return the argument string
	 */
	protected String getArgumentString(final Class<?>[] types) {
		final StringBuilder builder = new StringBuilder();
		
		for (final Class<?> clazz : types) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(clazz.getCanonicalName());
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the argument string.
	 * 
	 * @param types
	 *            the types
	 * @return the argument string
	 */
	protected String getArgumentString(final Object[] types) {
		final StringBuilder builder = new StringBuilder();
		
		for (final Object object : types) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(object.getClass().getCanonicalName());
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the argument string.
	 * 
	 * @param types
	 *            the types
	 * @return the argument string
	 */
	protected String getArgumentString(final Type[] types) {
		final StringBuilder builder = new StringBuilder();
		
		for (final Type type : types) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(type);
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the constructor string.
	 * 
	 * @param constructor
	 *            the constructor
	 * @return the constructor string
	 */
	protected String getConstructorString(final Constructor<?> constructor) {
		final StringBuilder builder = new StringBuilder();
		
		final String modifier = getModifierString(constructor.getModifiers());
		if (modifier.length() > 0) {
			builder.append(modifier).append(" ");
		}
		
		builder.append(constructor.getName()).append('(');
		final String arguments = getArgumentString(constructor.getGenericParameterTypes());
		builder.append(arguments).append(')');
		
		final String exceptions = getArgumentString(constructor.getGenericExceptionTypes());
		if (exceptions.length() > 0) {
			builder.append(" throws ").append(exceptions);
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the constructor string.
	 * 
	 * @param name
	 *            the name
	 * @param modifier
	 *            the modifier
	 * @param arguments
	 *            the arguments
	 * @return the constructor string
	 */
	protected String getConstructorString(final String name,
	                                      final String modifier,
	                                      final Object[] arguments) {
		final StringBuilder builder = new StringBuilder();
		
		if (modifier.length() > 0) {
			builder.append(modifier).append(" ");
		}
		
		builder.append(name).append('(');
		final String argumentString = getArgumentString(arguments);
		builder.append(argumentString).append(')');
		
		return builder.toString();
	}
	
	/**
	 * Gets the modifier string.
	 * 
	 * @param modifiers
	 *            the modifiers
	 * @return the modifier string
	 */
	protected String getModifierString(final int modifiers) {
		final StringBuilder builder = new StringBuilder();
		
		if ((modifiers & Modifier.PRIVATE) != 0) {
			builder.append("private ");
		} else if ((modifiers & Modifier.PUBLIC) != 0) {
			builder.append("public ");
		} else if ((modifiers & Modifier.PROTECTED) != 0) {
			builder.append("static ");
		}
		
		if ((modifiers & Modifier.STATIC) != 0) {
			builder.append("static ");
		}
		
		if ((modifiers & Modifier.FINAL) != 0) {
			builder.append("final ");
		}
		
		if ((modifiers & Modifier.ABSTRACT) != 0) {
			builder.append("abstract ");
		}
		
		if ((modifiers & Modifier.SYNCHRONIZED) != 0) {
			builder.append("synchronized ");
		}
		
		if ((modifiers & Modifier.VOLATILE) != 0) {
			builder.append("volatile ");
		}
		
		if ((modifiers & Modifier.TRANSIENT) != 0) {
			builder.append("transient ");
		}
		
		if ((modifiers & Modifier.NATIVE) != 0) {
			builder.append("native ");
		}
		
		return builder.toString();
	}
	
	/**
	 * Gets the source code.
	 * 
	 * @param file
	 *            the file
	 * @param lineNumber
	 *            the line number
	 * @param contextSize
	 *            the context size
	 * @return the source code
	 */
	protected String getSourceCode(final File file,
	                               final int lineNumber,
	                               final int contextSize) {
		final StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			
			builder.append("Source code: ").append(FileUtils.lineSeparator);
			
			try {
				int line = 1;
				String theLine = null;
				while ((line < (lineNumber - contextSize)) && ((theLine = reader.readLine()) != null)) {
					reader.readLine();
					++line;
				}
				
				final int charLength = (int) Math.log10(lineNumber + contextSize) + 1;
				
				while ((line <= (lineNumber + contextSize)) && ((theLine = reader.readLine()) != null)) {
					++line;
					builder.append(String.format(" %-" + charLength + "s:  ", line));
					builder.append(theLine);
					builder.append(FileUtils.lineSeparator);
				}
				
			} catch (final IOException e) {
				builder.append("Source code providing failed while reading from file: ")
				       .append(file != null
				                           ? file.getAbsolutePath()
				                           : "(null)").append(FileUtils.lineSeparator);
			}
		} catch (final FileNotFoundException ignore) { // ignore
		} catch (final IOException ignore) {
			// ignore
		}
		
		return builder.toString();
	}
	
}

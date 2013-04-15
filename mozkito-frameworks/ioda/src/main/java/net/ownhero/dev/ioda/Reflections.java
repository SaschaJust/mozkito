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

package net.ownhero.dev.ioda;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class Reflections.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Reflections {
	
	/**
	 * The Class ContractViolation.
	 */
	public static final class ContractViolation extends RuntimeException {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -8775291509872636200L;
		
		/**
		 * Instantiates a new contract violation.
		 */
		public ContractViolation() {
			super();
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Instantiates a new contract violation.
		 * 
		 * @param message
		 *            the message
		 */
		public ContractViolation(final String message) {
			super(message);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Instantiates a new contract violation.
		 * 
		 * @param message
		 *            the message
		 * @param cause
		 *            the cause
		 */
		public ContractViolation(final String message, final Throwable cause) {
			super(message, cause);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Instantiates a new contract violation.
		 * 
		 * @param cause
		 *            the cause
		 */
		public ContractViolation(final Throwable cause) {
			super(cause);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
	}
	
	/**
	 * The Class InstantianException.
	 */
	public static final class InstantianException extends Exception {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 5128744548288997018L;
		
		/**
		 * Instantiates a new instantian exception.
		 */
		public InstantianException() {
			super();
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Instantiates a new instantian exception.
		 * 
		 * @param message
		 *            the message
		 */
		public InstantianException(final String message) {
			super(message);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Instantiates a new instantian exception.
		 * 
		 * @param message
		 *            the message
		 * @param cause
		 *            the cause
		 */
		public InstantianException(final String message, final Throwable cause) {
			super(message, cause);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Instantiates a new instantian exception.
		 * 
		 * @param cause
		 *            the cause
		 */
		public InstantianException(final Throwable cause) {
			super(cause);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
	}
	
	/** The Constant ls. */
	private static final String ls = System.getProperty("line.separator");
	
	/**
	 * Gets the modifiers in the order that conforms to the suggestions in the Java Language specification, sections
	 * 8.1.1, 8.3.1 and 8.4.3. The correct order is:
	 * 
	 * public protected private abstract static final transient volatile synchronized native strictfp
	 * 
	 * @param modifiers
	 *            the modifiers
	 * @return the modifier
	 */
	public static String getModifierString(final int modifiers) {
		final StringBuilder builder = new StringBuilder();
		
		if ((modifiers & Modifier.PUBLIC) != 0) {
			spaceAppend(builder, "public");
		} else if ((modifiers & Modifier.PROTECTED) != 0) {
			spaceAppend(builder, "protected");
		} else if ((modifiers & Modifier.PRIVATE) != 0) {
			spaceAppend(builder, "private");
		}
		
		if ((modifiers & Modifier.ABSTRACT) != 0) {
			spaceAppend(builder, "abstract");
		} else if ((modifiers & Modifier.STATIC) != 0) {
			spaceAppend(builder, "static");
		}
		
		if ((modifiers & Modifier.FINAL) != 0) {
			spaceAppend(builder, "final");
		}
		
		if ((modifiers & Modifier.TRANSIENT) != 0) {
			spaceAppend(builder, "transient");
		}
		
		if ((modifiers & Modifier.VOLATILE) != 0) {
			spaceAppend(builder, "volatile");
		}
		
		if ((modifiers & Modifier.SYNCHRONIZED) != 0) {
			spaceAppend(builder, "synchronized");
		}
		
		if ((modifiers & Modifier.NATIVE) != 0) {
			spaceAppend(builder, "native");
		}
		
		if ((modifiers & Modifier.STRICT) != 0) {
			spaceAppend(builder, "strict");
		}
		
		return builder.toString();
	}
	
	/**
	 * Save instantiate.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param parameterTypes
	 *            the parameter types
	 * @param arguments
	 *            the arguments
	 * @return the t
	 * @throws InstantianException
	 *             the instantian exception
	 */
	public static <T> T saveInstantiate(@NotNull final Class<? extends T> clazz,
	                                    @NotNull final Class<?>[] parameterTypes,
	                                    @NotNull final Object[] arguments) throws InstantianException {
		PRECONDITIONS: {
			if (clazz == null) {
				throw new InstantianException(new NullPointerException("Argument 'clazz' may not be null."));
			}
			
			if ((clazz.getModifiers() & Modifier.INTERFACE) != 0) {
				throw new InstantianException(
				                              new IllegalArgumentException(
				                                                           "The parameter 'clazz' refers to an interface."));
			} else if (clazz == void.class) {
				throw new InstantianException(new IllegalArgumentException("The parameter 'clazz' refers to void."));
			} else if (clazz.isArray()) {
				throw new InstantianException(
				                              new IllegalArgumentException(
				                                                           "The parameter 'clazz' refers to an array class."));
			} else if (clazz.isPrimitive()) {
				throw new InstantianException(
				                              new IllegalArgumentException(
				                                                           "The parameter 'clazz' refers to a primitive."));
			} else if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
				throw new InstantianException(
				                              new IllegalArgumentException(
				                                                           "The parameter 'clazz' refers to an abstract class."));
			}
			
			if (parameterTypes == null) {
				throw new InstantianException(new NullPointerException("Argument 'parameterTypes' may not be null."));
			}
			
			if (arguments == null) {
				throw new InstantianException(new NullPointerException("Argument 'arguments' may not be null."));
			}
			
			if (parameterTypes.length != arguments.length) {
				throw new InstantianException(new IndexOutOfBoundsException("The length (" + parameterTypes.length
				        + ") of the parameter 'parameterTypes' must match the length (" + arguments.length
				        + ") of the parameter 'arguments'."));
			}
			
			for (int i = 0; i < arguments.length; ++i) {
				if (parameterTypes[i].isPrimitive()) {
					if (arguments[i] == null) {
						throw new InstantianException(
						                              new IllegalArgumentException(
						                                                           "The element with index '"
						                                                                   + i
						                                                                   + "' of the parameter 'arguments' is null and thus does not conform with the expected primitive type '"
						                                                                   + parameterTypes[i].getCanonicalName()
						                                                                   + "'."));
					} else {
						if (parameterTypes[i] == Integer.TYPE) {
							if (!Integer.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Long.TYPE) {
							if (!Long.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Float.TYPE) {
							if (!Float.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Double.TYPE) {
							if (!Double.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Character.TYPE) {
							if (!Character.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Byte.TYPE) {
							if (!Byte.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Short.TYPE) {
							if (!Short.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						} else if (parameterTypes[i] == Boolean.TYPE) {
							if (!Boolean.class.isAssignableFrom(arguments[i].getClass())) {
								throw new InstantianException(new IllegalArgumentException("The element with index '"
								        + i + "' of the parameter 'arguments' (which is of type '"
								        + arguments[i].getClass().getCanonicalName()
								        + "') is not conform with the expected type '"
								        + parameterTypes[i].getCanonicalName() + "'."));
							}
						}
					}
				} else if ((arguments[i] != null) && !parameterTypes[i].isAssignableFrom(arguments[i].getClass())) {
					throw new InstantianException(new IllegalArgumentException("The element with index '" + i
					        + "' of the parameter 'arguments' (which is of type '"
					        + arguments[i].getClass().getCanonicalName() + "') is not conform with the expected type '"
					        + parameterTypes[i].getCanonicalName() + "'."));
				}
			}
		}
		
		T object = null;
		
		Constructor<? extends T> objectConstructor = null;
		String objectConstructorStringRepresentation;
		
		SANITY: {
			assert clazz != null;
		}
		
		try {
			objectConstructor = clazz.getDeclaredConstructor(parameterTypes);
			objectConstructorStringRepresentation = objectConstructor.toString();
			if (!objectConstructor.isAccessible()) {
				if (Logger.logDebug()) {
					Logger.debug("Changing constructors %s accessibility.", objectConstructorStringRepresentation);
				}
				objectConstructor.setAccessible(true);
			}
		} catch (final NoSuchMethodException e) {
			// handle error
			final StringBuilder builder = new StringBuilder();
			
			// check who called us
			final StackTraceElement traceElement = new Throwable().getStackTrace()[1];
			
			builder.append("Tried to look-up constructor of '").append(clazz.getCanonicalName())
			       .append("' with parameter types ").append(JavaUtils.arrayToString(parameterTypes))
			       .append(", but wasn't able to do so.").append(ls).append("Please fix '")
			       .append(traceElement.getClassName()).append(':').append(traceElement.getMethodName()).append("#")
			       .append(traceElement.getLineNumber()).append("' with one of the following alternatives: ");
			
			@SuppressWarnings ("unchecked")
			final Constructor<? extends T>[] constructors = (Constructor<? extends T>[]) clazz.getDeclaredConstructors();
			builder.append("Available constructors: ");
			
			for (final Constructor<? extends T> constructor : constructors) {
				builder.append(ls).append("- ")
				       .append(constructor.getAnnotation(Deprecated.class) != null
				                                                                  ? "@deprecated "
				                                                                  : "").append(constructor);
			}
			
			throw new InstantianException(builder.toString());
		}
		
		try {
			// create the repository instance
			assert objectConstructor != null;
			object = objectConstructor.newInstance(arguments);
			assert object != null;
			return object;
		} catch (final InstantiationException e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Instantiation of '"
			        + objectConstructorStringRepresentation
			        + "' failed due to an InstantiationException. This should never happen since all scenarios that cause this exception should have already been handled (the exception can occur, if: 1. the class object represents an abstract class, an interface, an array class, a primitive type, or void, 2. the class has no nullary constructor). The 'saveInstantiate' method needs to be fixed.");
			throw new InstantianException(builder.toString());
		} catch (final IllegalArgumentException e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Instantiation of '"
			        + objectConstructorStringRepresentation
			        + "' failed due to an IllegalArgumentException. This should never happen since all scenarios that cause this exception should have already been handled (i.e. checking that the actual parameters match the signature). The 'saveInstantiate' method needs to be fixed.");
			throw new InstantianException(builder.toString());
		} catch (final SecurityException e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Instantiation of '").append(objectConstructor)
			       .append("' failed due intervention from the security manager. See nested exceptions for details.");
			
			throw new InstantianException(builder.toString(), e);
		} catch (final InvocationTargetException e) {
			final StringBuilder builder = new StringBuilder();
			assert objectConstructor != null;
			// get cause first
			final Throwable cause = e.getCause();
			assert cause != null;
			
			// check for kanuni exceptions
			if (cause instanceof ContractViolation) {
				// kanuni precondition violated
				// TODO change the ContractViolation to the actual kanuni annotation when upgrading to kanuni 1.0
				builder.append("Instantiation violated a kanuni contract. ");
			}
			
			assert e.getCause() != null;
			assert e.getCause().getStackTrace() != null;
			assert e.getCause().getStackTrace().length > 0;
			final String throwingMethodName = e.getCause().getStackTrace()[0].getMethodName();
			final String throwingClassName = e.getCause().getStackTrace()[0].getClassName().replace('$', '.');
			final String clazzName = clazz.getCanonicalName();
			
			if ("<init>".equals(throwingMethodName) && clazzName.equals(throwingClassName)) {
				builder.append("Instantiation failed probably to invalid arguments. The constructor itself threw: "
				        + cause.getClass().getCanonicalName());
			}
			
			// else
			if (builder.length() == 0) {
				builder.append("Instantiation of '")
				       .append(objectConstructor.toString())
				       .append("' failed. This might result from a bug in this constructor. See nested exceptions for details.");
			}
			
			throw new InstantianException(builder.toString(), e.getCause());
		} catch (final IllegalAccessException e) {
			assert objectConstructor.isAccessible() : "We set accessible flag earlier.";
			
			final StringBuilder builder = new StringBuilder();
			builder.append("Instantiation of '")
			       .append(objectConstructorStringRepresentation)
			       .append("' failed. This shouldn't be the case, since accessibility has been set before instantiation. See nested exceptions for details.");
			
			throw new InstantianException(builder.toString(), e);
		}
	}
	
	/**
	 * Space append.
	 * 
	 * @param builder
	 *            the builder
	 * @param string
	 *            the string
	 * @return the string builder
	 */
	private static StringBuilder spaceAppend(final StringBuilder builder,
	                                         final String string) {
		assert builder != null;
		assert string != null;
		
		if (builder.length() > 0) {
			builder.append(' ');
		}
		
		builder.append(string);
		return builder;
	}
	
	/**
	 * Instantiates a new reflections.
	 */
	private Reflections() {
		// avoid instantiation
		throw new InstantiationError("This class is static.");
	}
}

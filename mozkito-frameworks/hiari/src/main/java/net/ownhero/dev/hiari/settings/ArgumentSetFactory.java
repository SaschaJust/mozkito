/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.hiari.settings;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kisa.Logger;

/**
 * A factory for creating Argument objects.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ArgumentSetFactory {
	
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	private static <T, Y extends ArgumentSet<T, X>, X extends ArgumentSetOptions<T, Y>> Y create(final Object option) throws SettingsParseError,
	                                                                                                                 ArgumentSetRegistrationException,
	                                                                                                                 ArgumentRegistrationException {
		boolean initialize = true;
		final ArgumentSetOptions options = (ArgumentSetOptions) option;
		
		// skip initialization if 'help' is set
		if (options.getArgumentSet().getSettings().getProperty("help") != null) {
			initialize = false;
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Creation of " + options + " requested.");
		}
		// add this argumentSet to the settings help table
		options.getArgumentSet().getSettings().addOption(options);
		
		final Class<Y> clazz = (Class<Y>) getTypeArguments(ArgumentSetOptions.class, options.getClass()).get(1);
		
		Constructor<Y> constructor = null;
		try {
			final Constructor<Y>[] constructors = (Constructor<Y>[]) clazz.getDeclaredConstructors();
			for (final Constructor<Y> c : constructors) {
				if (Logger.logTrace()) {
					Logger.trace("Checking for valid constructor: " + c.getName());
				}
				
				if ((c.getParameterTypes().length == 1)
				        && c.getParameterTypes()[0].isAssignableFrom(options.getClass())) {
					constructor = c;
					if (Logger.logTrace()) {
						Logger.trace("Contructor " + c + " is valid.");
					}
					break;
				} else {
					if (Logger.logTrace()) {
						Logger.trace("Contructor " + c + " is invalid.");
					}
				}
			}
			
			if (constructor == null) {
				if (Logger.logTrace()) {
					Logger.trace(String.format("No valid constructor found in '%s' for '%s'.",
					                           clazz.getCanonicalName(), options.getHandle()));
				}
				
				throw new SettingsParseError(String.format("Couldn't find a valid constructor in '%s' for '%s'.",
				                                           clazz.getCanonicalName(), options.getHandle()));
			}
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Instantiating '%s' with '%s'.", clazz.getCanonicalName(),
				                           options.getHandle()));
			}
			
			final Y argument = constructor.newInstance(options);
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Requesting requirements for '%s'.", argument));
			}
			
			final Map<String, IOptions<?, ?>> requirementsOptions = options.requirements(argument);
			
			if (Logger.logTrace()) {
				Logger.trace(String.format("Found the following requirements for '%s'.", argument));
				for (final String key : requirementsOptions.keySet()) {
					Logger.trace(String.format("Requires: %s", requirementsOptions.get(key)));
				}
			}
			
			final Map<String, IArgument<?, ?>> requirements = new HashMap<String, IArgument<?, ?>>();
			
			for (final String key : requirementsOptions.keySet()) {
				final IOptions<?, ?> iOptions = requirementsOptions.get(key);
				
				if (Logger.logTrace()) {
					Logger.trace(String.format("Checking '%s'.", iOptions));
				}
				
				if (options.getArgumentSet().getSettings().hasSetting(key)) {
					if (Logger.logTrace()) {
						Logger.trace(String.format("Required IArgument with tag '%s' already present. Skipping initialization.",
						                           iOptions.getTag()));
					}
				} else {
					if (ArgumentOptions.class.isAssignableFrom(iOptions.getClass())) {
						final ArgumentOptions<?, ? extends Argument> ao = (ArgumentOptions<?, ? extends Argument>) iOptions;
						if (Logger.logTrace()) {
							Logger.trace(String.format("Required Argument with tag '%s' not present. Calling factory to create it with options: %s",
							                           iOptions.getTag(), iOptions));
						}
						final Argument create = ArgumentFactory.create(ao);
						
						if (Logger.logTrace()) {
							Logger.trace(String.format("Creation of Argument with tag '%s' successful.",
							                           iOptions.getTag()));
						}
						requirements.put(key, create);
					} else if (ArgumentSetOptions.class.isAssignableFrom(iOptions.getClass())) {
						final ArgumentSetOptions<?, ? extends ArgumentSet> ao = (ArgumentSetOptions<?, ? extends ArgumentSet>) iOptions;
						
						if (Logger.logTrace()) {
							Logger.trace(String.format("Required ArgumentSet with tag '%s' not present. Calling factory to create it with options: %s",
							                           iOptions.getTag(), iOptions));
						}
						
						final ArgumentSet create = ArgumentSetFactory.create(ao);
						
						if (Logger.logTrace()) {
							Logger.trace(String.format("Creation of Argument with tag '%s' successful.",
							                           iOptions.getTag()));
						}
						requirements.put(key, create);
					} else {
						if (Logger.logError()) {
							Logger.error("TODO: Some implemented an option that implements IOptions but is not of type ArgumentOption or ArgumentSetOption. This requires this method to be fixed.");
						}
					}
				}
			}
			
			if (Logger.logTrace()) {
				Logger.trace("Checking if \"help\" mode is enabled.");
			}
			if (initialize) {
				if (Logger.logTrace()) {
					Logger.trace(String.format("Help mode disabled. Initializing the ArgumentSet with options: %s",
					                           options));
				}
				
				final T value = (T) options.init();
				
				if (value != null) {
					if (Logger.logTrace()) {
						Logger.trace(String.format("Initialization of '%s' was successful and yielded '%s'.", argument,
						                           value));
					}
					argument.setCachedValue(value);
					
					if (Logger.logTrace()) {
						Logger.trace("Set the cached value of the argument to the return value of the initialization.");
					}
				} else {
					throw new ArgumentSetRegistrationException("", argument, options);
				}
				
				return argument;
			} else {
				if (Logger.logTrace()) {
					Logger.trace("Help mode enabled. Skipping initialization of the ArgumentSet and returning null.");
				}
				
				return null;
			}
		} catch (final IllegalArgumentException e) {
			throw new ArgumentSetRegistrationException(
			                                           String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
			                                                         clazz.getSimpleName(), options, e.getMessage()),
			                                           null, options, e);
		} catch (final SecurityException e) {
			throw new ArgumentSetRegistrationException(
			                                           String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
			                                                         clazz.getSimpleName(), options, e.getMessage()),
			                                           null, options, e);
			
		} catch (final InstantiationException e) {
			throw new ArgumentSetRegistrationException(
			                                           String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
			                                                         clazz.getSimpleName(), options, e.getMessage()),
			                                           null, options, e);
			
		} catch (final IllegalAccessException e) {
			throw new ArgumentSetRegistrationException(
			                                           String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
			                                                         clazz.getSimpleName(), options, e.getMessage()),
			                                           null, options, e);
			
		} catch (final InvocationTargetException e) {
			throw new ArgumentSetRegistrationException(
			                                           String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
			                                                         clazz.getSimpleName(), options, e.getMessage()),
			                                           null, options, e);
			
		}
	}
	
	/**
	 * Creates the requested argument instance.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param <X>
	 *            the generic type
	 * @param options
	 *            the options
	 * @return the x
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 * @throws ArgumentSetRegistrationException
	 */
	@SuppressWarnings ("unchecked")
	public static <T, Y extends ArgumentSet<T, X>, X extends ArgumentSetOptions<T, Y>> Y create(final X options) throws SettingsParseError,
	                                                                                                            ArgumentSetRegistrationException,
	                                                                                                            ArgumentRegistrationException {
		return (Y) create((Object) options);
	}
	
	/**
	 * Gets the class.
	 * 
	 * @param type
	 *            the type
	 * @return the class
	 */
	@SuppressWarnings ("rawtypes")
	public static Class<?> getClass(final Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			final Type componentType = ((GenericArrayType) type).getGenericComponentType();
			final Class<?> componentClass = getClass(componentType);
			
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the type arguments.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return the type arguments
	 */
	@SuppressWarnings ("rawtypes")
	private static <T> List<Class<?>> getTypeArguments(final Class<T> baseClass,
	                                                   final Class<? extends T> childClass) {
		final Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just
				// keep going.
				type = ((Class) type).getGenericSuperclass();
			} else {
				final ParameterizedType parameterizedType = (ParameterizedType) type;
				final Class<?> rawType = (Class) parameterizedType.getRawType();
				
				final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}
				
				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}
		
		// finally, for each actual type argument provided to baseClass,
		// determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		final List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}
}

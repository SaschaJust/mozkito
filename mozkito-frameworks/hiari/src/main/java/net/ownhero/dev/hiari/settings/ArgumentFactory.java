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
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * A factory for creating Argument objects.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ArgumentFactory {
	
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
	public static <T, Y extends Argument<T, X>, X extends ArgumentOptions<T, Y>> Y create(@NotNull final X options) throws ArgumentRegistrationException,
	                                                                                                               SettingsParseError,
	                                                                                                               ArgumentSetRegistrationException {
		boolean initialize = true;
		
		if (options.getArgumentSet().getSettings().getProperty("help") != null) {
			initialize = false;
		}
		
		options.getArgumentSet().getSettings().addOption(options);
		
		if (initialize) {
			@SuppressWarnings ("unchecked")
			final Class<Y> clazz = (Class<Y>) getTypeArguments(ArgumentOptions.class, options.getClass()).get(1);
			
			Constructor<Y> constructor;
			try {
				constructor = clazz.getDeclaredConstructor(options.getClass());
				final Y argument = constructor.newInstance(options);
				final ISettings settings = options.getArgumentSet().getSettings();
				
				final String property = settings.getProperty(argument.getTag());
				
				if (property != null) {
					// set the actual value
					argument.setStringValue(property);
				} else {
					// check for deny default values
					if (argument.getDefaultValue() != null) {
						if (settings.getProperty(settings.getDenyDefaultValuesTag()) != null) {
							
							throw new ArgumentRegistrationException(
							                                        "Can't setup argument because denyDefaultValues is set and no explicit value set.",
							                                        argument, options);
						} else {
							// take the default value
						}
					} else {
						if (argument.required()) {
							throw new ArgumentRegistrationException("Required but not set.", argument, options);
						}
					}
				}
				
				final List<Requirement> requiredDependencies = argument.getRequirements().getRequiredDependencies();
				
				if (argument.required() && (requiredDependencies != null)) {
					throw new ArgumentRegistrationException("Required dependencies were not fullfilled. Lagging: "
					        + JavaUtils.collectionToString(requiredDependencies), argument, options);
				}
				
				if (!argument.init()) {
					throw new ArgumentRegistrationException("Initializing the argument failed.", argument, options);
				}
				
				argument.getParent().parse();
				
				return argument;
			} catch (final IllegalArgumentException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				
				throw new ArgumentRegistrationException(
				                                        String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
				                                                      clazz.getSimpleName(), options, e.getMessage()),
				                                        null, options, e);
			} catch (final SecurityException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new ArgumentRegistrationException(
				                                        String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
				                                                      clazz.getSimpleName(), options, e.getMessage()),
				                                        null, options, e);
			} catch (final NoSuchMethodException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new ArgumentRegistrationException(
				                                        String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
				                                                      clazz.getSimpleName(), options, e.getMessage()),
				                                        null, options, e);
			} catch (final InstantiationException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new ArgumentRegistrationException(
				                                        String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
				                                                      clazz.getSimpleName(), options, e.getMessage()),
				                                        null, options, e);
			} catch (final IllegalAccessException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new ArgumentRegistrationException(
				                                        String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
				                                                      clazz.getSimpleName(), options, e.getMessage()),
				                                        null, options, e);
			} catch (final InvocationTargetException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				throw new ArgumentRegistrationException(
				                                        String.format("Instantiating the argument of type %s failed with arguments (%s). Error: %s",
				                                                      clazz.getSimpleName(), options, e.getMessage()),
				                                        null, options, e);
			}
		} else {
			return null;
		}
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

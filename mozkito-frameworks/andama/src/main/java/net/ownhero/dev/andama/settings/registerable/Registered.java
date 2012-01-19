/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings.registerable;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.DoubleArgument;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.InputFileArgument;
import net.ownhero.dev.andama.settings.ListArgument;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.andama.settings.MaskedStringArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.andama.settings.StringArgument;
import net.ownhero.dev.andama.settings.URIArgument;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.ArrayCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * Classes extending {@link Registered} can dynamically register config options
 * to the tool chain. Additionally there is support to automatically generate
 * config option names following the standard naming convention.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class Registered {
	
	private static String buildRegisteredList(final Collection<Class<? extends Registered>> registereds) {
		final StringBuilder builder = new StringBuilder();
		builder.append(FileUtils.lineSeparator);
		
		for (final Class<? extends Registered> registered : registereds) {
			try {
				builder.append('\t').append("  ").append(registered.getSimpleName()).append(": ")
				       .append(registered.newInstance().getDescription());
			} catch (final InstantiationException e) {
			} catch (final IllegalAccessException e) {
			}
			
			if (builder.length() != 0) {
				builder.append(FileUtils.lineSeparator);
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * @param clazz
	 *            the base class extending {@link Registered}
	 * @return the lowercase part of the name specifies the category of the
	 *         registered class, e.g. "engine" for MappingEngine.
	 */
	private static final String findRegisteredSuper(final Class<? extends Registered> clazz,
	                                                final Set<String> superTags) {
		return findRegisteredSuper(clazz, superTags, false);
	}
	
	/**
	 * @param clazz
	 * @param superTag
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private static String findRegisteredSuper(final Class<? extends Registered> clazz,
	                                          final Set<String> superTags,
	                                          final boolean abstractSuccessor) {
		if (clazz.getSuperclass() == Registered.class) {
			if (((clazz.getModifiers() & Modifier.ABSTRACT) == 0) || !abstractSuccessor) {
				superTags.add(clazz.getSimpleName());
				return clazz.getSimpleName().toLowerCase();
			} else {
				return null;
			}
			// superTag[0] = clazz.getSimpleName().replaceFirst("^[A-Z][^A-Z]+",
			// "");
			
		} else if (clazz == Registered.class) {
			throw new UnrecoverableError("Instance of ABSTRACT class " + Registered.class.getSimpleName()
			        + " tries to register config option.");
		} else {
			Class<? extends Registered> c = clazz;
			if (Registered.class.isAssignableFrom(c.getSuperclass()) && (c.getSuperclass() != Registered.class)) {
				c = (Class<? extends Registered>) c.getSuperclass();
				final String string = findRegisteredSuper(c, superTags, (clazz.getModifiers() & Modifier.ABSTRACT) != 0);
				String retval;
				String simpleName = clazz.getSimpleName();
				for (final String tag : superTags) {
					simpleName = simpleName.replace(tag, "");
				}
				if (string != null) {
					retval = string + "." + simpleName.toLowerCase();
				} else {
					retval = simpleName;
				}
				superTags.add(simpleName);
				return retval;
			}
		}
		throw new UnrecoverableError("Instance of ABSTRACT class " + Registered.class.getSimpleName()
		        + " tries to register config option.");
	}
	
	public static Set<? extends Registered> handleRegistered(final AndamaChain chain,
	                                                         final AndamaSettings settings,
	                                                         final AndamaArgumentSet arguments,
	                                                         final String argumentName,
	                                                         final Class<? extends Registered> superClass,
	                                                         final boolean isRequired) {
		final Set<Registered> registereds = new HashSet<Registered>();
		
		final Collection<Class<? extends Registered>> registeredClasses = new LinkedList<Class<? extends Registered>>();
		try {
			registeredClasses.addAll(ClassFinder.getClassesExtendingClass(superClass.getPackage(), superClass,
			                                                              Modifier.ABSTRACT | Modifier.INTERFACE
			                                                                      | Modifier.PRIVATE));
		} catch (final Exception e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		
		final StringBuilder builder = new StringBuilder();
		
		for (final Class<? extends Registered> clazz : registeredClasses) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(clazz.getSimpleName());
		}
		arguments.addArgument(new ListArgument(settings, chain.getName().toLowerCase() + "." + argumentName,
		                                       "A list of " + chain.getName() + " " + argumentName
		                                               + "s that shall be used: "
		                                               + buildRegisteredList(registeredClasses), builder.toString(),
		                                       isRequired));
		
		final String registeredstring = System.getProperty(chain.getName().toLowerCase() + "." + argumentName);
		final Set<String> registeredNames = new HashSet<String>();
		
		if (registeredstring != null) {
			for (final String registeredName : registeredstring.split(",")) {
				registeredNames.add(superClass.getPackage().getName() + "." + registeredName);
			}
			
		}
		
		for (final Class<? extends Registered> klass : registeredClasses) {
			if (registeredNames.isEmpty() || registeredNames.contains(klass.getCanonicalName())) {
				if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
					if (Logger.logInfo()) {
						Logger.info("Adding new " + klass.getSuperclass().getSimpleName() + " "
						        + klass.getCanonicalName());
					}
					
					try {
						final Registered instance = klass.newInstance();
						instance.register(settings, arguments);
						registereds.add(instance);
					} catch (final Exception e) {
						
						if (Logger.logWarn()) {
							Logger.warn("Skipping registration of " + klass.getSimpleName() + " due to errors: "
							                    + e.getMessage(), e);
						}
					}
				}
			} else {
				if (Logger.logInfo()) {
					Logger.info("Not loading available engine: " + klass.getSimpleName());
				}
			}
		}
		
		return registereds;
	}
	
	/**
	 * @param registereds
	 */
	public static void initRegistereds(final Set<Registered> registereds) {
		for (final Registered registered : registereds) {
			registered.init();
		}
	}
	
	private boolean                                             initialized       = false;
	
	private boolean                                             registered        = false;
	
	private final Map<String, Tuple<String, AndamaArgument<?>>> registeredOptions = new HashMap<String, Tuple<String, AndamaArgument<?>>>();
	
	private AndamaSettings                                      settings;
	
	/**
	 * 
	 */
	public Registered() {
		super();
	}
	
	/**
	 * @param optionName
	 * @param configString
	 * @param argument
	 * @return
	 */
	private boolean addOption(final String optionName,
	                          final String configString,
	                          final AndamaArgument<?> argument) {
		if (!this.registeredOptions.containsKey(optionName)) {
			this.registeredOptions.put(optionName, new Tuple<String, AndamaArgument<?>>(configString, argument));
			return true;
		} else {
			return false;
		}
	}
	
	final String deriveSettingsClassificationString(final Class<? extends AndamaSettings> settingsClass,
	                                                final Set<String> tokens,
	                                                final StringBuilder result) {
		if (settingsClass == AndamaSettings.class) {
			tokens.add("Settings");
			final String string = AndamaSettings.class.getSimpleName().replace("Settings", "");
			return string;
		} else {
			@SuppressWarnings ("unchecked")
			final String helper = deriveSettingsClassificationString((Class<? extends AndamaSettings>) settingsClass.getSuperclass(),
			                                                         tokens, result);
			String name = settingsClass.getSimpleName();
			for (final String token : tokens) {
				name = name.replace(token, "");
			}
			
			// Remove same prefix if length > 2 and following character is
			// uppercase
			int len = 0;
			
			while (name.regionMatches(0, helper, 0, len + 1)) {
				++len;
			}
			
			while ((len > 2) && ((len + 1) < name.length()) && (!Character.isUpperCase(name.charAt(len)))) {
				--len;
			}
			
			if (len > 2) {
				String newtoken = name.substring(0, len);
				newtoken = name.replace(newtoken, "");
				tokens.add(newtoken);
				if (result.length() > 0) {
					result.insert(0, '.');
				}
				result.insert(0, newtoken.toLowerCase());
				name = name.replace(newtoken, "");
			}
			
			// Remove same suffix if length > 2 and succeeding character is
			// uppercase
			len = 0;
			
			while (name.regionMatches(name.length() - len - 1, helper, helper.length() - len - 1, len + 1)) {
				++len;
			}
			
			while ((len > 2) && (len <= name.length()) && !Character.isUpperCase(name.charAt(len - 1))) {
				--len;
			}
			
			if (len > 2) {
				String newtoken = name.substring(name.length() - len - 1, len);
				newtoken = name.replace(newtoken, "");
				tokens.add(newtoken);
				if (result.length() > 0) {
					result.insert(0, '.');
				}
				result.insert(0, newtoken.toLowerCase());
				name = name.replace(newtoken, "");
			}
			
			return name;
		}
	}
	
	/**
	 * @return a string describing the task of the registered class
	 */
	public abstract String getDescription();
	
	/**
	 * @return
	 */
	public final String getHandle() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * @param optionName
	 * @return
	 */
	protected Tuple<String, AndamaArgument<?>> getOption(final String optionName) {
		return this.registeredOptions.get(optionName);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param option
	 * @return
	 */
	final String getOptionName(final String option) {
		final StringBuilder builder = new StringBuilder();
		final Set<String> tokens = new HashSet<String>();
		final String settingsName = deriveSettingsClassificationString(this.settings.getClass(), tokens, builder);
		if (settingsName.length() > 0) {
			if (builder.length() > 0) {
				builder.insert(0, '.');
			}
			builder.insert(0, settingsName.toLowerCase());
			tokens.add(settingsName);
		}
		
		builder.append('.');
		builder.append(findRegisteredSuper(this.getClass(), tokens).toLowerCase()).append('.');
		builder.append(option);
		return builder.toString();
	}
	
	/**
	 * @return the settings
	 */
	public final AndamaSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * 
	 */
	public void init() {
		Condition.check(isRegistered(), "The " + this.getClass().getSuperclass().getSimpleName()
		        + " has to be registered before it is initialized: %s", this.getClass().getSimpleName());
		setInitialized(true);
	}
	
	/**
	 * @return true, if the {@link Registered} object is enabled
	 */
	public abstract boolean isEnabled();
	
	/**
	 * @param listSetting
	 * @return
	 */
	public final boolean isEnabled(final String listSetting,
	                               final String registered) {
		if (getSettings() != null) {
			final ListArgument setting = (ListArgument) getSettings().getSetting(listSetting);
			return (setting.getValue() != null) & setting.getValue().contains(registered);
		} else {
			return true;
		}
	}
	
	/**
	 * @return the initialized
	 */
	public final boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return the registered
	 */
	public final boolean isRegistered() {
		return this.registered;
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param isRequired
	 */
	@NoneNull
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet arguments) {
		setSettings(settings);
		setRegistered(true);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerBooleanOption(@NotNull final AndamaSettings settings,
	                                        @NotNull final AndamaArgumentSet arguments,
	                                        @NotNull final String name,
	                                        @NotNull final String description,
	                                        final String defaultValue,
	                                        @NotNull final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new BooleanArgument(settings, configString, description, defaultValue,
		                                                       required && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerDirectoryOption(@NotNull final AndamaSettings settings,
	                                          @NotNull final AndamaArgumentSet arguments,
	                                          @NotNull final String name,
	                                          @NotNull final String description,
	                                          final String defaultValue,
	                                          final boolean required,
	                                          final boolean create) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new DirectoryArgument(settings, configString, description, defaultValue,
		                                                         required && isEnabled(), create);
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerDoubleOption(@NotNull final AndamaSettings settings,
	                                       @NotNull final AndamaArgumentSet arguments,
	                                       @NotNull final String name,
	                                       @NotNull final String description,
	                                       final String defaultValue,
	                                       final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new DoubleArgument(settings, configString, description, defaultValue,
		                                                      required && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @param required
	 * @param type
	 * @return
	 */
	protected boolean registerEnumOption(final AndamaSettings settings,
	                                     final AndamaArgumentSet arguments,
	                                     final String name,
	                                     final String description,
	                                     final String defaultValue,
	                                     final boolean required,
	                                     final String[] validValues) {
		if (defaultValue != null) {
			ArrayCondition.contains(validValues, defaultValue,
			                        "Default value '%s' has to be contained in valid values array: ", defaultValue,
			                        validValues);
		}
		
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new EnumArgument(settings, configString, description, defaultValue, required
		        && isEnabled(), validValues);
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
		
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerInputFileOption(@NotNull final AndamaSettings settings,
	                                          @NotNull final AndamaArgumentSet arguments,
	                                          @NotNull final String name,
	                                          @NotNull final String description,
	                                          final String defaultValue,
	                                          final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new InputFileArgument(settings, configString, description, defaultValue,
		                                                         required && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @param create
	 * @return
	 */
	protected boolean registerListOption(@NotNull final AndamaSettings settings,
	                                     @NotNull final AndamaArgumentSet arguments,
	                                     @NotNull final String name,
	                                     @NotNull final String description,
	                                     final String defaultValue,
	                                     final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new ListArgument(settings, configString, description, defaultValue, required
		        && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerLongOption(@NotNull final AndamaSettings settings,
	                                     @NotNull final AndamaArgumentSet arguments,
	                                     @NotNull final String name,
	                                     @NotNull final String description,
	                                     final String defaultValue,
	                                     final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new LongArgument(settings, configString, description, defaultValue, required
		        && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerMaskedStringOption(@NotNull final AndamaSettings settings,
	                                             @NotNull final AndamaArgumentSet arguments,
	                                             @NotNull final String name,
	                                             @NotNull final String description,
	                                             final String defaultValue,
	                                             final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new MaskedStringArgument(settings, configString, description, defaultValue,
		                                                            required && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerOutputFileOption(@NotNull final AndamaSettings settings,
	                                           @NotNull final AndamaArgumentSet arguments,
	                                           @NotNull final String name,
	                                           @NotNull final String description,
	                                           final String defaultValue,
	                                           final boolean required,
	                                           final boolean create) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new OutputFileArgument(settings, configString, description, defaultValue,
		                                                          required && isEnabled(), create);
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerStringOption(@NotNull final AndamaSettings settings,
	                                       @NotNull final AndamaArgumentSet arguments,
	                                       @NotNull final String name,
	                                       @NotNull final String description,
	                                       final String defaultValue,
	                                       final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new StringArgument(settings, configString, description, defaultValue,
		                                                      required && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param required
	 * @return
	 */
	protected boolean registerURIOption(@NotNull final AndamaSettings settings,
	                                    @NotNull final AndamaArgumentSet arguments,
	                                    @NotNull final String name,
	                                    @NotNull final String description,
	                                    final String defaultValue,
	                                    final boolean required) {
		final String configString = getOptionName(name);
		final AndamaArgument<?> argument = new URIArgument(settings, configString, description, defaultValue, required
		        && isEnabled());
		addOption(name, configString, argument);
		return arguments.addArgument(argument);
	}
	
	// protected boolean registerOption(final AndamaSettings settings,
	// final AndamaArgumentSet arguments,
	// final String name,
	// final String description,
	// final String defaultValue,
	// final boolean required,
	// final Class<EnumArgument> type) {
	// final Constructor<?>[] constructors = type.getConstructors();
	// final LinkedList<Constructor<?>> validConstructors = new
	// LinkedList<Constructor<?>>();
	// for (final Constructor<?> constructor : constructors) {
	// if (constructor.getParameterTypes().length >= 5) {
	// validConstructors.add(constructor);
	// }
	// }
	//
	// final String configString = getOptionName(name);
	// final Object[] initargs = new Object[] { settings, configString,
	// description, defaultValue,
	// required && isEnabled() };
	// for (final Constructor<?> constructor : validConstructors) {
	// try {
	// final AndamaArgument<?> argument = (AndamaArgument<?>)
	// constructor.newInstance(initargs);
	// addOption(name, configString, argument);
	// return arguments.addArgument(argument);
	// } catch (final Exception e) {
	// throw new UnrecoverableError(e);
	// }
	// }
	//
	// return false;
	// }
	
	/**
	 * @param initialized
	 *            the initialized to set
	 */
	final void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered
	 *            the registered to set
	 */
	final void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings
	 *            the settings to set
	 */
	final public void setSettings(final AndamaSettings settings) {
		this.settings = settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSuperclass().getSimpleName() + " [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @param string
	 * @return
	 */
	protected final String truncate(final String string) {
		return (string != null)
		                       ? string.substring(0, Math.min(string.length(), 254))
		                       : "";
		
	}
	
}

package net.ownhero.dev.andama.settings.registerable;

/*******************************************************************************
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
 ******************************************************************************/

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * Classes extending {@link Registered} can dynamically register config options to the tool chain. Additionally there is
 * support to automatically generate config option names following the standard naming convention.
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
	 * @return the lowercase part of the name specifies the category of the registered class, e.g. "engine" for
	 *         MappingEngine.
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
	                                                         final AndamaArgumentSet<?> arguments,
	                                                         final String argumentName,
	                                                         final Class<? extends Registered> superClass,
	                                                         final boolean isRequired) {
		final Set<Registered> registereds = new HashSet<Registered>();
		
		// final Collection<Class<? extends Registered>> registeredClasses = new
		// LinkedList<Class<? extends Registered>>();
		// try {
		// registeredClasses.addAll(ClassFinder.getClassesExtendingClass(superClass.getPackage(),
		// superClass,
		// Modifier.ABSTRACT | Modifier.INTERFACE
		// | Modifier.PRIVATE));
		// } catch (final Exception e) {
		// throw new UnrecoverableError(e.getMessage(), e);
		// }
		//
		// final StringBuilder builder = new StringBuilder();
		//
		// for (final Class<? extends Registered> clazz : registeredClasses) {
		// if (builder.length() > 0) {
		// builder.append(",");
		// }
		// builder.append(clazz.getSimpleName());
		// }
		// arguments.addArgument(new ListArgument(settings.getRootArgumentSet(),
		// chain.getName().toLowerCase() + "."
		// + argumentName, "A list of " + chain.getName() + " " + argumentName +
		// "s that shall be used: "
		// + buildRegisteredList(registeredClasses), builder.toString(),
		// isRequired));
		//
		// final String registeredstring =
		// System.getProperty(chain.getName().toLowerCase() + "." +
		// argumentName);
		// final Set<String> registeredNames = new HashSet<String>();
		//
		// if (registeredstring != null) {
		// for (final String registeredName : registeredstring.split(",")) {
		// registeredNames.add(superClass.getPackage().getName() + "." +
		// registeredName);
		// }
		//
		// }
		//
		// for (final Class<? extends Registered> klass : registeredClasses) {
		// if (registeredNames.isEmpty() ||
		// registeredNames.contains(klass.getCanonicalName())) {
		// if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
		// if (Logger.logInfo()) {
		// Logger.info("Adding new " + klass.getSuperclass().getSimpleName() +
		// " "
		// + klass.getCanonicalName());
		// }
		//
		// try {
		// final Registered instance = klass.newInstance();
		// instance.register(settings, arguments);
		// instance.setSettings(settings);
		// registereds.add(instance);
		// } catch (final Exception e) {
		//
		// if (Logger.logWarn()) {
		// Logger.warn("Skipping registration of " + klass.getSimpleName() +
		// " due to errors: "
		// + e.getMessage(), e);
		// }
		// }
		// }
		// } else {
		// if (Logger.logInfo()) {
		// Logger.info("Not loading available engine: " +
		// klass.getSimpleName());
		// }
		// }
		// }
		
		return registereds;
	}
	
	private final Map<String, Tuple<String, AndamaArgument<?>>> registeredOptions = new HashMap<String, Tuple<String, AndamaArgument<?>>>();
	
	private AndamaSettings                                      settings;
	
	/**
	 * @param settingsClass
	 * @param tokens
	 * @param result
	 * @return
	 */
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
	protected final Tuple<String, AndamaArgument<?>> getOption(@NotNull final String optionName) {
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
	 * @param settings
	 * @param arguments
	 * @param isRequired
	 */
	@NoneNull
	public abstract void register(final AndamaSettings settings,
	                              final AndamaArgumentSet<?> arguments);
	
	/**
	 * @param settings
	 *            the settings to set
	 */
	public final void setSettings(final AndamaSettings settings) {
		this.settings = settings;
	}
	
}

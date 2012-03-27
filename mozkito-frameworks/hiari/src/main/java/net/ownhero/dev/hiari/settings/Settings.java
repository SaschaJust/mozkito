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
package net.ownhero.dev.hiari.settings;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ClassLoadingError;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.ClassCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class Settings.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class Settings implements ISettings {
	
	/**
	 * The Class Help.
	 */
	private class Help {
		
		/** The option map. */
		Map<String, IOptions<?, ?>> optionMap = new TreeMap<String, IOptions<?, ?>>();
		
		/**
		 * Adds the option.
		 * 
		 * @param <T>
		 *            the generic type
		 * @param <Y>
		 *            the generic type
		 * @param <X>
		 *            the generic type
		 * @param options
		 *            the options
		 * @throws ArgumentRegistrationException
		 *             the argument registration exception
		 * @throws ArgumentSetRegistrationException
		 *             the argument set registration exception
		 */
		public <T, Y extends IArgument<T, ?>, X extends IOptions<T, ? extends Y>> void addOption(@NotNull final X options) throws ArgumentRegistrationException,
		                                                                                                                  ArgumentSetRegistrationException {
			if (!this.optionMap.containsKey(options.getTag())) {
				this.optionMap.put(options.getTag(), options);
			} else {
				if (options instanceof ArgumentOptions) {
					throw new ArgumentRegistrationException("Options already present.", null,
					                                        (ArgumentOptions<?, ?>) options);
				} else if (options instanceof ArgumentSetOptions) {
					throw new ArgumentSetRegistrationException("Options already present.", null,
					                                           (ArgumentSetOptions<?, ?>) options);
				} else {
					if (Logger.logError()) {
						Logger.error("TODO THIS SHOULD NEVER HAPPEN.");
					}
				}
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			// PRECONDITIONS
			
			final StringBuilder builder = new StringBuilder();
			
			try {
				int maxlength = 0;
				for (final String key : this.optionMap.keySet()) {
					if (key.length() > maxlength) {
						maxlength = key.length();
					}
				}
				
				for (final String key : this.optionMap.keySet()) {
					builder.append(this.optionMap.get(key).getHelpString(maxlength + 1))
					       .append(FileUtils.lineSeparator);
					// builder.append(key).append('\t').append(this.optionMap.get(key)).append(FileUtils.lineSeparator);
				}
				return builder.toString();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/**
	 * The Class RootArgumentSet.
	 * 
	 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
	 */
	final class RootArgumentSet extends ArgumentSet<Boolean, RootArgumentSet.Options> {
		
		/**
		 * The Class Options.
		 */
		class Options extends ArgumentSetOptions<Boolean, RootArgumentSet> {
			
			/** The Constant TAG. */
			static final String TAG         = "ROOT";
			
			/** The Constant DESCRIPTION. */
			static final String DESCRIPTION = "Base arguments.";
			
			/**
			 * Instantiates a new options.
			 * 
			 * @param argumentSet
			 *            the argument set
			 * @param requirements
			 *            the requirements
			 */
			public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
				super(argumentSet, TAG, DESCRIPTION, requirements);
			}
			
			/*
			 * (non-Javadoc)
			 * @see net.ownhero.dev.andama.settings.ArgumentSetOptions#init(java.util.Map)
			 */
			@Override
			public Boolean init() {
				return true;
			}
			
			/*
			 * (non-Javadoc)
			 * @see
			 * net.ownhero.dev.andama.settings.ArgumentSetOptions#requirements(net.ownhero.dev.andama.settings.ArgumentSet
			 * )
			 */
			@Override
			public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
			                                                                            SettingsParseError {
				return new HashMap<String, IOptions<?, ?>>();
			}
			
		}
		
		/**
		 * Instantiates a new root argument set.
		 * 
		 * @param settings
		 *            the settings
		 */
		@SuppressWarnings ("deprecation")
		private RootArgumentSet(final ISettings settings) {
			super(settings, Options.TAG, Options.DESCRIPTION);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.andama.settings.ArgumentSet#getParent()
		 */
		@Override
		public ArgumentSet<?, ?> getParent() {
			return null;
		}
		
	}
	
	/** The Constant settingsTag. */
	private static final String settingsTag = "config";
	
	/**
	 * Gets the report this.
	 * 
	 * @return the report this
	 */
	public static String getReportThis() {
		return reportThis;
	}
	
	/** The argument sets. */
	private final Map<String, ArgumentSet<?, ?>>          argumentSets         = new HashMap<String, ArgumentSet<?, ?>>();
	
	/** The tool information. */
	private final Map<String, String>                     information          = new HashMap<String, String>();
	
	/** The no default value arg. */
	private BooleanArgument                               noDefaultValueArg;
	
	/** The disable crash arg. */
	private BooleanArgument                               disableCrashArg;
	
	/** The settings arg. */
	private URIArgument                                   settingsArg;
	
	/** The mail arguments. */
	private ArgumentSet<Properties, MailOptions>          mailArguments;
	
	/** The root argument set. */
	private ArgumentSet<Boolean, RootArgumentSet.Options> root;
	
	/** The bug report argument. */
	private StringArgument                                bugReportArgument;
	
	/** The properties. */
	private final Properties                              properties           = new Properties();
	
	/** The report this. */
	private static String                                 reportThis           = "Please file a bug report with this error message here: https://dev.own-hero.net";
	
	/** The Constant denyDefaultValuesTag. */
	public static final String                            denyDefaultValuesTag = "denyDefaultValues";
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		try {
			System.getProperties().remove("help");
			
			ISettings settings = new Settings();
			System.err.println("toString() (without -Dhelp)");
			System.err.println(settings);
			System.err.println();
			System.err.println("HELP (without -Dhelp)");
			System.err.println(settings.getHelpString());
			
			System.err.println();
			System.err.println();
			
			System.setProperty("help", "T");
			settings = new Settings();
			System.err.println("toString() (with -Dhelp)");
			System.err.println(settings);
			System.err.println();
			System.err.println("HELP (with -Dhelp)");
			System.err.println(settings.getHelpString());
			
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		}
		
	}
	
	/** The help. */
	public Help                                 help   = new Help();
	
	/** The logger args. */
	private ArgumentSet<Boolean, LoggerOptions> loggerArgs;
	
	/** The nohelp. */
	private boolean                             nohelp = true;
	
	/**
	 * Instantiates a new settings.
	 * 
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	public Settings() throws SettingsParseError {
		// in any case, you first want to read the logger options
		Logger.readConfiguration();
		
		try {
			// first read
			getProperties().clear();
			
			// first set properties to the properties set on the command line
			final Properties commandlineProps = (Properties) System.getProperties().clone();
			// this will be set later by the corresponding URIArgument if set at all
			final Properties fileProps = new Properties();
			getProperties().putAll(commandlineProps);
			
			// now, create the root of all arguments
			this.root = new RootArgumentSet(this);
			
			// setup the help argument
			ArgumentFactory.create(new BooleanArgument.Options(getRoot(), "help", "Shows this help menu.", false,
			                                                   Requirement.optional));
			if (getProperties().get("help") != null) {
				this.nohelp = false;
			}
			this.settingsArg = ArgumentFactory.create(new URIArgument.Options(
			                                                                  getRoot(),
			                                                                  settingsTag,
			                                                                  "Setting file that contains the JavaVM arguments for the current toolchain.",
			                                                                  null, Requirement.optional));
			// check to load settings from URI
			if (System.getProperty(settingsTag) != null) {
				try {
					final InputStream stream = this.settingsArg.getValue().toURL().openStream();
					fileProps.load(stream);
					
				} catch (final MalformedURLException e) {
					throw new SettingsParseError(e.getMessage());
				} catch (final IOException e) {
					throw new SettingsParseError(e.getMessage());
				}
			}
			
			if (fileProps != null) {
				getProperties().putAll(fileProps);
				// overwrite values given on the commandline
				getProperties().putAll(commandlineProps);
			}
			
			this.bugReportArgument = ArgumentFactory.create(new StringArgument.Options(
			                                                                           getRoot(),
			                                                                           "report",
			                                                                           "Determines the error string yielding the URL to the bug tracker for this project.",
			                                                                           "Please file a bug report with this error message here: https://dev.own-hero.net",
			                                                                           Requirement.required));
			this.noDefaultValueArg = ArgumentFactory.create(new BooleanArgument.Options(getRoot(),
			                                                                            denyDefaultValuesTag,
			                                                                            "Ignore default values!",
			                                                                            false, Requirement.optional));
			this.disableCrashArg = ArgumentFactory.create(new BooleanArgument.Options(
			                                                                          getRoot(),
			                                                                          "disableCrashEmail",
			                                                                          "If set to `true` no crash emails will be send!",
			                                                                          null, Requirement.optional));
			this.mailArguments = ArgumentSetFactory.create(new MailOptions(getRoot(), Requirement.required));
			this.loggerArgs = ArgumentSetFactory.create(new LoggerOptions(getRoot(), Requirement.required));
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new SettingsParseError(e.getMessage(), e.getArgumentSet(), e);
		} catch (final ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new SettingsParseError(e.getMessage(), e.getArgumentSet(), e);
			
		}
		
	}
	
	/**
	 * Adds the argument mapping.
	 * 
	 * @param name
	 *            the name
	 * @param argument
	 *            the argument
	 * @return true, if successful
	 */
	boolean addArgumentMapping(final String name,
	                           final ArgumentSet<?, ?> argument) {
		if (!this.argumentSets.containsKey(name)) {
			synchronized (this.argumentSets) {
				if (!this.argumentSets.containsKey(name)) {
					this.argumentSets.put(name, argument);
					return true;
				} else {
					return false;
				}
			}
			
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#addInformation(java.lang.String, java.lang.String)
	 */
	@Override
	public void addInformation(final String key,
	                           final String information) {
		synchronized (information) {
			this.information.put(key, information);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#addOption(net.ownhero.dev.andama.settings.IOptions)
	 */
	@Override
	public <T, X extends IArgument<T, Y>, Y extends IOptions<T, X>> void addOption(@NotNull final Y options) throws ArgumentRegistrationException,
	                                                                                                        ArgumentSetRegistrationException {
		this.help.addOption(options);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ISettings#getAnchor(java.lang.String)
	 */
	@Override
	public final ArgumentSet<?, ?> getAnchor(final String argumentSetTag) {
		return this.argumentSets.get(argumentSetTag);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ISettings#getArgument(net.ownhero.dev.hiari.settings.IArgumentOptions)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public <T, X extends ArgumentOptions<T, Y>, Y extends Argument<T, X>> Y getArgument(final IArgumentOptions<T, Y> option) {
		// PRECONDITIONS
		
		try {
			if (Logger.logTrace()) {
				Logger.trace(String.format("Requesting Argument (tag: '%s').", option.getTag()));
			}
			synchronized (this.argumentSets) {
				return this.argumentSets.get(option.getTag()).getArgument(option);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ISettings#getArgumentSet(net.ownhero.dev.hiari.settings.IArgumentSetOptions)
	 */
	@Override
	public <T, X extends ArgumentSetOptions<T, Y>, Y extends ArgumentSet<T, X>> Y getArgumentSet(final IArgumentSetOptions<T, Y> option) {
		// PRECONDITIONS
		
		try {
			if (Logger.logTrace()) {
				Logger.trace(String.format("Requesting ArgumentSet (tag: '%s').", option.getTag()));
			}
			synchronized (this.argumentSets) {
				return (Y) this.argumentSets.get(option.getTag());
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the bug report argument.
	 * 
	 * @return the bugReportArgument
	 */
	@Override
	public final StringArgument getBugReportArgument() {
		return this.bugReportArgument;
	}
	
	/**
	 * Gets the deny default values tag.
	 * 
	 * @return the denydefaultvaluestag
	 */
	@Override
	public final String getDenyDefaultValuesTag() {
		// PRECONDITIONS
		
		try {
			return denyDefaultValuesTag;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(denyDefaultValuesTag, "Field '%s' in '%s'.", "denyDefaultValuesTag",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * Gets the disable crash arg.
	 * 
	 * @return the disableCrashArg
	 */
	public final BooleanArgument getDisableCrashArg() {
		return this.disableCrashArg;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#getHandle()
	 */
	@Override
	public String getHandle() {
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#getHelpString()
	 */
	@Override
	public String getHelpString() {
		if (this.nohelp) {
			if (Logger.logTrace()) {
				Logger.trace("Help mode is inactive. Displaying graph structure deduced from active settings.");
			}
			return getRoot().getHelpString();
		} else {
			if (Logger.logTrace()) {
				Logger.trace("Help mode is active. Displaying information based on IOptions seen so far (stored in the Help entity in Settings).");
			}
			return this.help.toString();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#getToolInformation()
	 */
	@Override
	public String getInformation() {
		final StringBuilder builder = new StringBuilder();
		
		synchronized (this.information) {
			for (final String tool : this.information.keySet()) {
				builder.append("[["); //$NON-NLS-1$
				builder.append(tool);
				builder.append("]]"); //$NON-NLS-1$
				builder.append(FileUtils.lineSeparator);
				builder.append(this.information.get(tool));
				builder.append(FileUtils.lineSeparator);
				builder.append(FileUtils.lineSeparator);
			}
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#getLoggerArguments()
	 */
	@Override
	public final ArgumentSet<Boolean, LoggerOptions> getLoggerArguments() {
		return this.loggerArgs;
	}
	
	/**
	 * Gets the mail arguments.
	 * 
	 * @return the mailArguments
	 */
	@Override
	public final ArgumentSet<Properties, MailOptions> getMailArguments() {
		return this.mailArguments;
	}
	
	/**
	 * Gets the no default value arg.
	 * 
	 * @return the noDefaultValueArg
	 */
	@Override
	public final BooleanArgument getNoDefaultValueArg() {
		return this.noDefaultValueArg;
	}
	
	/**
	 * Gets the properties.
	 * 
	 * @return the properties
	 */
	private final Properties getProperties() {
		return this.properties;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#getProperty(java.lang.String)
	 */
	@Override
	public final String getProperty(final String name) {
		// PRECONDITIONS
		Condition.notNull(getProperties(), "The field %s in %s.", "properties", getHandle());
		
		return getProperties().getProperty(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#getRootArgumentSet()
	 */
	@Override
	public final ArgumentSet<Boolean, RootArgumentSet.Options> getRoot() {
		return this.root;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#hasSetting(java.lang.String)
	 */
	@Override
	public final boolean hasSetting(final String name) {
		synchronized (this.argumentSets) {
			return this.argumentSets.containsKey(name);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ISettings#helpRequested()
	 */
	@Override
	public final boolean helpRequested() {
		return !this.nohelp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ISettings#isCrashEmailDisabled()
	 */
	@Override
	public final boolean isCrashEmailDisabled() {
		return this.disableCrashArg.getValue();
	}
	
	/**
	 * Load by class.
	 * 
	 * @param providerClass
	 *            the provider class
	 * @param anchorSet
	 *            the anchor set
	 * @return the argument set
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Override
	public final ArgumentSet<?, ?> loadByClass(final Class<? extends SettingsProvider> providerClass,
	                                           final ArgumentSet<?, ?> anchorSet) throws ArgumentRegistrationException,
	                                                                             ArgumentSetRegistrationException,
	                                                                             SettingsParseError {
		try {
			ClassCondition.instantiable(providerClass, "Argument '%s' in '%s'.", "providerClass", getHandle());
			final SettingsProvider provider = providerClass.newInstance();
			return loadByEntity(provider, anchorSet);
		} catch (final InstantiationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		} catch (final IllegalAccessException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
		}
		
		return null;
	}
	
	/**
	 * Load by entity.
	 * 
	 * @param provider
	 *            the provider
	 * @param anchorSet
	 *            the anchor set
	 * @return the argument set
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Override
	public final ArgumentSet<?, ?> loadByEntity(final SettingsProvider provider,
	                                            final ArgumentSet<?, ?> anchorSet) throws ArgumentRegistrationException,
	                                                                              ArgumentSetRegistrationException,
	                                                                              SettingsParseError {
		return provider.provide(anchorSet);
	}
	
	/**
	 * Load by inheritance.
	 * 
	 * @param pakkage
	 *            the pakkage
	 * @param anchorSet
	 *            the anchor set
	 * @return the collection
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Override
	public final Collection<ArgumentSet<?, ?>> loadByInheritance(final Package pakkage,
	                                                             final ArgumentSet<?, ?> anchorSet) throws ArgumentRegistrationException,
	                                                                                               ArgumentSetRegistrationException,
	                                                                                               SettingsParseError {
		final Collection<ArgumentSet<?, ?>> ret = new LinkedList<ArgumentSet<?, ?>>();
		
		try {
			final Collection<Class<SettingsProvider>> collection = ClassFinder.getClassesOfInterface(pakkage,
			                                                                                         SettingsProvider.class,
			                                                                                         Modifier.ABSTRACT
			                                                                                                 | Modifier.INTERFACE
			                                                                                                 | Modifier.PRIVATE
			                                                                                                 | Modifier.PROTECTED);
			for (final Class<SettingsProvider> providerClass : collection) {
				ret.add(loadByClass(providerClass, anchorSet));
			}
			
		} catch (final ClassNotFoundException e) {
			throw new ClassLoadingError(e, null);
		} catch (final WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e);
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
		
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(getClass().getSimpleName() + ":"); //$NON-NLS-1$
		builder.append(FileUtils.lineSeparator);
		
		for (int i = 0; i < (getClass().getSimpleName().length() + ":".length()); ++i) { //$NON-NLS-1$
			builder.append('-');
		}
		
		builder.append(FileUtils.lineSeparator);
		
		builder.append(getRoot().toString());
		
		return builder.toString();
	}
	
}

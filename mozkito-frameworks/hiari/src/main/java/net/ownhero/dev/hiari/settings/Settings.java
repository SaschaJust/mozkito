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
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;

import net.ownhero.dev.hiari.settings.arguments.BooleanArgument;
import net.ownhero.dev.hiari.settings.arguments.LoggerArguments;
import net.ownhero.dev.hiari.settings.arguments.MailArguments;
import net.ownhero.dev.hiari.settings.arguments.StringArgument;
import net.ownhero.dev.hiari.settings.arguments.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.registerable.ArgumentProvider;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Optional;
import net.ownhero.dev.hiari.settings.requirements.Required;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class Settings implements ISettings {
	
	/**
	 * @return
	 */
	public static String getReportThis() {
		return reportThis;
	}
	
	private final Map<String, ArgumentSet<?>> argumentSets      = new HashMap<String, ArgumentSet<?>>();
	
	private final Map<String, String>         toolInformation   = new HashMap<String, String>();
	private Properties                        commandlineProps;
	private BooleanArgument                   noDefaultValueArg;
	private BooleanArgument                   helpArg;
	private BooleanArgument                   disableCrashArg;
	private URIArgument                       settingsArg;
	private MailArguments                     mailArguments     = null;
	
	private ArgumentSet<Boolean>              rootArgumentSet;
	private StringArgument                    bugReportArgument;
	private final Properties                  fileProps         = new Properties();
	private final Properties                  properties        = new Properties();
	
	private final Set<ArgumentProvider>       argumentProviders = new HashSet<ArgumentProvider>();
	
	private static String                     reportThis        = "Please file a bug report with this error message here: https://dev.own-hero.net";
	
	/**
	 * 
	 */
	public Settings() {
		Logger.readConfiguration();
		try {
			this.rootArgumentSet = new ArgumentSet<Boolean>(this, "ROOT arguments") {
				
				@Override
				protected boolean init() {
					setCachedValue(true);
					return true;
				}
			};
			
			this.bugReportArgument = new StringArgument(
			                                            getRootArgumentSet(),
			                                            "bug.report",
			                                            "Determines the error string yielding the URL to the bug tracker for this project.",
			                                            "Please file a bug report with this error message here: https://dev.own-hero.net",
			                                            new Required());
			this.noDefaultValueArg = new BooleanArgument(getRootArgumentSet(), "denyDefaultValues",
			                                             "Ignore default values!", "false", new Optional());
			this.helpArg = new BooleanArgument(getRootArgumentSet(), "help", "Shows this help menu.", "false",
			                                   new Optional());
			this.disableCrashArg = new BooleanArgument(getRootArgumentSet(), "disableCrashEmail",
			                                           "If set to `true` no crash emails will be send!", null,
			                                           new Optional());
			this.settingsArg = new URIArgument(
			                                   getRootArgumentSet(),
			                                   "andamaSettings",
			                                   "Setting file that contains the JavaVM arguments for the current toolchain.",
			                                   null, new Optional());
			
			this.mailArguments = new MailArguments(getRootArgumentSet(), new Required());
			
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	/**
	 * adds an argument to the andama suite settings. Leave default value <code>null</code> if none to be set.
	 * 
	 * @param name
	 *            Name of the JavaVM argument (-D<name>
	 * @param description
	 *            Short description that will be displayed when requesting help
	 * @param defaultValue
	 *            String that will be set as default value. If none to be set pass <code>null</code>.
	 * @return <code>true</code> if the argument could be added. <code>false</code> otherwise.
	 */
	protected boolean addArgument(@NotNull final ArgumentSet<?> argument) {
		
		if (this.argumentSets.containsKey(argument.getName())) {
			return false;
		} else {
			this.argumentSets.put(argument.getName(), argument);
			return true;
		}
		
	}
	
	@Override
	public boolean addArgumentMapping(final String name,
	                                  final ArgumentSet<?> argument) {
		if (!this.argumentSets.containsKey(name)) {
			this.argumentSets.put(name, argument);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @param argument
	 */
	@Override
	public void addArgumentProvider(final ArgumentProvider provider) {
		this.argumentProviders.add(provider);
	}
	
	/**
	 * @param tool
	 * @param information
	 */
	@Override
	public void addToolInformation(final String tool,
	                               final String information) {
		this.toolInformation.put(tool, information);
	}
	
	/**
	 * @return
	 */
	boolean frozen() {
		// TODO: implement this
		return false;
	}
	
	/**
	 * @param argument
	 * @return
	 */
	private final Argument<?> getArgument(final String argument) {
		return this.argumentSets.get(argument).getArgument(argument);
	}
	
	/**
	 * Return the arguments registered at this settings.
	 * 
	 * @return
	 */
	@Override
	public Collection<ArgumentSet<?>> getArguments() {
		return this.argumentSets.values();
	}
	
	/**
	 * @return the bugReportArgument
	 */
	public final StringArgument getBugReportArgument() {
		return this.bugReportArgument;
	}
	
	/**
	 * Return the help string that will contain all possible command line arguments.
	 * 
	 * @return
	 */
	@Override
	public String getHelpString() {
		final StringBuilder ss = new StringBuilder();
		ss.append("Available JavaVM arguments:");
		ss.append(System.getProperty("line.separator"));
		
		return getRootArgumentSet().getHelpString();
	}
	
	/**
	 * @return the mailArguments
	 */
	public MailArguments getMailArguments() {
		return this.mailArguments;
	}
	
	/**
	 * @return the properties
	 */
	@Override
	public Properties getProperties() {
		return this.properties;
	}
	
	/**
	 * @return the rootArgumentSet
	 */
	public final ArgumentSet<Boolean> getRootArgumentSet() {
		return this.rootArgumentSet;
	}
	
	/**
	 * @param name
	 * @return
	 */
	@Override
	public IArgument<?> getSetting(final String name) {
		final ArgumentSet<?> argumentSet = this.argumentSets.get(name);
		if (argumentSet.getArgument(name) == null) {
			return argumentSet;
		} else {
			return argumentSet.getArgument(name);
		}
	}
	
	/**
	 * @return
	 */
	@Override
	public String getToolInformation() {
		final StringBuilder builder = new StringBuilder();
		
		for (final String tool : this.toolInformation.keySet()) {
			builder.append("[[");
			builder.append(tool);
			builder.append("]]");
			builder.append(FileUtils.lineSeparator);
			builder.append(this.toolInformation.get(tool));
			builder.append(FileUtils.lineSeparator);
			builder.append(FileUtils.lineSeparator);
		}
		
		return builder.toString();
	}
	
	@Override
	public boolean hasSetting(final String name) {
		return this.argumentSets.containsKey(name);
	}
	
	/**
	 * @return
	 */
	public boolean isCrashEmailDisabled() {
		return this.disableCrashArg.getValue();
	}
	
	/**
	 * @throws SettingsParseError
	 * 
	 */
	@Override
	public void parse() throws SettingsParseError {
		// check to load settings from URI
		if (System.getProperty("andamaSettings") != null) {
			this.settingsArg.setStringValue(System.getProperty("andamaSettings"));
			try {
				this.settingsArg.parse();
			} catch (final SettingsParseError e) {
				if (Logger.logError()) {
					Logger.error(getHelpString());
				}
				throw e;
			}
			
			if (!this.settingsArg.isInitialized()) {
				throw new SettingsParseError("You cannot parse andamaSettings that are not initialized.");
			} else {
				try {
					final InputStream stream = this.settingsArg.getValue().toURL().openStream();
					this.fileProps.load(stream);
					
				} catch (final MalformedURLException e) {
					throw new SettingsParseError(e.getMessage());
				} catch (final IOException e) {
					throw new SettingsParseError(e.getMessage());
				}
				
			}
		}
		
		getProperties().clear();
		getProperties().putAll(this.fileProps);
		
		this.commandlineProps = (Properties) System.getProperties().clone();
		
		// overwrite properties with the one from the command line
		getProperties().putAll(this.commandlineProps);
		
		if (System.getProperty("help") != null) {
			System.err.println(getHelpString());
			throw new SettingsParseError(null, null);
		}
		
		for (final Entry<Object, Object> entry : getProperties().entrySet()) {
			final String argName = entry.getKey().toString().trim();
			final String value = entry.getValue().toString().trim();
			
			if (this.argumentSets.containsKey(argName)) {
				getArgument(argName).setStringValue(value);
				
			}
		}
		
		try {
			getRootArgumentSet().parse();
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(getHelpString());
			}
			throw e;
		}
		
		if (!validateSettings()) {
			System.err.println(getHelpString());
			throw new SettingsParseError(null, null);
		}
		
		if (this.helpArg.getValue()) {
			System.err.println(getHelpString());
			throw new SettingsParseError(null, null);
		}
		
		if (Logger.logInfo()) {
			Logger.info("Using settings: ");
			Logger.info(toString());
		}
		
		reportThis = this.bugReportArgument.getValue();
		
		// call after parse
		for (final ArgumentProvider provider : this.argumentProviders) {
			provider.afterParse();
		}
	}
	
	@Override
	public void parseArguments(final Collection<IArgument<?>> arguments) throws SettingsParseError {
		
		final PriorityQueue<IArgument<?>> queue = new PriorityQueue<IArgument<?>>(arguments);
		IArgument<?> argument = null;
		while ((argument = queue.poll()) != null) {
			
			parseArguments(argument.getDependencies());
			
			if (!argument.getRequirements().required()) {
				
				final String errorMessage = "Could not resolved dependencies. Argument: " + argument
				        + " has unresolved dependencies: "
				        + JavaUtils.collectionToString(argument.getRequirements().getMissingRequirements());
				throw new SettingsParseError(errorMessage, argument);
			}
			
			boolean initResult = false;
			if (argument instanceof Argument<?>) {
				initResult = ((Argument<?>) argument).init();
			} else {
				initResult = ((ArgumentSet<?>) argument).init();
			}
			
			if (!initResult) {
				final String message = "Could not initialize " + argument
				        + ". Please see error earlier error messages, refer to the argument help information, "
				        + "or review the init() method of the corresponding " + argument.getHandle() + ".";
				throw new SettingsParseError(message, argument);
			}
		}
		
		if (!validateSettings()) {
			throw new SettingsParseError("Provided arguments are invalid.");
		}
		
		if (this.helpArg.getValue()) {
			System.err.println(getHelpString());
		} else if (Logger.logInfo()) {
			Logger.info("Using settings: ");
			Logger.info(toString());
		}
	}
	
	/**
	 * Set the value of a registered argument field
	 * 
	 * @param argument
	 *            The name of the argument the value shall be set for.
	 * @param value
	 *            The value to be set as String.
	 * @throws NoSuchFieldException
	 *             If no argument with the specified name is registered.
	 */
	protected void setField(final String argument,
	                        final String value) throws NoSuchFieldException {
		if (!this.argumentSets.containsKey(argument)) {
			throw new NoSuchFieldException("Argument could not be set in " + getClass().getSimpleName()
			        + ". The argument is not part of the current argument set.");
		}
		((Argument<?>) getArgument(argument)).setStringValue(value);
	}
	
	/**
	 * Add the settings set for logger.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public LoggerArguments setLoggerArg(final Requirement requirements) throws ArgumentRegistrationException {
		return new LoggerArguments(getRootArgumentSet(), requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(getClass().getSimpleName() + ":");
		builder.append(FileUtils.lineSeparator);
		
		for (int i = 0; i < (getClass().getSimpleName().length() + ":".length()); ++i) {
			builder.append('-');
		}
		
		builder.append(FileUtils.lineSeparator);
		
		builder.append(getRootArgumentSet().toString());
		
		return builder.toString();
	}
	
	/**
	 * Check if all required arguments are set.
	 * 
	 * @return <code>null</code> if all required arguments are set. Returns the required argument with no value set
	 *         first found.
	 */
	private boolean validateSettings() {
		final Set<Argument<?>> defaultValueArgs = new HashSet<Argument<?>>();
		
		for (final ArgumentSet<?> args : this.argumentSets.values()) {
			for (final Argument<?> argument : args.getArguments().values()) {
				if (!argument.wasSet()) {
					if (this.noDefaultValueArg.getValue()) {
						argument.setStringValue(null);
					} else if (argument.getDefaultValue() != null) {
						defaultValueArgs.add(argument);
					}
				}
				if (argument.required() && (argument.getValue() == null)) {
					if (Logger.logError()) {
						Logger.error("Required argument `" + argument.getName() + "` is not set.");
					}
					return false;
				}
			}
		}
		
		if (defaultValueArgs.size() > 0) {
			final StringBuilder sb = new StringBuilder();
			sb.append("ARGUMENT WARNING: The following required arguments were not set and their default values will be used:");
			sb.append(FileUtils.lineSeparator);
			for (final Argument<?> arg : defaultValueArgs) {
				sb.append(arg.getName());
				sb.append(": ");
				sb.append(arg.getDefaultValue());
				sb.append(FileUtils.lineSeparator);
			}
			sb.append("Use -DdenyDefaultValues=T to allow only manually set arguments");
			if (Logger.logWarn()) {
				Logger.warn(sb.toString());
			}
		}
		
		return true;
	}
	
}

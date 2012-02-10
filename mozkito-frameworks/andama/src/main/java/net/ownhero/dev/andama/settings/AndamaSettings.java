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
package net.ownhero.dev.andama.settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.andama.settings.dependencies.Required;
import net.ownhero.dev.andama.settings.dependencies.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class AndamaSettings {
	
	public static void main(final String[] args) {
		final AndamaSettings settings = new AndamaSettings();
		System.err.println(settings.toString());
	}
	
	private final Map<String, AndamaArgumentSet<?>> argumentSets     = new HashMap<String, AndamaArgumentSet<?>>();
	private final Map<String, String>               toolInformation  = new HashMap<String, String>();
	
	private final Properties                        commandlineProps = (Properties) System.getProperties().clone();
	private final BooleanArgument                   noDefaultValueArg;
	private final BooleanArgument                   helpArg;
	private final BooleanArgument                   disableCrashArg;
	private final URIArgument                       settingsArg;
	private final MailArguments                     mailArguments;
	private AndamaArgumentSet<Boolean>              rootArgumentSet;
	
	private StringArgument                          bugReportArgument;
	private final Properties                        fileProps        = new Properties();
	private final Properties                        properties       = new Properties();
	
	/**
	 * 
	 */
	public AndamaSettings() {
		Logger.readConfiguration();
		this.rootArgumentSet = new AndamaArgumentSet<Boolean>(this, "ROOT arguments") {
			
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
		
		if (System.getProperty("andamaSettings") != null) {
			this.settingsArg.setStringValue(System.getProperty("andamaSettings"));
			if (!this.settingsArg.init()) {
				// FIXME ERROR
			} else {
				try {
					final InputStream stream = this.settingsArg.getValue().toURL().openStream();
					this.fileProps.load(stream);
					
				} catch (final MalformedURLException e) {
					// FIXME ERROR
				} catch (final IOException e) {
					// FIXME ERROR
				}
				
			}
		}
		
		this.properties.putAll(this.fileProps);
		this.properties.putAll(this.commandlineProps);
		
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
	protected boolean addArgument(@NotNull final AndamaArgumentSet<?> argument) {
		
		if (this.argumentSets.containsKey(argument.getName())) {
			return false;
		} else {
			this.argumentSets.put(argument.getName(), argument);
			return true;
		}
		
	}
	
	public boolean addArgumentMapping(final String name,
	                                  final AndamaArgumentSet<?> argument) {
		if (!this.argumentSets.containsKey(name)) {
			this.argumentSets.put(name, argument);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @param tool
	 * @param information
	 */
	public void addToolInformation(final String tool,
	                               final String information) {
		this.toolInformation.put(tool, information);
	}
	
	/**
	 * @param argument
	 * @return
	 */
	private final AndamaArgument<?> getArgument(final String argument) {
		return null;
	}
	
	/**
	 * Return the arguments registered at this settings.
	 * 
	 * @return
	 */
	public Collection<AndamaArgumentSet<?>> getArguments() {
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
	public String getHelpString() {
		final StringBuilder ss = new StringBuilder();
		ss.append("Available JavaVM arguments:");
		ss.append(System.getProperty("line.separator"));
		
		final TreeSet<AndamaArgumentInterface<?>> args = new TreeSet<AndamaArgumentInterface<?>>();
		args.addAll(this.argumentSets.values());
		
		for (final AndamaArgumentInterface<?> arg : args) {
			ss.append(arg).append(FileUtils.lineSeparator);
			// if (arg instanceof AndamaArgument<?>) {
			// AndamaArgument<?> argument = (AndamaArgument<?>) arg;
			// ss.append("\t");
			// ss.append("-D");
			// ss.append(argument.getName());
			// ss.append(": ");
			// ss.append(argument.getDescription());
			//
			// if ((argument.getDefaultValue() != null) &&
			// (argument.getDefaultValue().trim().length() > 0)) {
			// ss.append("| DEFAULT=");
			// ss.append(argument.getDefaultValue().trim());
			// }
			//
			// ss.append(" (" + argument.getRequirements() + ")");
			//
			// ss.append(System.getProperty("line.separator"));
			// }
		}
		
		return ss.toString();
	}
	
	/**
	 * @return the mailArguments
	 */
	public MailArguments getMailArguments() {
		return this.mailArguments;
	}
	
	/**
	 * @return the rootArgumentSet
	 */
	public final AndamaArgumentSet<Boolean> getRootArgumentSet() {
		return this.rootArgumentSet;
	}
	
	/**
	 * @param name
	 * @return
	 */
	public AndamaArgumentInterface<?> getSetting(final String name) {
		final AndamaArgumentSet<?> argumentSet = this.argumentSets.get(name);
		if (argumentSet.getArgument(name) == null) {
			return argumentSet;
		} else {
			return argumentSet.getArgument(name);
		}
	}
	
	/**
	 * @return
	 */
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
	
	/**
	 * @return
	 */
	public boolean isCrashEmailDisabled() {
		return this.disableCrashArg.getValue();
	}
	
	/**
	 * Calling this method, the specified setting file will be parsed (iff option -DrepoSuiteSettings is set) before
	 * parsing given JavaVM arguments. Options set in setting file will be overwritten by command line arguments.
	 */
	public void parseArguments() {
		for (final Entry<Object, Object> entry : this.properties.entrySet()) {
			final String argName = entry.getKey().toString().trim();
			final String value = entry.getValue().toString().trim();
			
			if (this.argumentSets.containsKey(argName)) {
				getArgument(argName).setStringValue(value);
				
			}
		}
		
		for (final AndamaArgumentSet<?> argument : this.argumentSets.values()) {
			if (!((AndamaArgumentSet<?>) argument).init()) {
				if (Logger.logError()) {
					Logger.error("Could not initialize " + argument
					        + ". Please see error earlier error messages, refer to the argument help information, "
					        + "or review the init() method of the corresponding " + argument.getHandle() + ".");
				}
				System.err.println(getHelpString());
				throw new Shutdown("Could not initialize " + argument
				        + ". Please see error earlier error messages, refer to the argument help information, "
				        + "or review the init() method of the corresponding " + argument.getHandle() + ".");
			} else {
				if (argument instanceof AndamaArgumentSet<?>) {
					if (!((AndamaArgumentSet<?>) argument).initSubArguments()) {
						if (Logger.logError()) {
							Logger.error("Could not initialize sub-arguments of "
							        + argument
							        + ". Please see error earlier error messages, refer to the argument help information, "
							        + "or review the init() method of the corresponding " + argument.getHandle() + ".");
						}
						System.err.println(getHelpString());
						throw new Shutdown("Could not initialize sub-arguments of " + argument
						        + ". Please see error earlier error messages, refer to the argument help information, "
						        + "or review the init() method of the corresponding " + argument.getHandle() + ".");
					}
				}
			}
		}
		
		if (!validateSettings()) {
			System.err.println(getHelpString());
			throw new Shutdown();
		}
		
		if (this.helpArg.getValue()) {
			System.err.println(getHelpString());
			throw new Shutdown();
		}
		
		if (Logger.logInfo()) {
			Logger.info("Using settings: ");
			Logger.info(toString());
		}
	}
	
	/**
	 * @param file
	 */
	public void save(final File file) {
		// TODO: getRootArgumentSet() and store tree to file
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
		((AndamaArgument<?>) getArgument(argument)).setStringValue(value);
	}
	
	/**
	 * Add the settings set for logger.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws DuplicateArgumentException
	 */
	public LoggerArguments setLoggerArg(final Requirement requirements) {
		return new LoggerArguments(getRootArgumentSet(), requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(AndamaSettings.class.getSimpleName() + ":");
		builder.append(FileUtils.lineSeparator);
		
		for (int i = 0; i < (AndamaSettings.class.getSimpleName().length() + ":".length()); ++i) {
			builder.append('-');
		}
		//
		// String passwordMask = "******** (masked)";
		// int maxNameLength = 0;
		// int maxValueLength = passwordMask.length();
		final TreeSet<AndamaArgumentSet<?>> set = new TreeSet<AndamaArgumentSet<?>>(this.argumentSets.values());
		// for (AndamaArgumentSet<?> arg : set) {
		// if (arg.getValue() != null) {
		// if (arg.getName().length() > maxNameLength) {
		// maxNameLength = arg.getName().length();
		// }
		//
		// if (arg.getValue().toString().length() > maxValueLength) {
		// maxValueLength = arg.getValue().toString().length();
		// }
		// }
		// }
		
		for (final AndamaArgumentInterface<?> arg : set) {
			if (arg.getValue() != null) {
				builder.append(FileUtils.lineSeparator);
				// new Formatter();
				// builder.append(formatter.format("%-" + maxNameLength +
				// "s : %-" + maxValueLength + "s (%s)",
				// arg.getName(), arg instanceof MaskedStringArgument
				// ? passwordMask
				// : arg.getValue(),
				// arg.toString()));
				builder.append(arg.toString());
				
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Check if all required arguments are set.
	 * 
	 * @return <code>null</code> if all required arguments are set. Returns the required argument with no value set
	 *         first found.
	 */
	private boolean validateSettings() {
		// Set<AndamaArgument<?>> defaultValueArgs = new
		// HashSet<AndamaArgument<?>>();
		//
		// for (AndamaArgumentSet<?> args : this.argumentSets.values()) {
		// for (AndamaArgument<?> argument : args.getArguments().values()) {
		// if (!argument.wasSet()) {
		// if (this.noDefaultValueArg.getValue()) {
		// argument.setStringValue(null);
		// } else if ((argument.getDefaultValue() != null) &&
		// argument.getRequirements().check()) {
		// defaultValueArgs.add(argument);
		// }
		// }
		// if (argument.getRequirements().check() && (argument.getValue() ==
		// null)) {
		// if (Logger.logError()) {
		// Logger.error("Required argument `" + arg.getName() +
		// "` is not set.");
		// }
		// return false;
		// }
		// }
		//
		// if (defaultValueArgs.size() > 0) {
		// StringBuilder sb = new StringBuilder();
		// sb.append("ARGUMENT WARNING: The following required arguments were not set and their default values will be used:");
		// sb.append(FileUtils.lineSeparator);
		// for (AndamaArgument<?> arg : defaultValueArgs) {
		// sb.append(arg.getName());
		// sb.append(": ");
		// sb.append(arg.getDefaultValue());
		// sb.append(FileUtils.lineSeparator);
		// }
		// sb.append("Use -DdenyDefaultValues=T to allow only manually set arguments");
		// if (Logger.logWarn()) {
		// Logger.warn(sb.toString());
		// }
		// }
		
		return true;
	}
	
}

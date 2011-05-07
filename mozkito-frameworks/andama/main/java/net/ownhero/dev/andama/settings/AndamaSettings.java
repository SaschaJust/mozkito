package net.ownhero.dev.andama.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.utils.AndamaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class AndamaSettings {
	
	private final Logger                   logger          = LoggerFactory.getLogger(this.getClass());
	public static final boolean            debug           = (System.getProperty("debug") != null);
	public static final String             reportThis      = "Please file a bug report with this error message here: "
	                                                               + "https://hg.st.cs.uni-saarland.de/projects/reposuite/issues/new";
	
	private Map<String, AndamaArgument<?>> arguments       = new HashMap<String, AndamaArgument<?>>();
	private final Map<String, String>      toolInformation = new HashMap<String, String>();
	private Properties                     commandlineProps;
	
	private final BooleanArgument          noDefaultValueArg;
	private final BooleanArgument          helpArg;
	private final BooleanArgument          disableCrashArg;
	@SuppressWarnings ("unused")
	private final StringArgument           settingsArg;
	
	public AndamaSettings() {
		this.noDefaultValueArg = new BooleanArgument(this, "denyDefaultValues", "Ignore default values!", "false",
		                                             false);
		this.helpArg = new BooleanArgument(this, "help", "Shows this help menu.", "false", false);
		this.disableCrashArg = new BooleanArgument(this, "disableCrashEmail",
		                                           "If set to `true` no crash emails will be send!", "false", false);
		this.settingsArg = new StringArgument(
		                                      this,
		                                      "settings",
		                                      "Setting file that contains the JavaVM arguments for the current repo suite task.",
		                                      null, false);
		
	}
	
	/**
	 * adds an argument to the repo suite settings. Leave default value
	 * <code>null</code> if none to be set.
	 * 
	 * @param name
	 *            Name of the JavaVM argument (-D<name>
	 * @param description
	 *            Short description that will be displayed when requesting help
	 * @param defaultValue
	 *            String that will be set as default value. If none to be set
	 *            pass <code>null</code>.
	 * @return <code>true</code> if the argument could be added.
	 *         <code>False</code> otherwise.
	 */
	protected boolean addArgument(@NotNull final AndamaArgument<?> argument) {
		if (this.arguments.containsKey(argument.getName())) {
			return false;
		}
		this.arguments.put(argument.getName(), argument);
		return true;
	}
	
	/**
	 * Adds a set of arguments to the repo suite settings. Only if all arguments
	 * can be added, the set will be added and the method returns true.
	 * 
	 * @param argSet
	 * @return <code>true</code> if all arguments in the set could be added.
	 */
	protected boolean addArgumentSet(final AndamaArgumentSet<?> argSet) {
		
		HashMap<String, AndamaArgument<?>> tmpArguments = new HashMap<String, AndamaArgument<?>>(this.arguments);
		for (AndamaArgument<?> argument : argSet.getArguments().values()) {
			if (tmpArguments.containsKey(argument.getName())) {
				return false;
			}
			tmpArguments.put(argument.getName(), argument);
		}
		this.arguments = tmpArguments;
		return true;
	}
	
	/**
	 * @param tool
	 * @param information
	 */
	protected void addToolInformation(final String tool,
	                                  final String information) {
		this.toolInformation.put(tool, information);
	}
	
	/**
	 * Return the arguments registered at this settings.
	 * 
	 * @return
	 */
	public Collection<AndamaArgument<?>> getArguments() {
		return this.arguments.values();
	}
	
	/**
	 * Return the help string that will contain all possible command line
	 * arguments.
	 * 
	 * @return
	 */
	public String getHelpString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Available JavaVM arguments:");
		builder.append(AndamaUtils.lineSeparator);
		
		TreeSet<AndamaArgument<?>> arguments = new TreeSet<AndamaArgument<?>>();
		arguments.addAll(this.arguments.values());
		
		int length = 0;
		
		for (AndamaArgument<?> argument : arguments) {
			length = Math.max(length, argument.getName().length());
		}
		
		String format = "\t-D%-" + length + "s\t%s%s" + AndamaUtils.lineSeparator;
		
		for (AndamaArgument<?> argument : arguments) {
			builder.append(String.format(format, argument.getName() + ":", argument.getDescription().trim(),
			                             argument.isRequired()
			                                                  ? " (required)"
			                                                  : ""));
		}
		
		return builder.toString();
	}
	
	/**
	 * @param name
	 * @return
	 */
	public AndamaArgument<?> getSetting(final String name) {
		return this.arguments.get(name);
	}
	
	/**
	 * @return
	 */
	public String getToolInformation() {
		StringBuilder builder = new StringBuilder();
		
		for (String tool : this.toolInformation.keySet()) {
			builder.append("[[");
			builder.append(tool);
			builder.append("]]");
			builder.append(AndamaUtils.lineSeparator);
			builder.append(this.toolInformation.get(tool));
			builder.append(AndamaUtils.lineSeparator);
			builder.append(AndamaUtils.lineSeparator);
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
	 * Calling this method, the specified setting file will be parsed (iff
	 * option -DrepoSuiteSettings is set) before parsing given JavaVM arguments.
	 * Options set in setting file will be overwritten by command line
	 * arguments.
	 */
	public void parseArguments() {
		
		if (this.helpArg.getValue()) {
			System.err.println(getHelpString());
			throw new Shutdown("Help requested.");
		}
		
		// save given arguments to load if necessary
		this.commandlineProps = (Properties) System.getProperties().clone();
		
		if (System.getProperty("repoSuiteSettings") != null) {
			boolean parseSettingFile = true;
			File settingFile = new File(System.getProperty("repoSuiteSettings"));
			if (!settingFile.exists()) {
				if (this.logger.isWarnEnabled()) {
					this.logger.warn("Specified repoSuite setting file `" + settingFile.getAbsolutePath()
					        + "` does not exists. Ignoring ...");
				}
				parseSettingFile = false;
			} else if (settingFile.isDirectory()) {
				if (this.logger.isWarnEnabled()) {
					this.logger.warn("Specified repoSuite setting file `" + settingFile.getAbsolutePath()
					        + "` is a directory. Ignoring ...");
				}
				parseSettingFile = true;
			}
			
			// parse setting file
			if (parseSettingFile) {
				try {
					System.getProperties().load(new FileInputStream(settingFile));
				} catch (FileNotFoundException e) {
					throw new Shutdown(e.getMessage(), e);
				} catch (IOException e) {
					throw new Shutdown(e.getMessage(), e);
				}
			}
			
			for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
				String argName = entry.getKey().toString().trim();
				String value = entry.getValue().toString().trim();
				if (this.arguments.containsKey(argName)) {
					this.arguments.get(argName).setValue(value);
				}
			}
		}
		
		for (Entry<Object, Object> entry : this.commandlineProps.entrySet()) {
			String argName = entry.getKey().toString().trim();
			String value = entry.getValue().toString().trim();
			
			if ((this.arguments.containsKey(argName)) && (!System.getProperties().contains(argName))) {
				this.arguments.get(argName).setValue(value);
			}
		}
		
		if (!validateSettings()) {
			System.err.println(getHelpString());
			throw new Shutdown("Invalid settings.");
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
		if (!this.arguments.containsKey(argument)) {
			throw new NoSuchFieldException("Argument could not be set in MinerSettings. "
			        + "The argument is not part of the current argument set.");
		}
		
		this.arguments.get(argument).setValue(value);
	}
	
	/**
	 * Add the settings set for logger.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws DuplicateArgumentException
	 */
	public LoggerArguments setLoggerArg(final boolean required) {
		return new LoggerArguments(this, required);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(AndamaSettings.class.getSimpleName() + ":");
		builder.append(AndamaUtils.lineSeparator);
		
		for (int i = 0; i < AndamaSettings.class.getSimpleName().length() + ":".length(); ++i) {
			builder.append('-');
		}
		
		String passwordMask = "******** (masked)";
		int maxNameLength = 0;
		int maxValueLength = passwordMask.length();
		TreeSet<AndamaArgument<?>> set = new TreeSet<AndamaArgument<?>>(this.arguments.values());
		for (AndamaArgument<?> arg : set) {
			if (arg.getValue() != null) {
				if (arg.getName().length() > maxNameLength) {
					maxNameLength = arg.getName().length();
				}
				
				if (arg.getValue().toString().length() > maxValueLength) {
					maxValueLength = arg.getValue().toString().length();
				}
			}
		}
		
		for (AndamaArgument<?> arg : set) {
			if (arg.getValue() != null) {
				builder.append(AndamaUtils.lineSeparator);
				Formatter formatter = new Formatter();
				builder.append(formatter.format("%-" + maxNameLength + "s : %-" + maxValueLength + "s (%s)",
				                                arg.getName(), arg instanceof MaskedStringArgument
				                                                                                  ? passwordMask
				                                                                                  : arg.getValue(),
				                                arg.toString()));
				
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Check if all required arguments are set.
	 * 
	 * @return <code>null</code> if all required arguments are set. Returns the
	 *         required argument with no value set first found.
	 */
	private boolean validateSettings() {
		Set<AndamaArgument<?>> defaultValueArgs = new HashSet<AndamaArgument<?>>();
		
		for (AndamaArgument<?> arg : this.arguments.values()) {
			if (!arg.isSet()) {
				if (this.noDefaultValueArg.getValue()) {
					arg.setValue(null);
				} else if ((arg.getDefaultValue() != null) && arg.isRequired()) {
					defaultValueArgs.add(arg);
				}
			}
			if (arg.isRequired() && (arg.getValue() == null)) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("Required argument `" + arg.getName() + "` is not set.");
				}
				return false;
			}
		}
		
		if (defaultValueArgs.size() > 0) {
			StringBuilder builder = new StringBuilder();
			builder.append("ARGUMENT WARNING: The following required arguments were not set and their default values will be used:");
			builder.append(AndamaUtils.lineSeparator);
			
			for (AndamaArgument<?> arg : defaultValueArgs) {
				builder.append(arg.getName());
				builder.append(": ");
				builder.append(arg.getDefaultValue());
				builder.append(AndamaUtils.lineSeparator);
			}
			
			builder.append("Use -DdenyDefaultValues=T to allow only manually set arguments");
			if (this.logger.isWarnEnabled()) {
				this.logger.warn(builder.toString());
			}
		}
		
		return true;
	}
}

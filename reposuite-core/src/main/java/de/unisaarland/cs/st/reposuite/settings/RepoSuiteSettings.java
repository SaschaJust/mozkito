package de.unisaarland.cs.st.reposuite.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RepoSuiteSettings {
	
	public static final boolean            debug      = (System.getProperty("debug") != null);
	public static final String             reportThis = "Please file a bug report with this error message here: "
	                                                          + "https://hg.st.cs.uni-saarland.de/projects/reposuite/issues/new";
	
	private Map<String, RepoSuiteArgument> arguments  = new HashMap<String, RepoSuiteArgument>();
	
	private Properties                     commandlineProps;
	
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
	protected boolean addArgument(final RepoSuiteArgument argument) {
		assert (argument != null);
		
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
	protected boolean addArgumentSet(final RepoSuiteArgumentSet argSet) {
		
		HashMap<String, RepoSuiteArgument> tmpArguments = new HashMap<String, RepoSuiteArgument>(this.arguments);
		for (RepoSuiteArgument argument : argSet.getArguments().values()) {
			if (tmpArguments.containsKey(argument.getName())) {
				return false;
			}
			tmpArguments.put(argument.getName(), argument);
		}
		this.arguments = tmpArguments;
		return true;
	}
	
	/**
	 * Return the arguments registered at this settings.
	 * 
	 * @return
	 */
	public Collection<RepoSuiteArgument> getArguments() {
		return this.arguments.values();
	}
	
	/**
	 * Return the help string that will contain all possible command line
	 * arguments.
	 * 
	 * @return
	 */
	public String getHelpString() {
		StringBuilder ss = new StringBuilder();
		ss.append("Available JavaVM arguments:");
		ss.append(System.getProperty("line.separator"));
		
		ss.append("\t");
		ss.append("-D");
		ss.append("repoSuiteSettings");
		ss.append(": ");
		ss.append("Setting file that contains the JavaVM arguments for the current repo suite task.");
		ss.append(System.getProperty("line.separator"));
		
		for (String argName : this.arguments.keySet()) {
			ss.append("\t");
			ss.append("-D");
			ss.append(argName);
			ss.append(": ");
			ss.append(this.arguments.get(argName).getDescription());
			if (this.arguments.get(argName).isRequired()) {
				ss.append(" (required!)");
			}
			ss.append(System.getProperty("line.separator"));
		}
		return ss.toString();
	}
	
	/**
	 * Calling this method, the specified setting file will be parsed (iff
	 * option -DrepoSuiteSettings is set) before parsing given JavaVM arguments.
	 * Options set in setting file will be overwritten by command line
	 * arguments.
	 */
	public void parseArguments() {
		
		// save given arguments to load if necessary
		this.commandlineProps = System.getProperties();
		
		if (System.getProperty("repoSuiteSettings") != null) {
			boolean parseSettingFile = true;
			File settingFile = new File(System.getProperty("repoSuiteSettings"));
			if (!settingFile.exists()) {
				if (Logger.logWarn()) {
					Logger.warn("Specified repoSuite setting file `" + settingFile.getAbsolutePath()
					        + "` does not exists. Ignoring ...");
				}
				parseSettingFile = false;
			} else if (settingFile.isDirectory()) {
				if (Logger.logWarn()) {
					Logger.warn("Specified repoSuite setting file `" + settingFile.getAbsolutePath()
					        + "` is a directory. Ignoring ...");
				}
				parseSettingFile = true;
			}
			
			// parse setting file
			if (parseSettingFile) {
				try {
					System.getProperties().load(new FileInputStream(settingFile));
				} catch (FileNotFoundException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage());
					}
					throw new RuntimeException();
				} catch (IOException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage());
					}
					throw new RuntimeException();
				}
			}
			for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
				String argName = entry.getKey().toString();
				String value = entry.getValue().toString();
				if (this.arguments.containsKey(argName)) {
					this.arguments.get(argName).setStringValue(value);
				}
			}
		}
		
		for (Entry<Object, Object> entry : this.commandlineProps.entrySet()) {
			String argName = entry.getKey().toString();
			String value = entry.getValue().toString();
			if ((this.arguments.containsKey(argName)) && (!System.getProperties().contains(argName))) {
				this.arguments.get(argName).setStringValue(value);
			}
		}
		
		if (!validateSettings()) {
			System.err.println(getHelpString());
			throw new RuntimeException();
		}
	}
	
	/**
	 * Add the settings set for the database.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the database settings required.
	 * @return
	 * @throws DuplicateArgumentException
	 */
	public DatabaseArguments setDatabaseArgs(final boolean isRequired) {
		DatabaseArguments minerDatabaseArguments = new DatabaseArguments(this, isRequired);
		return minerDatabaseArguments;
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
	protected void setField(final String argument, final String value) throws NoSuchFieldException {
		if (!this.arguments.containsKey(argument)) {
			throw new NoSuchFieldException("Argument could not be set in MinerSettings. "
			        + "The argument is not part of the current argument set.");
		}
		this.arguments.get(argument).setStringValue(value);
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
	
	/**
	 * Add the repository argument set.
	 * 
	 * @param isRequired
	 *            Set to <code>true</code> if the repository settings are
	 *            required.
	 * @return
	 * @throws DuplicateArgumentException
	 */
	public RepositoryArguments setRepositoryArg(final boolean isRequired) {
		RepositoryArguments minerRepoArgSet = new RepositoryArguments(this, isRequired);
		return minerRepoArgSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(RepoSuiteSettings.class.getSimpleName() + ":");
		builder.append(System.getProperty("line.separator"));
		
		for (int i = 0; i < builder.length(); ++i) {
			builder.append('-');
		}
		
		for (RepoSuiteArgument arg : this.arguments.values()) {
			builder.append(System.getProperty("line.separator"));
			builder.append(arg.toString());
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
		for (RepoSuiteArgument arg : this.arguments.values()) {
			if (arg.isRequired() && (arg.getValue() == null)) {
				if (Logger.logError()) {
					Logger.error("Required argument `" + arg.getName() + "` is not set.");
				}
				return false;
			}
		}
		return true;
	}
}

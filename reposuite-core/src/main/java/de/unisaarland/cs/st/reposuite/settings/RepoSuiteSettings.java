package de.unisaarland.cs.st.reposuite.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

public class RepoSuiteSettings {
	
	public static final boolean                      debug = (System.getProperty("debug") != null);
	private final HashMap<String, RepoSuiteArgument> arguments;
	
	private Properties                               commandlineProps;
	
	public RepoSuiteSettings() {
		this.arguments = new HashMap<String, RepoSuiteArgument>();
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
	 */
	protected void addArgument(RepoSuiteArgument argument) throws DuplicateArgumentException {
		
		if (this.arguments.containsKey(argument.getName())) {
			throw new DuplicateArgumentException(argument);
		}
		this.arguments.put(argument.getName(), argument);
	}
	
	/**
	 * Adds a set of arguments to the repo suite settings.
	 * 
	 * @param argSet
	 * @throws DuplicateArgumentException
	 */
	protected void addArgumentSet(RepoSuiteArgumentSet argSet) throws DuplicateArgumentException {
		
		for (RepoSuiteArgument argument : argSet.getArguments()) {
			if (this.arguments.containsKey(argument.getName())) {
				throw new DuplicateArgumentException(argument);
			}
			this.arguments.put(argument.getName(), argument);
		}
	}
	
	public Collection<RepoSuiteArgument> getArguments() {
		return this.arguments.values();
	}
	
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
				Logger.getLogger(RepoSuiteSettings.class).warn(
				        "Specified repoSuite setting file `" + settingFile.getAbsolutePath()
				                + "` does not exists. Ignoring ...");
				parseSettingFile = false;
			} else if (settingFile.isDirectory()) {
				Logger.getLogger(RepoSuiteSettings.class).warn(
				        "Specified repoSuite setting file `" + settingFile.getAbsolutePath()
				                + "` is a directory. Ignoring ...");
				parseSettingFile = true;
			}
			
			// parse setting file
			if (parseSettingFile) {
				try {
					System.getProperties().load(new FileInputStream(settingFile));
				} catch (FileNotFoundException e) {
					Logger.getLogger(RepoSuiteSettings.class).fatal(e);
					throw new RuntimeException();
				} catch (IOException e) {
					Logger.getLogger(RepoSuiteSettings.class).fatal(e);
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
		
		try {
			validateSettings();
		} catch (MissingRequiredArgumentException e) {
			Logger.getLogger(RepoSuiteSettings.class).error(e.getMessage());
			System.err.println(getHelpString());
			System.exit(-1);
		}
	}
	
	public DatabaseArguments setDatabaseArgs(boolean isRequired) throws DuplicateArgumentException {
		DatabaseArguments minerDatabaseArguments = new DatabaseArguments(this, isRequired);
		return minerDatabaseArguments;
	}
	
	protected void setField(String argument, String value) throws NoSuchFieldException {
		if (!this.arguments.containsKey(argument)) {
			throw new NoSuchFieldException("Argument could not be set in MinerSettings. "
			        + "The argument is not part of the current argument set.");
		}
		this.arguments.get(argument).setStringValue(value);
	}
	
	public RepositoryArguments setRepositoryArg(boolean isRequired) throws DuplicateArgumentException {
		RepositoryArguments minerRepoArgSet = new RepositoryArguments(this, isRequired);
		return minerRepoArgSet;
	}
	
	private void validateSettings() throws MissingRequiredArgumentException {
		for (RepoSuiteArgument arg : this.arguments.values()) {
			if (arg.isRequired() && (arg.getValue() == null)) {
				throw new MissingRequiredArgumentException(arg);
			}
		}
	}
	
}

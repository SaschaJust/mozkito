package de.unisaarland.cs.st.reposuite.settings;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.LogLevel;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoggerArguments extends RepoSuiteArgumentSet {
	
	/**
	 * @param settings
	 * @param isRequired
	 */
	public LoggerArguments(final RepoSuiteSettings settings, final boolean isRequired) {
		super();
		
		LogLevel[] values = LogLevel.values();
		String[] argEnums = new String[values.length];
		for (int i = 0; i < argEnums.length; ++i) {
			argEnums[i] = values[i].toString();
		}
		
		new EnumArgument(settings, "log.console.level", "determines the log level for the console log", "WARN", false,
		                 argEnums);
		new EnumArgument(settings, "log.file.level", "determines the log level for the oevrall log file", "INFO",
		                 false, argEnums);
		new OutputFileArgument(settings, "log.file", "specifies the path to a file the file log shall be written to.",
		                       "./.log", false, true);
		new StringArgument(
		                   settings,
		                   "log.class.<class_name>",
		                   "This is a special option. You can use also multiple of them. Should be used to specify individual log level for particular classes (also works for categories). Replace <class_name> by the name of the class (full qualified, e.g. net.ownhero.dev.kisa.Logger). The value of the option is the log level to be set for this particula class. Optionally you can also a file path the class log will be written to behind the log level, seperated by a colon. The complete format would look like this:"
		                           + FileUtils.lineSeparator + "-Dlog.class.<class_name>=<log_level>[,<log_file_path>]",
		                   null, false);
		System.setProperty("log.class.org.tmatesoft.svn", "WARN");
		System.setProperty("log.class.org.hibernate", "WARN");
		System.setProperty("log.class.org.hibernate.type", "WARN");
		System.setProperty("log.class.org.apache.http", "WARN");
		System.setProperty("log.class.org.apache.http.wire", "ERROR");
		System.setProperty("log.class.openjpa.Tool", "INFO");
		System.setProperty("log.class.openjpa.Runtime", "INFO");
		System.setProperty("log.class.openjpa.Remote", "WARN");
		System.setProperty("log.class.openjpa.DataCache", "WARN");
		System.setProperty("log.class.openjpa.MetaData", "WARN");
		System.setProperty("log.class.openjpa.Enhance", "WARN");
		System.setProperty("log.class.openjpa.Query", "WARN");
		System.setProperty("log.class.openjpa.jdbc.SQL", "WARN");
		System.setProperty("log.class.openjpa.jdbc.JDBC", "WARN");
		System.setProperty("log.class.openjpa.jdbc.Schema", "WARN");
		// this.logConfig = new InputFileArgument(settings, "log.config",
		// "Configuration file for the logging engine",
		// null, false);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Boolean getValue() {
		return true;
	}
	
}

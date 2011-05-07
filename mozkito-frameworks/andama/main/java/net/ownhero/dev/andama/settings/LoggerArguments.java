package net.ownhero.dev.andama.settings;

import java.net.URL;

import net.ownhero.dev.andama.utils.LogLevel;
import net.ownhero.dev.andama.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoggerArguments extends AndamaArgumentSet<Boolean> {
	
	private final InputFileArgument logConfig;
	private final EnumArgument      logLevel;
	
	/**
	 * @param settings
	 * @param isRequired
	 */
	public LoggerArguments(final AndamaSettings settings, final boolean isRequired) {
		super();
		
		LogLevel[] values = LogLevel.values();
		String[] argEnums = new String[values.length];
		for (int i = 0; i < argEnums.length; ++i) {
			argEnums[i] = values[i].toString();
		}
		
		this.logLevel = new EnumArgument(settings, "log.level", "determines the log level", "WARN", false, argEnums);
		this.logConfig = new InputFileArgument(settings, "log.config", "Configuration file for the logging engine",
		                                       null, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public Boolean getValue() {
		if (this.logConfig.getValue() != null) {
			Logger.readConfiguration(this.logConfig.getValue().getAbsolutePath());
		} else {
			URL url = Logger.class.getResource("log4j.properties");
			if (url != null) {
				Logger.readConfiguration(url);
			}
		}
		
		if (this.logLevel.getValue() != null) {
			Logger.setLogLevel(LogLevel.valueOf(this.logLevel.getValue()));
		}
		
		return true;
	}
	
}

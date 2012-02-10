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
package net.ownhero.dev.andama.settings;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoggerArguments extends AndamaArgumentSet<Boolean> {
	
	private final EnumArgument<LogLevel>   consoleLevel;
	private final EnumArgument<LogLevel>   fileLevel;
	private final LoggerOutputFileArgument filePath;
	
	/**
	 * @param settings
	 * @param isRequired
	 * @throws ArgumentRegistrationException
	 */
	public LoggerArguments(@NotNull final AndamaArgumentSet<?> argumentSet, @NotNull final Requirement requirements)
	        throws ArgumentRegistrationException {
		super(argumentSet, "Used to configure kisa logging options.", requirements);
		
		this.consoleLevel = new EnumArgument<LogLevel>(this, "log.console.level",
		                                               "determines the log level for the console log", LogLevel.WARN,
		                                               new Optional());
		this.fileLevel = new EnumArgument<LogLevel>(this, "log.file.level",
		                                            "determines the log level for the oevrall log file", LogLevel.INFO,
		                                            new Optional());
		this.filePath = new LoggerOutputFileArgument(this, "log.file",
		                                             "specifies the path to a file the file log shall be written to.",
		                                             "./.log", new Optional(), true);
		new StringArgument(
		                   this,
		                   "log.class.<class_name>",
		                   "This is a special option. You can use also multiple of them. Should be used to specify individual log level for particular classes (also works for categories). Replace <class_name> by the name of the class (full qualified, e.g. net.ownhero.dev.kisa.Logger). The value of the option is the log level to be set for this particula class. Optionally you can also a file path the class log will be written to behind the log level, seperated by a colon. The complete format would look like this:"
		                           + FileUtils.lineSeparator + "-Dlog.class.<class_name>=<log_level>[,<log_file_path>]",
		                   null, new Optional());
		
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
	}
	
	/**
	 * @return the consoleLevel
	 */
	public final EnumArgument<LogLevel> getConsoleLevel() {
		return this.consoleLevel;
	}
	
	/**
	 * @return the fileLevel
	 */
	public final EnumArgument<LogLevel> getFileLevel() {
		return this.fileLevel;
	}
	
	/**
	 * @return the filePath
	 */
	public final LoggerOutputFileArgument getFilePath() {
		return this.filePath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentSet#init()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						Logger.readConfiguration();
						setCachedValue(true);
						ret = true;
					} else {
						ret = true;
					}
				}
			} else {
				ret = true;
			}
			
			return ret;
		} finally {
			if (ret) {
				Condition.check(isInitialized(), "If init() returns true, the %s has to be set to initialized.",
				                getHandle());
			}
		}
	}
}

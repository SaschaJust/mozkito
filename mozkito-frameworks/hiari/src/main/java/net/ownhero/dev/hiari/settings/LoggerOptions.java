/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoggerOptions extends ArgumentSetOptions<Boolean, ArgumentSet<Boolean, LoggerOptions>> {
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param requirements
	 */
	public LoggerOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "log", "Configures the logging options.", requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	public Boolean init() {
		// PRECONDITIONS
		Logger.readConfiguration();
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSetOptions#requirements(net.ownhero.dev.andama.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
		
		try {
			map.put("console.level", new EnumArgument.Options<LogLevel>(set, "consoleLevel",
			                                                            "determines the log level for the console log",
			                                                            LogLevel.WARN, Requirement.optional));
			map.put("file.level",
			        new EnumArgument.Options<LogLevel>(set, "fileLevel",
			                                           "determines the log level for the oevrall log file",
			                                           LogLevel.INFO, Requirement.optional));
			map.put("file",
			        new OutputFileArgument.Options(set, "file",
			                                       "specifies the path to a file the file log shall be written to.",
			                                       new File(".log"), Requirement.optional, true));
			map.put("class.<class_name>",
			        new StringArgument.Options(
			                                   set,
			                                   "class<class_name>",
			                                   "This is a special option. You can use also multiple of them. Should be used to specify individual log level for particular classes (also works for categories). Replace <class_name> by the name of the class (full qualified, e.g. net.ownhero.dev.kisa.Logger). The value of the option is the log level to be set for this particula class. Optionally you can also a file path the class log will be written to behind the log level, seperated by a colon. The complete format would look like this: "
			                                           + "-Dlog.class<class_name>=<log_level>[,<log_file_path>]", null,
			                                   Requirement.optional));
			
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
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
}

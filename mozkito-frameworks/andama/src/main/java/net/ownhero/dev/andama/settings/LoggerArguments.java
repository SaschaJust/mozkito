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

import net.ownhero.dev.andama.settings.dependencies.Requirement;
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class LoggerArguments extends AndamaArgumentSet<Boolean> {
	
	/**
	 * @param settings
	 * @param isRequired
	 */
	public LoggerArguments(final AndamaArgumentSet<?> argumentSet, final Requirement requirements) {
		super(argumentSet, "Used to configure kisa logging options.", requirements);
		
		final LogLevel[] values = LogLevel.values();
		final String[] argEnums = new String[values.length];
		for (int i = 0; i < argEnums.length; ++i) {
			argEnums[i] = values[i].toString();
		}
		
		new EnumArgument(argumentSet, "log.console.level", "determines the log level for the console log", "WARN",
		                 new Optional(), argEnums);
		new EnumArgument(argumentSet, "log.file.level", "determines the log level for the oevrall log file", "INFO",
		                 new Optional(), argEnums);
		new LoggerOutputFileArgument(argumentSet, "log.file",
		                             "specifies the path to a file the file log shall be written to.", "./.log",
		                             new Optional(), true);
		new StringArgument(
		                   argumentSet,
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
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentSet#init()
	 */
	@Override
	protected final boolean init() {
		if (!isInitialized()) {
			synchronized (this) {
				if (!isInitialized()) {
					Logger.readConfiguration();
					setCachedValue(true);
					return true;
				}
			}
		}
		return true;
	}
	
}

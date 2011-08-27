/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import java.net.URL;

import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

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

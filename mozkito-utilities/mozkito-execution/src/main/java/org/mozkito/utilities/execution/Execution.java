/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.utilities.execution;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Execution {
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command) {
		return create(command, new String[0]);
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments) {
		return create(command, arguments, null);
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments,
	                                final File dir) {
		return create(command, arguments, dir, null);
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @param environment
	 *            the environment
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments,
	                                final File dir,
	                                final Map<String, String> environment) {
		return create(command, arguments, dir, environment, Charset.defaultCharset());
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @param environment
	 *            the environment
	 * @param charset
	 *            the charset
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments,
	                                final File dir,
	                                final Map<String, String> environment,
	                                @NotNull final Charset charset) {
		return new Executor(command, arguments, dir, environment, charset);
	}
	
}

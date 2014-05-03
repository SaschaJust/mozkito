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

package net.ownhero.dev.andama.observer;

import java.util.HashSet;
import java.util.Set;

/**
 * The Class Command.
 */
public abstract class Command {
	
	/** The valid arguments. */
	private final Set<Class<? extends Argument>> validArguments = new HashSet<Class<? extends Argument>>();
	
	/**
	 * Gets the valid arguments.
	 *
	 * @return the valid arguments
	 */
	public Set<Class<? extends Argument>> getValidArguments() {
		return validArguments;
	}
	
	/**
	 * Instantiates a new command.
	 *
	 * @param arguments the arguments
	 */
	public Command(Argument... arguments) {
		// TODO check if arguments ar valid
	}
	
	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {
		return this.getClass().getSimpleName()
		           .substring(0, this.getClass().getSimpleName().length() - Command.class.getSimpleName().length())
		           .toLowerCase();
	}
	
	/**
	 * Execute.
	 *
	 * @return true, if successful
	 */
	public abstract boolean execute();
	
}

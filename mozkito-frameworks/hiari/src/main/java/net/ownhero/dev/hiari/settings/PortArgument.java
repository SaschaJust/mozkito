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

import java.io.IOException;
import java.net.ServerSocket;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class PortArgument extends Argument<Integer, PortArgument.Options> {
	
	public static class Options extends ArgumentOptions<Integer, PortArgument> {
		
		private final boolean canBind;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param defaultValue
		 * @param requirements
		 */
		public Options(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
		        @NotNull @NotEmptyString final String description, @NotNull final Integer defaultValue,
		        @NotNull final Requirement requirements, final boolean canBind) {
			super(argumentSet, name, description, defaultValue, requirements);
			this.canBind = canBind;
		}
		
		/**
		 * @return the free
		 */
		public final boolean canBind() {
			// PRECONDITIONS
			
			try {
				return this.canBind;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.canBind, "Field '%s' in '%s'.", "free", getClass().getSimpleName());
			}
		}
		
	}
	
	/**
	 * @param options
	 * @throws ArgumentRegistrationException
	 */
	PortArgument(final Options options) throws ArgumentRegistrationException {
		super(options);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.Argument#init()
	 */
	@Override
	protected boolean init() {
		boolean ret = false;
		
		try {
			if (!validStringValue()) {
				if (required()) {
					if (Logger.logError()) {
						Logger.error("Argument required but doesn't have a valid string value (from options '%s').",
						             getOptions());
					}
				} else {
					if (Logger.logWarn()) {
						Logger.warn("Optional argument is not set: %s", getTag());
					}
					setCachedValue(null);
					ret = true;
				}
			} else {
				try {
					final Integer port = Integer.valueOf(getStringValue());
					if ((port < 0) || (port > 65535)) {
						if (Logger.logError()) {
							Logger.error("Port invalid. Ports have to be within the range 0-65535. Given: %s",
							             getStringValue());
						}
					} else {
						if (port < 1024) {
							if (Logger.logWarn()) {
								Logger.warn("Port '%s' is within the range of well-known ports (system range). On UNIX based systems, programs require superuser privileges to bind to these ports.",
								            getStringValue());
							}
						}
						
						if (getOptions().canBind()) {
							try (ServerSocket socket = new ServerSocket(port)) {
								setCachedValue(port);
								ret = true;
							} catch (final IOException e) {
								if (Logger.logError()) {
									Logger.error(e);
								}
							}
						} else {
							setCachedValue(port);
							ret = true;
						}
					}
					
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Value '%s' given for argument '%s' could not be interpreted as a port. Abort!",
						             getStringValue(), getTag());
					}
					
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
	
}

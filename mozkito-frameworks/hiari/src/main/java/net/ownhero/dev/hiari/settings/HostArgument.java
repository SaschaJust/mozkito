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
package net.ownhero.dev.hiari.settings;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * The Class HostArgument.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class HostArgument extends Argument<String, HostArgument.Options> {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentOptions<String, HostArgument> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param name
		 *            the name
		 * @param description
		 *            the description
		 * @param defaultValue
		 *            the default value
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final String name, final String description,
		        final String defaultValue, final Requirement requirements) {
			super(argumentSet, name, description, defaultValue, requirements);
		}
		
	}
	
	/**
	 * Instantiates a new host argument.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	HostArgument(final Options options) throws ArgumentRegistrationException {
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
					final String host = "http://" + getStringValue();
					final UrlValidator validator = new UrlValidator();
					final boolean valid = validator.isValid(host) || getStringValue().toLowerCase().equals("localhost");
					
					if (valid) {
						setCachedValue(getStringValue());
						ret = true;
					} else {
						if (Logger.logError()) {
							Logger.error("Hostname is not valid: %s", getStringValue());
						}
					}
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Value given for argument `" + getName()
						        + "` could not be interpreted as a Long value. Abort!");
					}
					
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
	
}

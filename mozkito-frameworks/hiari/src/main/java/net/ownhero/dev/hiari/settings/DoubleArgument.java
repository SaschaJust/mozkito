/*******************************************************************************
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
 ******************************************************************************/
package net.ownhero.dev.hiari.settings;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class DoubleArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class DoubleArgument extends Argument<Double, DoubleArgument.Options> {
	
	/**
	 * The Class DoubleArgumentOptions.
	 */
	public static class Options extends ArgumentOptions<Double, DoubleArgument> {
		
		/**
		 * Instantiates a new double argument options.
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
		        final Double defaultValue, final Requirement requirements) {
			super(argumentSet, name, description, defaultValue, requirements);
		}
		
	}
	
	/**
	 * Instantiates a new double argument.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 */
	protected DoubleArgument(@NotNull final Options options) throws ArgumentRegistrationException {
		super(options);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument#getValue()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!validStringValue()) {
				if (required()) {
					// TODO Error log
				} else {
					setCachedValue(null);
					ret = true;
				}
			} else {
				try {
					setCachedValue(Double.valueOf(getStringValue()));
					ret = true;
				} catch (final NumberFormatException e) {
					if (Logger.logError()) {
						Logger.error("Value given for argument `" + getName()
						        + "` could not be interpreted as a Double value. Abort!");
					}
					
					ret = false;
				}
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
}

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
package net.ownhero.dev.andama.settings.arguments;

import java.util.HashSet;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.Argument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.StringCondition;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ListArgument extends Argument<HashSet<String>> {
	
	private final String delimiter;
	
	/**
	 * General Arguments as described in RepoSuiteArgument. The string value
	 * will be split using delimiter `,` to receive the list of values.
	 * 
	 * @see Argument
	 * 
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws ArgumentRegistrationException
	 * @throws DuplicateArgumentException
	 */
	public ListArgument(final ArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final Requirement requirements) throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements);
		try {
			this.delimiter = ",";
		} finally {
			Condition.notNull(this.delimiter, "The delimiter in %s must not be null.", getHandle());
			StringCondition.notEmpty(this.delimiter, "The delimiter in %s must not be empty.", getHandle());
		}
	}
	
	/**
	 * 
	 * General Arguments as described in {@link Argument}
	 * 
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param requirements
	 * @param delimiter
	 *            The string value will be split using this delimiter to receive
	 *            the list of values
	 * @throws ArgumentRegistrationException
	 */
	public ListArgument(final ArgumentSet<?> argumentSet, final String name, final String description,
	        final String defaultValue, final Requirement requirements, final String delimiter)
	        throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements);
		try {
			this.delimiter = delimiter;
		} finally {
			Condition.notNull(this.delimiter, "The delimiter in %s must not be null.", getHandle());
			StringCondition.notEmpty(this.delimiter, "The delimiter in %s must not be empty.", getHandle());
			StringCondition.equals(this.delimiter,
			                       delimiter,
			                       "The delimiter in %s is not equal to the one give in the constructor ('%s' vs '%s').",
			                       getHandle(), this.delimiter, delimiter);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgument#init()
	 */
	@Override
	protected final boolean init() {
		boolean ret = false;
		
		try {
			if (!isInitialized()) {
				synchronized (this) {
					if (!isInitialized()) {
						if (getStringValue() == null) {
							setCachedValue(null);
							ret = true;
						} else {
							final HashSet<String> result = new HashSet<String>();
							
							for (final String s : getStringValue().split(this.delimiter)) {
								result.add(s.trim());
							}
							
							setCachedValue(result);
							ret = true;
						}
					} else {
						ret = true;
					}
				}
			} else {
				ret = true;
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
}

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

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.StringCondition;

/**
 * The Class ListArgument.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ListArgument extends Argument<List<String>, ListArgument.Options> {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends ArgumentOptions<List<String>, ListArgument> {
		
		/** The delimiter. */
		private String delimiter;
		
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
		public Options(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
		        @NotNull @NotEmptyString final String description, final List<String> defaultValue,
		        @NotNull final Requirement requirements) {
			super(argumentSet, name, description, defaultValue, requirements);
			
			try {
				this.delimiter = ",";
			} finally {
				Condition.notNull(this.delimiter, "The delimiter in %s must not be null.", getHandle());
				StringCondition.notEmpty(this.delimiter, "The delimiter in %s must not be empty.", getHandle());
			}
		}
		
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
		 * @param delimiter
		 *            the delimiter
		 */
		public Options(@NotNull final ArgumentSet<?, ?> argumentSet, @NotNull @NotEmptyString final String name,
		        @NotNull @NotEmptyString final String description, final List<String> defaultValue,
		        @NotNull final Requirement requirements, final String delimiter) {
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
		
		/**
		 * Gets the delimiter.
		 * 
		 * @return the delimiter
		 */
		public final String getDelimiter() {
			return this.delimiter;
		}
		
	}
	
	/** The delimiter. */
	private final String delimiter;
	
	/**
	 * General Arguments as described in RepoSuiteArgument. The string value will be split using delimiter `,` to
	 * receive the list of values.
	 * 
	 * @param options
	 *            the options
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @see Argument
	 */
	protected ListArgument(@NotNull final Options options) throws ArgumentRegistrationException {
		super(options);
		
		try {
			this.delimiter = options.getDelimiter();
		} finally {
			Condition.notNull(this.delimiter, "The delimiter in %s must not be null.", getHandle());
			StringCondition.notEmpty(this.delimiter, "The delimiter in %s must not be empty.", getHandle());
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
			
			if (getStringValue() == null) {
				setCachedValue(null);
				ret = true;
			} else {
				final List<String> result = new LinkedList<String>();
				
				for (final String s : getStringValue().split(this.delimiter)) {
					result.add(s.trim());
				}
				
				setCachedValue(result);
				ret = true;
			}
			
			return ret;
		} finally {
			__initPostCondition(ret);
		}
	}
	
}

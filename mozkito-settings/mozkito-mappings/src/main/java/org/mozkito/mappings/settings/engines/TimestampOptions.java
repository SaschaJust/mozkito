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

package org.mozkito.mappings.settings.engines;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.TupleArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.joda.time.Interval;

import org.mozkito.mappings.engines.TimestampEngine;
import org.mozkito.mappings.messages.Messages;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TimestampOptions extends
        ArgumentSetOptions<TimestampEngine, ArgumentSet<TimestampEngine, TimestampOptions>> {
	
	/** The interval option. */
	private net.ownhero.dev.hiari.settings.TupleArgument.Options intervalOption;
	
	/**
	 * Instantiates a new options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirements
	 *            the requirements
	 */
	public TimestampOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, TimestampEngine.TAG, TimestampEngine.DESCRIPTION, requirements);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
	 */
	@Override
	public TimestampEngine init() {
		// PRECONDITIONS
		
		try {
			final TupleArgument intervalArgument = getSettings().getArgument(this.intervalOption);
			final Tuple<String, String> tuple = intervalArgument.getValue();
			
			int start = 0;
			int end = 0;
			
			start = parseIntervalString(tuple.getFirst());
			end = parseIntervalString(tuple.getSecond());
			
			// inplace swap
			if (start > end) {
				start ^= end ^= start ^= end;
			}
			
			if (Logger.logInfo()) {
				Logger.info(Messages.getString("TimestampEngine.usingInterval") + " [", start + ", " + end + "]."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			
			return new TimestampEngine(new Interval((long) start * TimestampEngine.MS_IN_SECONDS, (long) end
			        * TimestampEngine.MS_IN_SECONDS));
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Parses the interval string.
	 * 
	 * @param string
	 *            the string
	 * @return the int
	 */
	private int parseIntervalString(final String string) {
		int value = 0;
		final Regex regex = new Regex(
		                              "\\s*[+-]?({days}[0-9]+)d\\s*({hours}[0-9]+)h\\s*({minutes}[0-9]+)m\\s*({seconds}[0-9]+)s", //$NON-NLS-1$
		                              Pattern.CASE_INSENSITIVE);
		final Match find = regex.find(string);
		
		if (find == null) {
			throw new Shutdown(
			                   Messages.getString("TimestampEngine.invalidInterval") + string + " " + Messages.getString("TimestampEngine.usingRegex") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			                           + regex.getPattern());
		} else {
			value += Integer.parseInt(regex.getGroup("days")) * TimestampEngine.SECONDS_IN_DAYS; //$NON-NLS-1$
			value += Integer.parseInt(regex.getGroup("hours")) * TimestampEngine.SECONDS_IN_HOURS; //$NON-NLS-1$
			value += Integer.parseInt(regex.getGroup("minutes")) * TimestampEngine.SECONDS_IN_MINUTES; //$NON-NLS-1$
			value += Integer.parseInt(regex.getGroup("seconds")); //$NON-NLS-1$
		}
		
		if (string.startsWith("-")) { //$NON-NLS-1$
			value *= -1;
		}
		
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
	                                                                                    SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			this.intervalOption = new TupleArgument.Options(argumentSet, "interval", //$NON-NLS-1$
			                                                Messages.getString("TimestampEngine.intervalDescription"), //$NON-NLS-1$
			                                                TimestampEngine.DEFAULT_INTERVAL, Requirement.required);
			map.put(this.intervalOption.getName(), this.intervalOption);
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

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
package de.unisaarland.cs.st.moskito.mapping.engines;

import java.util.List;
import java.util.regex.Pattern;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.TupleArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.History;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.persistence.model.EnumTuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class TimestampEngine extends MappingEngine {
	
	private static final String                description     = Messages.getString("TimestampEngine.description"); //$NON-NLS-1$
	                                                                                                                
	private static final Tuple<String, String> defaultInterval = new Tuple<String, String>("-0d 2h 0m 0s", //$NON-NLS-1$
	                                                                                       "+1d 0h 0m 0s");        //$NON-NLS-1$
	                                                                                                                
	/**
	 * @return the defaultinterval
	 */
	private static final Tuple<String, String> getDefaultinterval() {
		// PRECONDITIONS
		
		try {
			return defaultInterval;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(defaultInterval, "Field '%s' in '%s'.", "defaultInterval", //$NON-NLS-1$ //$NON-NLS-2$
			                  TimestampEngine.class.getSimpleName());
		}
	}
	
	private TupleArgument.Options intervalOption;
	private TupleArgument         intervalArgument;
	
	private Interval              interval;
	
	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return the interval
	 */
	private final Interval getInterval() {
		// PRECONDITIONS
		
		try {
			return this.interval;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.interval, "Field '%s' in '%s'.", "interval", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @return the intervalArgument
	 */
	private final TupleArgument getIntervalArgument() {
		// PRECONDITIONS
		
		try {
			return this.intervalArgument;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.intervalArgument, "Field '%s' in '%s'.", "intervalArgument", //$NON-NLS-1$ //$NON-NLS-2$
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the intervalOption
	 */
	private final TupleArgument.Options getIntervalOption() {
		// PRECONDITIONS
		
		try {
			return this.intervalOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.intervalOption, "Field '%s' in '%s'.", "intervalOption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		Condition.notNull(this.intervalOption, "Field '%s' in '%s'.", "intervalOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			setIntervalArgument(getSettings().getArgument(getIntervalOption()));
			Condition.notNull(this.intervalArgument, "Field '%s' in '%s'.", "intervalArgument", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			final Tuple<String, String> tuple = getIntervalArgument().getValue();
			
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
			
			setInterval((new Interval(start * 1000, end * 1000)));
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param string
	 * @return
	 */
	private int parseIntervalString(final String string) {
		int value = 0;
		final Regex regex = new Regex(
		                              "\\s*[+-]?({days}[0-9]+)d\\s*({hours}[0-9]+)h\\s*({minutes}[0-9]+)m\\s*({seconds}[0-9]+)s", //$NON-NLS-1$
		                              Pattern.CASE_INSENSITIVE);
		final List<RegexGroup> find = regex.find(string);
		
		if (find == null) {
			throw new Shutdown(
			                   Messages.getString("TimestampEngine.invalidInterval") + string + " " + Messages.getString("TimestampEngine.usingRegex") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			                           + regex.getPattern());
		} else {
			value += Integer.parseInt(regex.getGroup("days")) * 24 * 60 * 60; //$NON-NLS-1$
			value += Integer.parseInt(regex.getGroup("hours")) * 60 * 60; //$NON-NLS-1$
			value += Integer.parseInt(regex.getGroup("minutes")) * 60; //$NON-NLS-1$
			value += Integer.parseInt(regex.getGroup("seconds")); //$NON-NLS-1$
		}
		
		if (string.startsWith("-")) { //$NON-NLS-1$
			value *= -1;
		}
		
		return value;
	}
	
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		setSettings(root.getSettings());
		Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// request the mapping.engines anchor
		final ArgumentSet<?, ?> anchor = super.getAnchor(getSettings());
		
		try {
			
			setIntervalOption(new TupleArgument.Options(anchor, "interval", //$NON-NLS-1$
			                                            Messages.getString("TimestampEngine.intervalDescription"), //$NON-NLS-1$
			                                            getDefaultinterval(), Requirement.required));
			
			return anchor;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.intervalOption, "Field '%s' in '%s'.", "intervalOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(anchor, "Field '%s' in '%s'.", "anchor", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final Mapping score) {
		double value = 0d;
		
		final DateTime element1Timestamp = ((DateTime) element1.get(FieldKey.CREATION_TIMESTAMP));
		final DateTime element2CreationTimestamp = ((DateTime) element2.get(FieldKey.CREATION_TIMESTAMP));
		final DateTime element2ResolutionTimestamp = ((DateTime) element2.get(FieldKey.RESOLUTION_TIMESTAMP));
		
		final Report report = ((MappableReport) element2).getReport();
		
		final Interval interval = new Interval(element1Timestamp.plus(getInterval().getStartMillis()),
		                                       element1Timestamp.plus(getInterval().getEndMillis()));
		
		if (element2CreationTimestamp.isBefore(element1Timestamp) && (element2ResolutionTimestamp != null)) {
			final History history = report.getHistory().get(Resolution.class.getSimpleName().toLowerCase());
			for (final HistoryElement element : history.getElements()) {
				final EnumTuple tuple = element.getChangedEnumValues().get(Resolution.class.getSimpleName()
				                                                                           .toLowerCase());
				@SuppressWarnings ("unchecked")
				final Enum<Resolution> val = (Enum<Resolution>) tuple.getNewValue();
				if (val.equals(Resolution.RESOLVED)) {
					if (interval.contains(element.getTimestamp())) {
						value = 1;
						
					} else if (element2ResolutionTimestamp.isAfter(element1Timestamp)) {
						value = Math.max(value,
						                 1.0d / (1.0d + ((element2ResolutionTimestamp.getMillis() - element1Timestamp.getMillis()) / 1000d / 3600d / 24d)));
					}
				}
			}
			
		}
		
		addFeature(score, value, FieldKey.CREATION_TIMESTAMP.name(), element1Timestamp.toString(),
		           element1Timestamp.toString(), FieldKey.RESOLUTION_TIMESTAMP.name(),
		           element2ResolutionTimestamp.toString(), element2ResolutionTimestamp.toString());
	}
	
	/**
	 * @param interval
	 *            the interval to set
	 */
	private final void setInterval(final Interval interval) {
		// PRECONDITIONS
		Condition.notNull(interval, "Argument '%s' in '%s'.", "interval", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.interval = interval;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.interval, interval,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	/**
	 * @param intervalArgument
	 *            the intervalArgument to set
	 */
	private final void setIntervalArgument(final TupleArgument intervalArgument) {
		// PRECONDITIONS
		Condition.notNull(intervalArgument, "Argument '%s' in '%s'.", "intervalArgument", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.intervalArgument = intervalArgument;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.intervalArgument, intervalArgument,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * @param intervalOption
	 *            the intervalOption to set
	 */
	private final void setIntervalOption(final TupleArgument.Options intervalOption) {
		// PRECONDITIONS
		Condition.notNull(intervalOption, "Argument '%s' in '%s'.", "intervalOption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			this.intervalOption = intervalOption;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.intervalOption, intervalOption,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.CREATION_TIMESTAMP),
		               new And(new Atom(Index.TO, Report.class), new Atom(Index.TO, FieldKey.RESOLUTION_TIMESTAMP)));
	}
	
}

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
import net.ownhero.dev.kanuni.conditions.ClassCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.History;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.model.Relation;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.persistence.model.EnumTuple;

/**
 * The Class TimestampEngine.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class TimestampEngine extends MappingEngine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<TimestampEngine, ArgumentSet<TimestampEngine, Options>> {
		
		private net.ownhero.dev.hiari.settings.TupleArgument.Options intervalOption;
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TimestampEngine.class.getSimpleName(), "...", requirements);
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
				
				return new TimestampEngine(new Interval(start * 1000, end * 1000));
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
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				final Map<String, IOptions<?, ?>> map = new HashMap<>();
				this.intervalOption = new TupleArgument.Options(
				                                                argumentSet,
				                                                "interval", //$NON-NLS-1$
				                                                Messages.getString("TimestampEngine.intervalDescription"), //$NON-NLS-1$
				                                                getDefaultinterval(), Requirement.required);
				map.put(this.intervalOption.getName(), this.intervalOption);
				return map;
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant description. */
	private static final String                description     = Messages.getString("TimestampEngine.description"); //$NON-NLS-1$
	                                                                                                                
	/** The Constant defaultInterval. */
	private static final Tuple<String, String> defaultInterval = new Tuple<String, String>("-0d 2h 0m 0s", //$NON-NLS-1$
	                                                                                       "+1d 0h 0m 0s");        //$NON-NLS-1$
	                                                                                                                
	/**
	 * Gets the defaultinterval.
	 * 
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
	
	/** The interval. */
	private Interval interval;
	
	/**
	 * @param interval
	 */
	public TimestampEngine(final Interval interval) {
		// PRECONDITIONS
		
		try {
			this.interval = interval;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the interval.
	 * 
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
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final Relation score) {
		double value = 0d;
		
		final DateTime element1Timestamp = ((DateTime) element1.get(FieldKey.CREATION_TIMESTAMP));
		final DateTime element2CreationTimestamp = ((DateTime) element2.get(FieldKey.CREATION_TIMESTAMP));
		final DateTime element2ResolutionTimestamp = ((DateTime) element2.get(FieldKey.RESOLUTION_TIMESTAMP));
		
		if (Logger.logDebug()) {
			Logger.debug("Creation FROM:%s, Creation TO:%s, Resolution TO:%s", element1Timestamp,
			             element2CreationTimestamp, element2ResolutionTimestamp);
		}
		
		if ((element1Timestamp != null) && (element2CreationTimestamp != null) && (element2ResolutionTimestamp != null)) {
			
			ClassCondition.instance(element2, MappableReport.class, "Required due to 'supported()' expression.");
			final Report report = ((MappableReport) element2).getReport();
			Condition.notNull(report, "Local variable '%s' in '%s:%s'.", "report", getHandle(), "score"); //$NON-NLS-1$ //$NON-NLS-2$
			
			final Interval localInterval = new Interval(element1Timestamp.plus(getInterval().getStartMillis()),
			                                            element1Timestamp.plus(getInterval().getEndMillis()));
			
			if (element2CreationTimestamp.isBefore(element1Timestamp) && (element2ResolutionTimestamp != null)) {
				// report got created before transaction
				if (Logger.logDebug()) {
					Logger.debug("report got created before transaction");
				}
				//final History history = report.getHistory().get(Resolution.class.getSimpleName().toLowerCase());
				
				/*if (!history.isEmpty()) {
					for (final HistoryElement element : history.getElements()) {
						if (Logger.logDebug()) {
							Logger.debug("Checking history element: %s", element);
						}
						final EnumTuple tuple = element.getChangedEnumValues().get(Resolution.class.getSimpleName()
						                                                                           .toLowerCase());
						@SuppressWarnings ("unchecked")
						final Enum<Resolution> val = (Enum<Resolution>) tuple.getNewValue();
						if (val != null && val.equals(Resolution.RESOLVED)) {
							if (Logger.logDebug()) {
								Logger.debug("This element set solved flag.");
							}
							if (localInterval.contains(element.getTimestamp())) {
								value = 1;
								if (Logger.logDebug()) {
									Logger.debug("Resolution is within specified interval, value: %s", value);
								}
							} else if (element.getTimestamp().isAfter(element1Timestamp)) {
								value = Math.max(value,
								                 1.0d / (1.0d + ((element.getTimestamp().getMillis() - element1Timestamp.getMillis()) / 1000d / 3600d / 24d)));
								if (Logger.logDebug()) {
									Logger.debug("Resolution is later than specified, value: %s", value);
								}
							}
						}
					}
				} else {*/
					if (localInterval.contains(element2ResolutionTimestamp)) {
						value = 1;
						if (Logger.logDebug()) {
							Logger.debug("Resolution is within specified interval, value: %s", value);
						}
					} else if (element2ResolutionTimestamp.isAfter(element1Timestamp)) {
						value = Math.max(value,
						                 1.0d / (1.0d + ((element2ResolutionTimestamp.getMillis() - element1Timestamp.getMillis()) / 1000d / 3600d / 24d)));
						if (Logger.logDebug()) {
							Logger.debug("Resolution is later than specified, value: %s", value);
						}
					}
				//}
				
			} else {
				if (Logger.logDebug()) {
					Logger.debug("Report got created after transaction");
				}
				value = -1;
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Scoring with confidence: %s", value);
			}
			addFeature(score, value, FieldKey.CREATION_TIMESTAMP.name(), element1Timestamp.toString(),
			           element1Timestamp.toString(), FieldKey.RESOLUTION_TIMESTAMP.name(),
			           element2ResolutionTimestamp.toString(), element2ResolutionTimestamp.toString());
		} else {
			if (Logger.logDebug()) {
				Logger.debug("Scoring with confidence: %s", value);
			}
			addFeature(score, value, FieldKey.CREATION_TIMESTAMP.name(), MappingEngine.getUnknown(),
			           MappingEngine.getUnknown(), FieldKey.RESOLUTION_TIMESTAMP.name(), MappingEngine.getUnknown(),
			           MappingEngine.getUnknown());
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

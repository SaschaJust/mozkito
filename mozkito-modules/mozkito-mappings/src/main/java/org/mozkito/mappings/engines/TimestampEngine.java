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

package org.mozkito.mappings.engines;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.ClassCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.model.History;
import org.mozkito.issues.model.HistoryElement;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.persistence.model.EnumTuple;
import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class TimestampEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TimestampEngine extends Engine {
	
	/** The Constant MS_IN_SECONDS. */
	public static final int                   MS_IN_SECONDS      = 1000;
	
	/** The Constant SECONDS_IN_MINUTES. */
	public static final int                   SECONDS_IN_MINUTES = 60;
	
	/** The Constant SECONDS_IN_HOURS. */
	public static final int                   SECONDS_IN_HOURS   = 60 * TimestampEngine.SECONDS_IN_MINUTES;
	
	/** The Constant SECONDS_IN_DAYS. */
	public static final int                   SECONDS_IN_DAYS    = 24 * TimestampEngine.SECONDS_IN_HOURS;
	
	/** The Constant defaultInterval. */
	public static final Tuple<String, String> DEFAULT_INTERVAL   = new Tuple<String, String>("-0d 2h 0m 0s", //$NON-NLS-1$
	                                                                                         "+1d 0h 0m 0s");        //$NON-NLS-1$
	                                                                                                                  
	/** The Constant description. */
	public static final String                DESCRIPTION        = Messages.getString("TimestampEngine.description"); //$NON-NLS-1$
	                                                                                                                  
	/** The Constant TAG. */
	public static final String                TAG                = "timestamp";                                      //$NON-NLS-1$
	                                                                                                                  
	/** The interval. */
	private Interval                          interval;
	
	/**
	 * Instantiates a new timestamp engine.
	 * 
	 * @param interval
	 *            the interval
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
	 * @see org.mozkito.mapping.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		return TimestampEngine.DESCRIPTION;
	}
	
	/**
	 * Gets the interval.
	 * 
	 * @return the interval
	 */
	private Interval getInterval() {
		// PRECONDITIONS
		
		try {
			return this.interval;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.interval, "Field '%s' in '%s'.", "interval", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final @NotNull Relation relation) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final MappableEntity from = relation.getFrom();
			final MappableEntity to = relation.getTo();
			
			SANITY: {
				assert from != null;
				assert to != null;
			}
			
			double value = 0d;
			
			final DateTime element1Timestamp = ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP));
			final DateTime element2CreationTimestamp = ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP));
			final DateTime element2ResolutionTimestamp = ((DateTime) to.get(FieldKey.RESOLUTION_TIMESTAMP));
			
			if (Logger.logDebug()) {
				Logger.debug("Creation FROM:%s, Creation TO:%s, Resolution TO:%s", element1Timestamp, //$NON-NLS-1$
				             element2CreationTimestamp, element2ResolutionTimestamp);
			}
			
			if ((element1Timestamp != null) && (element2CreationTimestamp != null)
			        && (element2ResolutionTimestamp != null)) {
				
				ClassCondition.instance(to, MappableReport.class, "Required due to 'supported()' expression."); //$NON-NLS-1$
				final Report report = ((MappableReport) to).getReport();
				Condition.notNull(report, "Local variable '%s' in '%s:%s'.", "report", getClassName(), "score"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
				final Interval localInterval = new Interval(element1Timestamp.plus(getInterval().getStartMillis()),
				                                            element1Timestamp.plus(getInterval().getEndMillis()));
				
				if (element2CreationTimestamp.isBefore(element1Timestamp) && (element2ResolutionTimestamp != null)) {
					// report got created before transaction
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("TimestampEngine.createdBeforeTransaction")); //$NON-NLS-1$
					}
					final History history = report.getHistory().get(Resolution.class.getSimpleName().toLowerCase());
					
					if (!history.isEmpty()) {
						for (final HistoryElement element : history.getElements()) {
							if (Logger.logDebug()) {
								Logger.debug(Messages.getString("TimestampEngine.checkingHistoryElement"), element); //$NON-NLS-1$
							}
							final EnumTuple tuple = element.getChangedEnumValues().get(Resolution.class.getSimpleName()
							                                                                           .toLowerCase());
							@SuppressWarnings ("unchecked")
							final Enum<Resolution> val = (Enum<Resolution>) tuple.getNewValue();
							if ((val != null) && val.equals(Resolution.RESOLVED)) {
								if (Logger.logDebug()) {
									Logger.debug(Messages.getString("TimestampEngine.elementResolved")); //$NON-NLS-1$
								}
								if (localInterval.contains(element.getTimestamp())) {
									value = 1;
									if (Logger.logDebug()) {
										Logger.debug(Messages.getString("TimestampEngine.resolutionWithinInterval"), value); //$NON-NLS-1$
									}
								} else if (element.getTimestamp().isAfter(element1Timestamp)) {
									value = Math.max(value,
									                 1.0d / (1.0d + ((element.getTimestamp().getMillis() - element1Timestamp.getMillis())
									                         / TimestampEngine.MS_IN_SECONDS / TimestampEngine.SECONDS_IN_DAYS)));
									if (Logger.logDebug()) {
										Logger.debug(Messages.getString("TimestampEngine.resolutionBehindWindow"), value); //$NON-NLS-1$
									}
								}
							}
						}
					} else {
						
						if (localInterval.contains(element2ResolutionTimestamp)) {
							value = 1;
							if (Logger.logDebug()) {
								Logger.debug("Resolution is within specified interval, value: %s", value); //$NON-NLS-1$
							}
						} else if (element2ResolutionTimestamp.isAfter(element1Timestamp)) {
							value = Math.max(value,
							                 1.0d / (1.0d + ((element2ResolutionTimestamp.getMillis() - element1Timestamp.getMillis())
							                         / TimestampEngine.MS_IN_SECONDS / TimestampEngine.SECONDS_IN_DAYS)));
							if (Logger.logDebug()) {
								Logger.debug("Resolution is later than specified, value: %s", value); //$NON-NLS-1$
							}
						}
					}
					
				} else {
					if (Logger.logDebug()) {
						Logger.debug(Messages.getString("TimestampEngine.reportAfterTransaction")); //$NON-NLS-1$
					}
					value = -1;
				}
				
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("TimestampEngine.scoring"), value); //$NON-NLS-1$
				}
				addFeature(relation, value, FieldKey.CREATION_TIMESTAMP.name(), element1Timestamp.toString(),
				           element1Timestamp.toString(), FieldKey.RESOLUTION_TIMESTAMP.name(),
				           element2ResolutionTimestamp.toString(), element2ResolutionTimestamp.toString());
			} else {
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("TimestampEngine.scoring", value)); //$NON-NLS-1$
				}
				addFeature(relation, value, FieldKey.CREATION_TIMESTAMP.name(), Engine.getUnknown(),
				           Engine.getUnknown(), FieldKey.RESOLUTION_TIMESTAMP.name(), Engine.getUnknown(),
				           Engine.getUnknown());
			}
		} finally {
			POSTCONDITIONS: {
				assert CollectionUtils.exists(relation.getFeatures(), new Predicate() {
					
					/**
					 * {@inheritDoc}
					 * 
					 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
					 */
					@Override
					public boolean evaluate(final Object object) {
						return ((Feature) object).getEngine().equals(TimestampEngine.class);
					}
				});
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.CREATION_TIMESTAMP),
		               new And(new Atom(Index.TO, Report.class), new Atom(Index.TO, FieldKey.RESOLUTION_TIMESTAMP)));
	}
	
}

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

import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.register.Node;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.persistence.FieldKey;
import org.mozkito.utilities.text.TextUtils;

/**
 * 
 * Engines analyze two candidates to match certain criteria and score neutral (0) if they don't match or
 * positive/negative according to the criterion under suspect.
 * 
 * Generating feature vectors for the candidates is a task that is accomplished by the scoring node. In the scoring
 * step, mozkito uses all enabled engines to compute a vector consisting of confidence values. Every engine may have its
 * own configuration options that are required to execute mozkito as soon as the engine is enabled. If the engine
 * depends on certain storages, further configuration dependencies might be pulled in. An engine takes a candidate pair
 * an checks for certain criteria and scores accordingly. Engines can score in three ways: positive, if they consider a
 * pair a valid mapping, negative if they consider the pair to be a false positive or 0 if they can't decide on any of
 * that. A criterion of an engine should be as atomic as possible, that means an engine shouldn't check for multiple
 * criteria at a time. After all engines have been execute on one candidate pair, the resulting feature vector is stored
 * to the database (including the information what has been scored by each engine and what data was considered while
 * computing the score).
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public abstract class Engine extends Node {
	
	/** The Constant defaultNegative. */
	public static final String  DEFAULT_NEGATIVE = "-1";                                       //$NON-NLS-1$
	                                                                                            
	/** The Constant defaultPositive. */
	public static final String  DEFAULT_POSITIVE = "1";                                        //$NON-NLS-1$
	                                                                                            
	/** The Constant unknown. */
	private static final String UNKNOWN          = Messages.getString("MappingEngine.unknown"); //$NON-NLS-1$
	                                                                                            
	/** The Constant unused. */
	private static final String UNUSED           = Messages.getString("MappingEngine.unused"); //$NON-NLS-1$
	                                                                                            
	/**
	 * Gets the default negative.
	 * 
	 * @return the defaultNegative
	 */
	public static String getDefaultNegative() {
		return Engine.DEFAULT_NEGATIVE;
	}
	
	/**
	 * Gets the default positive.
	 * 
	 * @return the defaultPositive
	 */
	public static String getDefaultPositive() {
		return Engine.DEFAULT_POSITIVE;
	}
	
	/**
	 * Gets the unknown.
	 * 
	 * @return the unknown
	 */
	public static String getUnknown() {
		return Engine.UNKNOWN;
	}
	
	/**
	 * Gets the unused.
	 * 
	 * @return the unused
	 */
	public static String getUnused() {
		return Engine.UNUSED;
	}
	
	/**
	 * Using this method, one can add features to a given {@link Relation}. The given score will be manipulated using
	 * the values given. The values are automatically <code>null</code> checked and truncated if needed.
	 * 
	 * @param score
	 *            the {@link Relation} a new feature shall be added
	 * @param confidence
	 *            a confidence value representing the impact of the feature
	 * @param fromFieldName
	 *            the name of the field (see {@link FieldKey}) of the "from" entity that caused this feature
	 * @param fromFieldContent
	 *            the content of the field (see {@link FieldKey}) of the "from" entity that caused this feature
	 * @param fromSubstring
	 *            the particular substring of the field (see {@link FieldKey}) of the "from" entity that caused this
	 *            feature
	 * @param toFieldName
	 *            the name of the field (see {@link FieldKey}) of the "to" entity that caused this feature
	 * @param toFieldContent
	 *            the content of the field (see {@link FieldKey}) of the "to" entity that caused this feature
	 * @param toSubstring
	 *            the particular substring of the field (see {@link FieldKey}) of the "to" entity that caused this
	 *            feature
	 */
	public final void addFeature(@NotNull final Relation score,
	                             final double confidence,
	                             @NotNull @NotEmpty final String fromFieldName,
	                             final Object fromFieldContent,
	                             final Object fromSubstring,
	                             @NotNull @NotEmpty final String toFieldName,
	                             final Object toFieldContent,
	                             final Object toSubstring) {
		score.addFeature(confidence,
		                 TextUtils.truncate(fromFieldName != null
		                                                         ? fromFieldName
		                                                         : Engine.UNUSED),
		                 TextUtils.truncate(fromFieldContent != null
		                                                            ? fromFieldContent.toString()
		                                                            : Engine.UNKNOWN),
		                 TextUtils.truncate(fromSubstring != null
		                                                         ? fromSubstring.toString()
		                                                         : TextUtils.truncate(fromFieldContent != null
		                                                                                                      ? fromFieldContent.toString()
		                                                                                                      : Engine.UNKNOWN)),
		                 TextUtils.truncate(toFieldName != null
		                                                       ? toFieldName
		                                                       : Engine.UNUSED),
		                 TextUtils.truncate(toFieldContent != null
		                                                          ? toFieldContent.toString()
		                                                          : Engine.UNKNOWN),
		                 TextUtils.truncate(toSubstring != null
		                                                       ? toSubstring.toString()
		                                                       : TextUtils.truncate(toFieldContent != null
		                                                                                                  ? toFieldContent.toString()
		                                                                                                  : Engine.UNKNOWN)),
		                 getClass());
	}
	
	/**
	 * Score.
	 * 
	 * @param relation
	 *            the relation
	 */
	public abstract void score(final @NotNull Relation relation);
	
	/**
	 * Supported.
	 * 
	 * @return an instance of {@link Expression} that represents the support of this engine
	 */
	public abstract Expression supported();
	
}

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
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.register.Node;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;

/**
 * 
 * Engines analyze two candidates to match certain criteria and score neutral
 * (0) if they don't match or positive/negative according to the criterion under
 * suspect.
 * 
 * Generating feature vectors for the candidates is a task that is accomplished
 * by the scoring node. In the scoring step, reposuite uses all enabled engines
 * to compute a vector consisting of confidence values. Every engine may have
 * its own configuration options that are required to execute reposuite as soon
 * as the engine is enabled. If the engine depends on certain storages, further
 * configuration dependencies might be pulled in. An engine takes a candidate
 * pair an checks for certain criteria and scores accordingly. Engines can score
 * in three ways: positive, if they consider a pair a valid mapping, negative if
 * they consider the pair to be a false positive or 0 if they can't decide on
 * any of that. A criterion of an engine should be as atomic as possible, that
 * means an engine shouldn't check for multiple criteria at a time. After all
 * engines have been execute on one candidate pair, the resulting feature vector
 * is stored to the database (incl. the information what has been scored by each
 * engine and what data was considered while computing the score).
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingEngine extends Node {
	
	public static final String defaultNegative = "-1";
	public static final String defaultPositive = "1";
	public static final String unknown         = "(unknown)";
	public static final String unused          = "(unused)";
	
	private final boolean      initialized     = false;
	private final boolean      registered      = false;
	
	/**
	 * Using this method, one can add features to a given {@link Mapping}. The
	 * given score will be manipulated using the values given. The values are
	 * automatically <code>null</code> checked and truncated if needed.
	 * 
	 * @param score
	 *            the {@link Mapping} a new feature shall be added
	 * @param confidence
	 *            a confidence value representing the impact of the feature
	 * @param fromFieldName
	 *            the name of the field (see {@link FieldKey}) of the "from"
	 *            entity that caused this feature
	 * @param fromFieldContent
	 *            the content of the field (see {@link FieldKey}) of the "from"
	 *            entity that caused this feature
	 * @param fromSubstring
	 *            the particular substring of the field (see {@link FieldKey})
	 *            of the "from" entity that caused this feature
	 * @param toFieldName
	 *            the name of the field (see {@link FieldKey}) of the "to"
	 *            entity that caused this feature
	 * @param toFieldContent
	 *            the content of the field (see {@link FieldKey}) of the "to"
	 *            entity that caused this feature
	 * @param toSubstring
	 *            the particular substring of the field (see {@link FieldKey})
	 *            of the "to" entity that caused this feature
	 */
	public final void addFeature(@NotNull final Mapping score,
	                             final double confidence,
	                             @NotNull @NotEmpty final String fromFieldName,
	                             final Object fromFieldContent,
	                             final Object fromSubstring,
	                             @NotNull @NotEmpty final String toFieldName,
	                             final Object toFieldContent,
	                             final Object toSubstring) {
		score.addFeature(confidence, truncate(fromFieldName != null
		                                                           ? fromFieldName
		                                                           : unused),
		                 truncate(fromFieldContent != null
		                                                  ? fromFieldContent.toString()
		                                                  : unknown),
		                 truncate(fromSubstring != null
		                                               ? fromSubstring.toString()
		                                               : truncate(fromFieldContent != null
		                                                                                  ? fromFieldContent.toString()
		                                                                                  : unknown)),
		                 truncate(toFieldName != null
		                                             ? toFieldName
		                                             : unused),
		                 truncate(toFieldContent != null
		                                                ? toFieldContent.toString()
		                                                : unknown),
		                 truncate(toSubstring != null
		                                             ? toSubstring.toString()
		                                             : truncate(toFieldContent != null
		                                                                              ? toFieldContent.toString()
		                                                                              : unknown)), getClass());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.register.Registered#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isEnabled("mapping.engines", this.getClass().getSimpleName());
	}
	
	/**
	 * @param from
	 *            the 'from' entity
	 * @param to
	 *            the 'to' entity
	 * @param score
	 *            the actual {@link Mapping} that will be manipulated by this
	 *            method
	 */
	@NoneNull
	public abstract void score(final MappableEntity from,
	                           final MappableEntity to,
	                           final Mapping score);
	
	/**
	 * @return an instance of {@link Expression} that represents the support of
	 *         this engine
	 */
	public abstract Expression supported();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MappingEngine [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
}

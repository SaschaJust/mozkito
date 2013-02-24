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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;

import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;

import serp.util.Strings;

/**
 * The Class RegexEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class RegexEngine extends Engine {
	
	/**
	 * The Class Matcher.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	private class Matcher {
		
		/** The comment. */
		private String comment;
		
		/** The regex. */
		private Regex  regex;
		
		/** The score. */
		private double score;
		
		/**
		 * Instantiates a new matcher.
		 * 
		 * @param score
		 *            the score
		 * @param pattern
		 *            the pattern
		 * @param options
		 *            the options
		 * @param comment
		 *            the comment
		 */
		public Matcher(final String score, final String pattern, final String options, final String comment) {
			setScore(Double.parseDouble(score));
			if (!options.isEmpty()) {
				if ("CASE_INSENSITIVE".equalsIgnoreCase(options)) { //$NON-NLS-1$
					setRegex(new Regex(pattern, Pattern.CASE_INSENSITIVE));
				} else {
					throw new UnrecoverableError("Unsupported regular expression option: " + options); //$NON-NLS-1$
				}
			} else {
				setRegex(new Regex(pattern));
			}
			setComment(comment);
		}
		
		/**
		 * Gets the comment.
		 * 
		 * @return the comment
		 */
		@SuppressWarnings ("unused")
		public final String getComment() {
			// PRECONDITIONS
			
			try {
				return this.comment;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.comment, "Field '%s' in '%s'.", "comment", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/**
		 * Gets the regex.
		 * 
		 * @param id
		 *            the id
		 * @return the regex
		 */
		public Regex getRegex(final String id) {
			return new Regex(this.regex.getPattern().replaceAll("##ID##", id)); //$NON-NLS-1$ 
		}
		
		/**
		 * Gets the score.
		 * 
		 * @return the score
		 */
		public double getScore() {
			return this.score;
		}
		
		/**
		 * Sets the comment.
		 * 
		 * @param comment
		 *            the comment to set
		 */
		public final void setComment(final String comment) {
			// PRECONDITIONS
			Condition.notNull(comment, "Argument '%s' in '%s'.", "comment", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			
			try {
				this.comment = comment;
			} finally {
				// POSTCONDITIONS
				CompareCondition.equals(this.comment, comment,
				                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
			}
		}
		
		/**
		 * Sets the regex.
		 * 
		 * @param regex
		 *            the regex to set
		 */
		public void setRegex(final Regex regex) {
			this.regex = regex;
		}
		
		/**
		 * Sets the score.
		 * 
		 * @param score
		 *            the score to set
		 */
		public void setScore(final double score) {
			this.score = score;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("Matcher [score="); //$NON-NLS-1$
			builder.append(this.score);
			builder.append(", regex="); //$NON-NLS-1$
			builder.append(this.regex);
			builder.append("]"); //$NON-NLS-1$
			return builder.toString();
		}
		
	}
	
	/** The config uri. */
	private URI                 configURI;
	
	/*
	 * Score, Pattern, Options e.g. 0.3 "({match}JAXEN-##ID##)" Pattern.CASE_INSENSITIVE 1.0
	 * "fixing bug #({match}##ID##)" Pattern.CASE_INSENSITIVE
	 */
	/** The matchers. */
	private Collection<Matcher> matchers;
	
	/** The unpad. */
	public String               unpad;
	
	/**
	 * Instantiates a new regex engine.
	 * 
	 * @param configURI
	 *            the config uri
	 */
	public RegexEngine(@NotNull final URI configURI) {
		// PRECONDITIONS
		
		try {
			this.configURI = configURI;
			final int maxFields = 3;
			
			setMatchers(new LinkedList<RegexEngine.Matcher>());
			
			CSVReader reader = null;
			try {
				reader = new CSVReader(new BufferedReader(new InputStreamReader(getConfigURI().toURL().openStream())),
				                       ' ');
				String[] line = null;
				while ((line = reader.readNext()) != null) {
					if (line.length < 2) {
						try {
							reader.close();
						} catch (final IOException e) {
							// ignore
						}
						throw new UnrecoverableError(Messages.getString("invalidRegexConfig", Strings.join(line, " "))); //$NON-NLS-1$//$NON-NLS-2$
						
					} else {
						getMatchers().add(new Matcher(
						                              line[0],
						                              line[1],
						                              line.length > 2
						                                             ? line[2]
						                                             : "", line.length > maxFields ? Strings.join(ArrayUtils.subarray(line, maxFields, line.length), " ").replaceAll("^\\s*//\\s*", "") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}
				}
				
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("RegexEngine.loadedPatterns", JavaUtils.collectionToString(getMatchers()))); //$NON-NLS-1$
				}
				
				reader.close();
			} catch (final IOException e) {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (final IOException e2) {
					// ignore
				}
				throw new UnrecoverableError(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the config.
	 * 
	 * @return the config
	 */
	private URI getConfigURI() {
		// PRECONDITIONS
		
		try {
			return this.configURI;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.configURI, "Field '%s' in '%s'.", "config", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#getDescription ()
	 */
	@Override
	public final String getDescription() {
		return Messages.getString("RegexEngine.description") + ": " + (getConfigURI() != null ? getConfigURI().toString() : "not yet configured"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Gets the matchers.
	 * 
	 * @return the matchers
	 */
	private Collection<Matcher> getMatchers() {
		return this.matchers;
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
			String relevantString = ""; //$NON-NLS-1$
			
			if (Logger.logDebug()) {
				Logger.debug(this.getClass().getSimpleName() + " checking " + from); //$NON-NLS-1$
			}
			
			for (final Matcher matcher : this.matchers) {
				final String id = to.getId();
				
				if (this.unpad != null) {
					if (Logger.logDebug()) {
						Logger.debug("Unpadding '%s' using [%s].", id, this.unpad); //$NON-NLS-1$
					}
					id.replaceAll("^[" + this.unpad + "]+", "[" + this.unpad + "]*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
				
				final Regex regex = matcher.getRegex(id);
				
				if (value < matcher.getScore()) {
					if (Logger.logDebug()) {
						Logger.debug("Using regex '" + regex.getPattern() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					if ((regex.find(from.get(FieldKey.BODY).toString()) != null) && (matcher.getScore() > value)) {
						value = matcher.getScore();
						relevantString = regex.getGroup("match"); //$NON-NLS-1$
						if (Logger.logDebug()) {
							Logger.debug("Found match: %s", relevantString); //$NON-NLS-1$
						}
					}
				}
			}
			
			addFeature(relation, value, FieldKey.BODY.name(), from.get(FieldKey.BODY).toString(), relevantString,
			           FieldKey.ID.name(), to.get(FieldKey.ID).toString(), to.get(FieldKey.ID).toString());
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
						return ((Feature) object).getEngine().equals(getClass());
					}
				});
			}
		}
	}
	
	/**
	 * Sets the matchers.
	 * 
	 * @param matchers
	 *            the matchers to set
	 */
	private void setMatchers(final Collection<Matcher> matchers) {
		this.matchers = matchers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public final Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.BODY), new Atom(Index.TO, FieldKey.ID));
	}
	
}

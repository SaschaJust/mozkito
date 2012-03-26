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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.URIArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Regex;
import au.com.bytecode.opencsv.CSVReader;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RegexEngine extends MappingEngine {
	
	/**
	 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
	 * 
	 */
	private class Matcher {
		
		private Regex  regex;
		private double score;
		
		/**
		 * @param score
		 * @param pattern
		 * @param options
		 */
		public Matcher(final String score, final String pattern, final String options) {
			setScore(Double.parseDouble(score));
			setRegex(new Regex(pattern, !options.isEmpty() && options.equalsIgnoreCase("CASE_INSENSITIVE") //$NON-NLS-1$
			        ? Pattern.CASE_INSENSITIVE
			        : 0));
		}
		
		/**
		 * @return the regex
		 */
		public Regex getRegex(final String id) {
			return new Regex(this.regex.getPattern().replace("##ID##", "" + id)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		/**
		 * @return the score
		 */
		public double getScore() {
			return this.score;
		}
		
		/**
		 * @param regex
		 *            the regex to set
		 */
		public void setRegex(final Regex regex) {
			this.regex = regex;
		}
		
		/**
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
	
	/*
	 * Score, Pattern, Options e.g. 0.3 "({match}JAXEN-##ID##)" Pattern.CASE_INSENSITIVE 1.0
	 * "fixing bug #({match}##ID##)" Pattern.CASE_INSENSITIVE
	 */
	private Collection<Matcher> matchers;
	
	private URIArgument.Options configOption;
	private URIArgument         configArgument;
	
	private URI                 config;
	
	/**
	 * @return the config
	 */
	private final URI getConfig() {
		// PRECONDITIONS
		
		try {
			return this.config;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.config, "Field '%s' in '%s'.", "config", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @return the configArgument
	 */
	private final URIArgument getConfigArgument() {
		// PRECONDITIONS
		
		try {
			return this.configArgument;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.configArgument, "Field '%s' in '%s'.", "configArgument", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @return the configOption
	 */
	private final URIArgument.Options getConfigOption() {
		// PRECONDITIONS
		
		try {
			return this.configOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.configOption, "Field '%s' in '%s'.", "configOption", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return Messages.getString("RegexEngine.description") + ": " + getConfig(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @return the matchers
	 */
	private Collection<Matcher> getMatchers() {
		return this.matchers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.config == null)
		                                                  ? 0
		                                                  : this.config.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#afterParse()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		Condition.notNull(this.configOption, "Field '%s' in '%s'.", "confidenceOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			setConfigArgument(getSettings().getArgument(getConfigOption()));
			setConfig(getConfigArgument().getValue());
			
			setMatchers(new LinkedList<RegexEngine.Matcher>());
			
			try {
				final CSVReader reader = new CSVReader(
				                                       new BufferedReader(
				                                                          new InputStreamReader(
				                                                                                getConfig().toURL()
				                                                                                           .openStream())),
				                                       ' ');
				String[] line = null;
				while ((line = reader.readNext()) != null) {
					getMatchers().add(new Matcher(line[0], line[1], line.length > 2
					                                                               ? line[2]
					                                                               : "")); //$NON-NLS-1$
				}
				
				if (Logger.logDebug()) {
					Logger.debug(Messages.getString("RegexEngine.loadedPatterns") + JavaUtils.collectionToString(getMatchers())); //$NON-NLS-1$
				}
			} catch (final IOException e) {
				throw new Shutdown(Messages.getString("RegexEngine.configReadError"), e); //$NON-NLS-1$
			}
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.configArgument, "Field '%s' in '%s'.", "configArgument", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.config, "Field '%s' in '%s'.", "config", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Provide.
	 * 
	 * @param anchorSet
	 *            the anchor set
	 * @return the argument set
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Override
	public ArgumentSet<?, ?> provide(@NotNull final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                                       ArgumentSetRegistrationException,
	                                                                       SettingsParseError {
		// PRECONDITIONS
		setSettings(root.getSettings());
		Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		final ArgumentSet<?, ?> anchor = super.getAnchor(getSettings());
		
		try {
			
			setConfigOption(new URIArgument.Options(anchor, "config", //$NON-NLS-1$
			                                        Messages.getString("RegexEngine.configDescription"), //$NON-NLS-1$
			                                        null, Requirement.contains(getOptions(getSettings()),
			                                                                   getClass().getSimpleName())));
			
			return anchor;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(getSettings(), "Field '%s' in '%s'.", "settings", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.notNull(this.configOption, "Field '%s' in '%s'.", "confidenceOption", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
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
		String relevantString = ""; //$NON-NLS-1$
		
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getSimpleName() + " checking " + element1); //$NON-NLS-1$
		}
		
		for (final Matcher matcher : this.matchers) {
			final Regex regex = matcher.getRegex(element2.get(FieldKey.ID).toString());
			
			if (value < matcher.getScore()) {
				
				if (Logger.logDebug()) {
					Logger.debug("Using regex '" + regex.getPattern() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if ((regex.find(element1.get(FieldKey.BODY).toString()) != null) && (matcher.getScore() > value)) {
					
					value += matcher.getScore();
					relevantString = regex.getGroup("match"); //$NON-NLS-1$
				}
			}
		}
		
		addFeature(score, value, FieldKey.BODY.name(), element1.get(FieldKey.BODY).toString(), relevantString,
		           FieldKey.ID.name(), element2.get(FieldKey.ID).toString(), element2.get(FieldKey.ID).toString());
	}
	
	/**
	 * @param config
	 *            the config to set
	 */
	private final void setConfig(@NotNull final URI config) {
		
		try {
			this.config = config;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.config, config,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * @param configArgument
	 *            the configArgument to set
	 */
	private final void setConfigArgument(@NotNull final URIArgument configArgument) {
		// PRECONDITIONS
		
		try {
			this.configArgument = configArgument;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.configArgument, configArgument,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * @param configOption
	 *            the configOption to set
	 */
	private final void setConfigOption(@NotNull final URIArgument.Options configOption) {
		// PRECONDITIONS
		
		try {
			this.configOption = configOption;
		} finally {
			// POSTCONDITIONS
			CompareCondition.equals(this.configOption, configOption,
			                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
		}
	}
	
	/**
	 * @param matchers
	 *            the matchers to set
	 */
	private void setMatchers(final Collection<Matcher> matchers) {
		this.matchers = matchers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.BODY), new Atom(Index.TO, FieldKey.ID));
	}
	
}

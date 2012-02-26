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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import net.ownhero.dev.hiari.settings.arguments.URIArgument;
import net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
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
			setRegex(new Regex(
			                   pattern,
			                   !options.isEmpty() && options.equalsIgnoreCase("CASE_INSENSITIVE")
			                                                                                     ? Pattern.CASE_INSENSITIVE
			                                                                                     : 0));
		}
		
		/**
		 * @return the regex
		 */
		public Regex getRegex(final String id) {
			return new Regex(this.regex.getPattern().replace("##ID##", "" + id));
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
			builder.append("Matcher [score=");
			builder.append(this.score);
			builder.append(", regex=");
			builder.append(this.regex);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
	private URI                 configPath;
	/*
	 * Score, Pattern, Options e.g. 0.3 "({match}JAXEN-##ID##)" Pattern.CASE_INSENSITIVE 1.0
	 * "fixing bug #({match}##ID##)" Pattern.CASE_INSENSITIVE
	 */
	private Collection<Matcher> matchers;
	private URIArgument         fileArgument;
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#afterParse()
	 */
	@Override
	public void afterParse() {
		setConfigPath(this.fileArgument.getValue());
		setMatchers(new LinkedList<RegexEngine.Matcher>());
		
		if (!getConfigPath().getScheme().equalsIgnoreCase("file")) {
			if (Logger.logError()) {
				Logger.error("Other locations then file are currently not supported for config files: "
				        + getConfigPath().toString());
			}
			throw new Shutdown("Other locations then file are currently not supported for config files: "
			        + getConfigPath().toString());
		}
		
		try {
			final CSVReader reader = new CSVReader(
			                                       new BufferedReader(
			                                                          new InputStreamReader(
			                                                                                getConfigPath().toURL()
			                                                                                               .openStream())),
			                                       ' ');
			String[] line = null;
			while ((line = reader.readNext()) != null) {
				getMatchers().add(new Matcher(line[0], line[1], line.length > 2
				                                                               ? line[2]
				                                                               : ""));
			}
			
			if (Logger.logDebug()) {
				Logger.debug("Loaded patterns: " + JavaUtils.collectionToString(getMatchers()));
			}
		} catch (final IOException e) {
			throw new Shutdown("Regex configuration read error.", e);
		}
	}
	
	/**
	 * @return the configPath
	 */
	private URI getConfigPath() {
		return this.configPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return "Scores if regular expressions from a given set match. See config at: " + getConfigPath();
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
		result = (prime * result) + ((this.configPath == null)
		                                                      ? 0
		                                                      : this.configPath.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws ArgumentRegistrationException {
		this.fileArgument = new URIArgument(set, "file",
		                                    "URI to file containing the regular expressions used to map the IDs.",
		                                    null, Requirement.required);
		return true;
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
		String relevantString = "";
		
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getSimpleName() + " checking " + element1);
		}
		
		for (final Matcher matcher : this.matchers) {
			final Regex regex = matcher.getRegex(element2.get(FieldKey.ID).toString());
			
			if (value < matcher.getScore()) {
				
				if (Logger.logDebug()) {
					Logger.debug("Using regex '" + regex.getPattern() + "'.");
				}
				if ((regex.find(element1.get(FieldKey.BODY).toString()) != null) && (matcher.getScore() > value)) {
					
					value += matcher.getScore();
					relevantString = regex.getGroup("match");
				}
			}
		}
		
		addFeature(score, value, FieldKey.BODY.name(), element1.get(FieldKey.BODY).toString(), relevantString,
		           FieldKey.ID.name(), element2.get(FieldKey.ID).toString(), element2.get(FieldKey.ID).toString());
	}
	
	/**
	 * @param uri
	 *            the configPath to set
	 */
	private void setConfigPath(final URI uri) {
		this.configPath = uri;
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
	
	/**
	 * @return
	 * @see java.net.URI#toASCIIString()
	 */
	public String toASCIIString() {
		return this.configPath.toASCIIString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#toString()
	 */
	@Override
	public String toString() {
		return this.configPath.toString();
	}
	
	/**
	 * @return
	 * @throws MalformedURLException
	 * @see java.net.URI#toURL()
	 */
	public URL toURL() throws MalformedURLException {
		return this.configPath.toURL();
	}
	
}

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
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
	 * Score, Pattern, Options e.g. 0.3 "({match}JAXEN-##ID##)"
	 * Pattern.CASE_INSENSITIVE 1.0 "fixing bug #({match}##ID##)"
	 * Pattern.CASE_INSENSITIVE
	 */
	private Collection<Matcher> matchers;
	
	/**
	 * @param arg0
	 * @return
	 * @see java.net.URI#compareTo(java.net.URI)
	 */
	public int compareTo(final URI arg0) {
		return this.configPath.compareTo(arg0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RegexEngine)) {
			return false;
		}
		final RegexEngine other = (RegexEngine) obj;
		if (this.configPath == null) {
			if (other.configPath != null) {
				return false;
			}
		} else if (!this.configPath.equals(other.configPath)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return
	 * @see java.net.URI#getAuthority()
	 */
	public String getAuthority() {
		return this.configPath.getAuthority();
	}
	
	/**
	 * @return the configPath
	 */
	private URI getConfigPath() {
		return this.configPath;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores if regular expressions from a given set match. See config at: " + getConfigPath();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getFragment()
	 */
	public String getFragment() {
		return this.configPath.getFragment();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getHost()
	 */
	public String getHost() {
		return this.configPath.getHost();
	}
	
	/**
	 * @return the matchers
	 */
	private Collection<Matcher> getMatchers() {
		return this.matchers;
	}
	
	/**
	 * @return
	 * @see java.net.URI#getPath()
	 */
	public String getPath() {
		return this.configPath.getPath();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getPort()
	 */
	public int getPort() {
		return this.configPath.getPort();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getQuery()
	 */
	public String getQuery() {
		return this.configPath.getQuery();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getRawAuthority()
	 */
	public String getRawAuthority() {
		return this.configPath.getRawAuthority();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getRawFragment()
	 */
	public String getRawFragment() {
		return this.configPath.getRawFragment();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getRawPath()
	 */
	public String getRawPath() {
		return this.configPath.getRawPath();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getRawQuery()
	 */
	public String getRawQuery() {
		return this.configPath.getRawQuery();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getRawSchemeSpecificPart()
	 */
	public String getRawSchemeSpecificPart() {
		return this.configPath.getRawSchemeSpecificPart();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getRawUserInfo()
	 */
	public String getRawUserInfo() {
		return this.configPath.getRawUserInfo();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getScheme()
	 */
	public String getScheme() {
		return this.configPath.getScheme();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getSchemeSpecificPart()
	 */
	public String getSchemeSpecificPart() {
		return this.configPath.getSchemeSpecificPart();
	}
	
	/**
	 * @return
	 * @see java.net.URI#getUserInfo()
	 */
	public String getUserInfo() {
		return this.configPath.getUserInfo();
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
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setConfigPath((URI) getOption("file").getSecond().getValue());
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
	 * @return
	 * @see java.net.URI#isAbsolute()
	 */
	public boolean isAbsolute() {
		return this.configPath.isAbsolute();
	}
	
	/**
	 * @return
	 * @see java.net.URI#isOpaque()
	 */
	public boolean isOpaque() {
		return this.configPath.isOpaque();
	}
	
	/**
	 * @return
	 * @see java.net.URI#normalize()
	 */
	public URI normalize() {
		return this.configPath.normalize();
	}
	
	/**
	 * @return
	 * @throws URISyntaxException
	 * @see java.net.URI#parseServerAuthority()
	 */
	public URI parseServerAuthority() throws URISyntaxException {
		return this.configPath.parseServerAuthority();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#init(de.
	 * unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments, boolean)
	 */
	@Override
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet<?> arguments) {
		super.register(settings, arguments);
		registerURIOption(settings, arguments, "file",
		                  "URI to file containing the regular expressions used to map the IDs.", null, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.model.Mapping)
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
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
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
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#toString()
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

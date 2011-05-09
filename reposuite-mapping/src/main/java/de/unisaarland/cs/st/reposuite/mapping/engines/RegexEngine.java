/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

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

import net.ownhero.dev.regex.Regex;
import au.com.bytecode.opencsv.CSVReader;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.URIArgument;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

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
		
		private double score;
		private Regex  regex;
		
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
		public Regex getRegex(final long id) {
			return new Regex(this.regex.getPattern().replace("##ID##", "" + id));
		}
		
		/**
		 * @return the score
		 */
		public double getScore() {
			return this.score;
		}
		
		/**
		 * @param regex the regex to set
		 */
		public void setRegex(final Regex regex) {
			this.regex = regex;
		}
		
		/**
		 * @param score the score to set
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
			StringBuilder builder = new StringBuilder();
			builder.append("Matcher [score=");
			builder.append(this.score);
			builder.append(", regex=");
			builder.append(this.regex);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
	/*
	 * Score, Pattern, Options e.g. 0.3 "({match}JAXEN-##ID##)"
	 * Pattern.CASE_INSENSITIVE 1.0 "fixing bug #({match}##ID##)"
	 * Pattern.CASE_INSENSITIVE
	 */
	private Collection<Matcher> matchers;
	private URI                 configPath;
	
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
		RegexEngine other = (RegexEngine) obj;
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
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
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
		result = prime * result + ((this.configPath == null)
		                                                    ? 0
		                                                    : this.configPath.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setConfigPath((URI) getSettings().getSetting("mapping.config.regexFile").getValue());
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
			CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(getConfigPath().toURL()
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
		} catch (IOException e) {
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
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init(de.
	 * unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired);
		arguments.addArgument(new URIArgument(settings, "mapping.config.regexFile",
		                                      "URI to file containing the regular expressions used to map the IDs.",
		                                      null, isRequired));
	}
	
	/**
	 * @param arg0
	 * @return
	 * @see java.net.URI#relativize(java.net.URI)
	 */
	public URI relativize(final URI arg0) {
		return this.configPath.relativize(arg0);
	}
	
	/**
	 * @param arg0
	 * @return
	 * @see java.net.URI#resolve(java.lang.String)
	 */
	public URI resolve(final String arg0) {
		return this.configPath.resolve(arg0);
	}
	
	/**
	 * @param arg0
	 * @return
	 * @see java.net.URI#resolve(java.net.URI)
	 */
	public URI resolve(final URI arg0) {
		return this.configPath.resolve(arg0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.rcs.model.RCSTransaction,
	 * de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		double value = 0d;
		String relevantString = "";
		
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getSimpleName() + " checking " + transaction);
		}
		
		for (Matcher matcher : this.matchers) {
			Regex regex = matcher.getRegex(report.getId());
			
			if (value < matcher.getScore()) {
				
				if (Logger.logDebug()) {
					Logger.debug("Using regex '" + regex.getPattern() + "'.");
				}
				if (regex.find(transaction.getMessage()) != null) {
					
					value += matcher.getScore();
					relevantString = regex.getGroup("match");
				}
			}
		}
		
		if (!relevantString.isEmpty()) {
			score.addFeature(value, "message", relevantString, "id", report.getId() + "", this.getClass());
		}
	}
	
	/**
	 * @param uri the configPath to set
	 */
	private void setConfigPath(final URI uri) {
		this.configPath = uri;
	}
	
	/**
	 * @param matchers the matchers to set
	 */
	private void setMatchers(final Collection<Matcher> matchers) {
		this.matchers = matchers;
	}
	
	/**
	 * @return
	 * @see java.net.URI#toASCIIString()
	 */
	public String toASCIIString() {
		return this.configPath.toASCIIString();
	}
	
	/**
	 * @return
	 * @see java.net.URI#toString()
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

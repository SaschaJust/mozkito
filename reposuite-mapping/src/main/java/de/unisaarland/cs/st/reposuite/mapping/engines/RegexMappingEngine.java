/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

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
import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class RegexMappingEngine extends MappingEngine {
	
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
	private static Collection<Matcher> matchers;
	private static URI                 configPath;
	
	/**
	 * @return the configPath
	 */
	private static URI getConfigPath() {
		return configPath;
	}
	
	/**
	 * @return the matchers
	 */
	private static Collection<Matcher> getMatchers() {
		return matchers;
	}
	
	/**
	 * @param uri the configPath to set
	 */
	private static void setConfigPath(final URI uri) {
		RegexMappingEngine.configPath = uri;
	}
	
	/**
	 * @param matchers the matchers to set
	 */
	private static void setMatchers(final Collection<Matcher> matchers) {
		RegexMappingEngine.matchers = matchers;
	}
	
	/**
	 * @param settings
	 */
	public RegexMappingEngine(final MappingSettings settings) {
		super(settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setConfigPath((URI) getSettings().getSetting("mapping.config.regexFile").getValue());
		setMatchers(new LinkedList<RegexMappingEngine.Matcher>());
		
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
		
		for (Matcher matcher : matchers) {
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
			score.addFeature(value, "message", relevantString, this.getClass());
		}
	}
	
}

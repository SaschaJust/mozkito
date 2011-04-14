/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.FilePermissionException;
import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
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
		public Regex getRegex() {
			return this.regex;
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
		
	}
	
	/*
	 * Score, Pattern, Options e.g. 0.3 "({match}JAXEN-##ID##)"
	 * Pattern.CASE_INSENSITIVE 1.0 "fixing bug #({match}##ID##)"
	 * Pattern.CASE_INSENSITIVE
	 */
	private static Collection<Matcher> matchers;
	private static String              configPath;
	
	/**
	 * @return the configPath
	 */
	private static String getConfigPath() {
		return configPath;
	}
	
	/**
	 * @return the matchers
	 */
	private static Collection<Matcher> getMatchers() {
		return matchers;
	}
	
	/**
	 * @param configPath the configPath to set
	 */
	private static void setConfigPath(final String configPath) {
		RegexMappingEngine.configPath = configPath;
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
	RegexMappingEngine(final MappingSettings settings) {
		super(settings);
		setConfigPath((String) getSettings().getSetting("mapping.config.regexFile").getValue());
		setMatchers(new LinkedList<RegexMappingEngine.Matcher>());
		
		File file = new File(getConfigPath());
		try {
			FileUtils.ensureFilePermissions(file, FileUtils.READABLE_FILE);
			CSVReader reader = new CSVReader(new FileReader(file), ' ');
			String[] line = null;
			while ((line = reader.readNext()) != null) {
				getMatchers().add(new Matcher(line[0], line[1], line.length > 2
				                                                               ? line[2]
				                                                               : ""));
			}
		} catch (FilePermissionException e) {
			throw new Shutdown("Regex configuration file has wrong permissions (" + FileUtils.permissionsToString(file)
			        + ").", e);
		} catch (FileNotFoundException e) {
			throw new Shutdown("Regex configuration file does not exist.", e);
		} catch (IOException e) {
			throw new Shutdown("Regex configuration read error.", e);
		}
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
			Regex regex = matcher.getRegex();
			
			if (value < matcher.getScore()) {
				if (regex.find(transaction.getMessage()) != null) {
					
					value += matcher.getScore();
					relevantString = regex.getGroup("match");
				}
			}
		}
		
		score.addFeature(value, "message", relevantString, this.getClass());
	}
	
}

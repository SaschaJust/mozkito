package de.unisaarland.cs.st.reposuite.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jregex.MatchIterator;
import jregex.MatchResult;
import jregex.Matcher;
import jregex.NamedPattern;
import jregex.RETokenizer;
import jregex.Replacer;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Regex {
	
	/**
	 * @param namedPattern
	 */
	public static void analyzePattern(final String pattern) {
		// check matching groups
		// check character groups
		// check named groups
		// check multiplier
		// check character classes
		// give examples
		
		checkRegex(pattern);
	}
	
	/**
	 * Checks the pattern for common mistakes.
	 * 
	 * @param pattern
	 * @return
	 */
	public static boolean checkRegex(final String pattern) {
		// avoid captured positive look ahead
		assert (pattern != null);
		assert (pattern.length() > 0);
		assert (checkRegex(pattern));
		
		if (RepoSuiteSettings.logDebug()) {
			Logger.debug("Checking pattern: " + pattern);
		}
		
		// remove all character classes []
		Regex characterGroups = new Regex("((?<!\\\\)\\[|^\\[)[^\\]]*\\]");
		String patternWithoutCharacterClasses = characterGroups.removeAll(pattern);
		
		if (RepoSuiteSettings.logDebug()) {
			Logger.debug("Pattern without chracter classes: " + patternWithoutCharacterClasses);
		}
		
		// check for closed matching groups
		Regex beginMatch = new Regex("(?<!\\\\)\\(|^\\(");
		Regex endMatch = new Regex("(?<!\\\\)\\)");
		
		int beginCount = beginMatch.findAll(patternWithoutCharacterClasses).size();
		int endCount = endMatch.findAll(patternWithoutCharacterClasses).size();
		
		if (beginCount != endCount) {
			return false;
		}
		
		// check for empty matching groups
		Regex emptyGroups = new Regex("\\((\\?<?[!=])?(\\{\\w+\\})?\\)");
		List<RegexGroup> find = emptyGroups.find(patternWithoutCharacterClasses);
		
		if (find != null) {
			return false;
		}
		
		// check for closed character groups
		
		return true;
	}
	
	private NamedPattern                 pattern;
	
	private List<RegexGroup>             matches;
	
	private final List<List<RegexGroup>> allMatches = new LinkedList<List<RegexGroup>>();
	
	/**
	 * @param namedPattern
	 */
	public Regex(final String pattern) {
		this(pattern, 0);
	}
	
	/**
	 * @param namedPattern
	 * @param flags
	 */
	public Regex(final String pattern, final int flags) {
		assert (pattern != null);
		assert (pattern.length() > 0);
		
		this.pattern = new NamedPattern(pattern, flags);
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
		if (!(obj instanceof Regex)) {
			return false;
		}
		Regex other = (Regex) obj;
		if (this.pattern == null) {
			if (other.pattern != null) {
				return false;
			}
		} else if (!this.pattern.equals(other.pattern)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Finds the first occurrence in the text.
	 * 
	 * @param text
	 *            the text to be analyzed
	 * @return a {@link List} of {@link RegexGroup} representing the matches.
	 */
	public List<RegexGroup> find(final String text) {
		assert (text != null);
		
		Matcher matcher = this.pattern.matcher(text);
		if (matcher.find()) {
			this.matches = new ArrayList<RegexGroup>(matcher.groupCount());
			for (int i = 1; i < matcher.groupCount(); ++i) {
				this.matches.add(new RegexGroup(this.pattern.toString(), text, matcher.group(i), i, this.pattern
				        .getGroupName(i)));
			}
		}
		
		return this.matches;
	}
	
	/**
	 * Finds all occurrences in the text
	 * 
	 * @param text
	 *            the text to be analyzed
	 * @see Regex#find(String)
	 * @return a {@link List} of {@link List}s of {@link RegexGroup}
	 */
	public List<List<RegexGroup>> findAll(final String text) {
		assert (text != null);
		
		Matcher matcher = this.pattern.matcher(text);
		
		MatchIterator findAll = matcher.findAll();
		
		while (findAll.hasMore()) {
			MatchResult match = findAll.nextMatch();
			List<RegexGroup> matches = new ArrayList<RegexGroup>(matcher.groupCount());
			
			for (int i = 1; i < match.groupCount(); ++i) {
				matches.add(new RegexGroup(this.pattern.toString(), text, match.group(i), i, this.pattern
				        .getGroupName(i)));
			}
			this.allMatches.add(matches);
		}
		return this.allMatches;
	}
	
	/**
	 * This uses non-breaking search to find all possible occurrences of the
	 * pattern, including those that are intersecting or nested. This is
	 * achieved by using the Matcher's method proceed() instead of find().
	 * 
	 * @param text
	 *            the text to be scanned
	 * @return a list of all possible matches
	 */
	public List<String> findAllPossibleMatches(final String text) {
		Matcher matcher = this.pattern.matcher(text);
		List<String> list = new LinkedList<String>();
		
		while (matcher.proceed()) {
			list.add(matcher.toString());
		}
		
		return list;
	}
	
	/**
	 * Reduces the pattern to find the longest substring of the pattern starting
	 * at the beginning that matches the text. If the specified pattern already
	 * matches the text, the pattern is returned.
	 * 
	 * @param text
	 *            to be checked against
	 * @return the {@link String} representation of the matching pattern
	 */
	public String findLongestMatchingPattern(final String text) {
		Regex regex = new Regex(this.pattern.toString());
		String pattern = regex.getPattern();
		
		while (!regex.matches(text) && (pattern.length() > 0)) {
			pattern = pattern.substring(0, pattern.length() - 1);
			regex.setPattern(pattern);
		}
		
		return pattern;
	}
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.pattern == null) ? 0 : this.pattern.hashCode());
		return result;
	}
	
	/**
	 * Checks if the specified pattern matches the text (completely).
	 * 
	 * @param text
	 *            the text to be matched
	 * @return if there is a full match
	 */
	public boolean matches(final String text) {
		assert (text != null);
		
		return this.pattern.matcher(text).matches();
	}
	
	/**
	 * This feature allows to find out whether the string could match by
	 * examining only its beginning part. For example, the string is being typed
	 * into a text field, and you want to reject the rest characters after the
	 * first few ones appear incorrect.
	 * 
	 * @param text
	 *            the text to be analyzed
	 * @return if the text is a prefix of a matching string
	 */
	public boolean prefixMatches(final String text) {
		assert (text != null);
		
		Matcher matcher = this.pattern.matcher();
		return matcher.matchesPrefix();
	}
	
	/**
	 * Removes all matches in the text
	 * 
	 * @param text
	 *            the base text
	 * @return the reduced string
	 */
	private String removeAll(final String text) {
		Replacer replacer = this.pattern.replacer("");
		return replacer.replace(text);
	}
	
	/**
	 * Replaces all occurrences of the pattern in the text with the given
	 * string.
	 * 
	 * @param text
	 * @param replacement
	 * @return
	 */
	public String replaceAll(final String text, final String replacement) {
		Replacer replacer = this.pattern.replacer(replacement);
		return replacer.replace(text);
	}
	
	/**
	 * Replaces all occurrences of the pattern in the text with the given
	 * pattern. Example:<br />
	 * <code>Pattern p=new Pattern("(\\d\\d):(\\d\\d):(\\d\\d)");
	 * Replacer r=p.replacer("[hour=$1, minute=$2, second=$3]");
	 * //see also the constructor Replacer(Pattern,String,boolean)
	 * String result=r.replace("the time is 10:30:01");
	 * //gives "the time is [hour=10, minute=30, second=01]"</code>
	 * 
	 * @param text
	 * @param replacement
	 * @return
	 */
	public String replaceAllWithPattern(final String text, final String replacement) {
		Replacer replacer = this.pattern.replacer(replacement);
		return replacer.replace(text);
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(final String pattern) {
		assert (pattern != null);
		assert (pattern.length() > 0);
		assert (checkRegex(pattern));
		
		this.pattern = new NamedPattern(pattern);
	}
	
	/**
	 * String tokenizing is pretty similar to using a standard StringTokenizer
	 * class. The only difference is that this one uses a pattern occurrence as
	 * a token delimiter. You can refine your search criteria by adding
	 * backward/forward scanning, e.g. when using a <code>"---"</code> delimiter
	 * you can use the pattern <code>"(?<!\")---(?!\")"</code>. This makes sure
	 * the hyphens are not enclosed by quote marks.
	 * 
	 * @param text
	 *            to be split using the pattern
	 * @return a string array containing all tokens
	 */
	public String[] tokenize(final String text) {
		return new RETokenizer(this.pattern, text).split();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Regex [pattern=" + this.pattern + "]";
	}
	
}

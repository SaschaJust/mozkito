package de.unisaarland.cs.st.reposuite.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jregex.MatchIterator;
import jregex.MatchResult;
import jregex.Matcher;
import jregex.NamedPattern;
import jregex.PatternSyntaxException;
import jregex.RETokenizer;
import jregex.Replacer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * This class provides regular expression support and as well interfaces as
 * extends JRegex.
 * 
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
		
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace("Checking pattern: " + pattern);
		}
		
		// remove all character classes []
		Regex characterGroups = new Regex("((?<!\\\\)\\[|^\\[)[^\\]]*\\][*+]?\\??");
		String patternWithoutCharacterClasses = characterGroups.removeAll(pattern);
		
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace("Pattern without character classes: " + patternWithoutCharacterClasses);
		}
		
		// check for closed matching groups
		Regex beginMatch = new Regex("(?<!\\\\)\\(|^\\(");
		Regex endMatch = new Regex("(?<!\\\\)\\)");
		
		List<List<RegexGroup>> allMatchingGroupsOpen = beginMatch.findAll(patternWithoutCharacterClasses);
		List<List<RegexGroup>> allMatchingGroupsClosed = endMatch.findAll(patternWithoutCharacterClasses);
		
		int beginCount = (allMatchingGroupsOpen != null ? allMatchingGroupsOpen.size() : 0);
		int endCount = (allMatchingGroupsClosed != null ? allMatchingGroupsClosed.size() : 0);
		
		if (beginCount != endCount) {
			if (beginCount > endCount) {
				
				if (RepoSuiteSettings.logWarn()) {
					Logger.warn("Too many opening '(' parenthesis.");
				}
			} else {
				if (RepoSuiteSettings.logWarn()) {
					Logger.warn("Too many closing ')' parenthesis.");
				}
			}
			return false;
		}
		
		// check for empty matching groups
		Regex emptyGroups = new Regex("(\\((\\?<?[!=])?(\\{\\w+\\})?\\))");
		List<List<RegexGroup>> emptyGroupsList = emptyGroups.findAll(pattern);
		
		if (emptyGroupsList != null) {
			
			if (RepoSuiteSettings.logWarn()) {
				Logger.warn("Empty matching groups: " + CollectionUtils.collect(emptyGroupsList, new Transformer() {
					
					@Override
					public Object transform(final Object input) {
						@SuppressWarnings ("unchecked") List<RegexGroup> list = (List<RegexGroup>) input;
						
						return list.get(0);
					}
				}));
			}
			return false;
		}
		
		// check for closed character groups
		beginMatch = new Regex("(?<!\\\\)\\[|^\\[");
		endMatch = new Regex("(?<!\\\\)\\][*+]?\\??"); // TODO remove trailing
		// multiplicators
		
		List<List<RegexGroup>> allClosedCharGroupsOpen = beginMatch.findAll(patternWithoutCharacterClasses);
		List<List<RegexGroup>> allClosedCharGroupsClosed = endMatch.findAll(patternWithoutCharacterClasses);
		
		beginCount = (allClosedCharGroupsOpen != null ? allClosedCharGroupsOpen.size() : 0);
		endCount = (allClosedCharGroupsClosed != null ? allClosedCharGroupsClosed.size() : 0);
		
		if (beginCount != endCount) {
			if (RepoSuiteSettings.logWarn()) {
				if (beginCount > endCount) {
					Logger.warn("Too many opening '[' parenthesis.");
				} else {
					Logger.warn("Too many closing ']' parenthesis.");
				}
			}
			return false;
		}
		
		// check for captured negative lookahead matching (must be avoided
		// because this leads to strange behavior)
		// and check for captured lookbehind groups in general
		Regex regex = new Regex("(\\(\\?(<=|<!|!)[^()]+\\([^)]*\\))");
		List<List<RegexGroup>> findAll = regex.findAll(patternWithoutCharacterClasses);
		
		if (findAll != null) {
			
			if (RepoSuiteSettings.logWarn()) {
				Logger.warn("Capturing negative lookahead groups is not supported. Affected groups: "
				        + JavaUtils.collectionToString(findAll));
			}
			return false;
		}
		
		// check for named lookbehind/lookahead (must be avoided since not
		// captured)
		regex = new Regex("(\\(\\?(<=|<!|!|=)\\{[^}]\\}[^)]\\))");
		findAll = regex.findAll(patternWithoutCharacterClasses);
		if (findAll != null) {
			
			if (RepoSuiteSettings.logWarn()) {
				Logger.warn("Naming of uncaptured group makes no sense: " + JavaUtils.collectionToString(findAll));
			}
		}
		
		// check for \ at the end
		if (pattern.endsWith("\\")) {
			
			if (RepoSuiteSettings.logWarn()) {
				Logger.warn("'\\' at the end of a regex is not supported.");
			}
			return false;
		}
		
		try {
			new NamedPattern(pattern);
		} catch (Exception e) {
			
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reduces the pattern to find the longest substring of the pattern
	 * (starting at the beginning that matches the text). If the specified
	 * pattern already matches the text, the pattern is returned.
	 * 
	 * @param text
	 *            to be checked against
	 * @return the {@link String} representation of the matching pattern
	 */
	public static String findLongestMatchingPattern(String pattern, final String text) {
		assert (text != null);
		assert (pattern != null);
		
		Regex regex = new Regex("placeholder");
		regex.setPattern(pattern);
		Regex bsReplacer = new Regex("\\\\+$");
		
		while ((regex.find(text) == null) && (pattern.length() > 1)) {
			
			try {
				pattern = pattern.substring(0, pattern.length() - 1);
				pattern = bsReplacer.removeAll(pattern);
				
				if (checkRegex(pattern)) {
					regex.setPattern(pattern);
				} else {
					if (RepoSuiteSettings.logDebug()) {
						Logger.debug("Skipping invalid pattern: " + pattern);
					}
				}
			} catch (PatternSyntaxException e) {
				
			}
		}
		
		return (regex.matched ? pattern : "");
	}
	
	private NamedPattern                 pattern;
	
	private Matcher                      matcher;
	private Replacer                     replacer;
	private Map<String, Integer>         groupNames = new HashMap<String, Integer>();
	
	private final List<RegexGroup>       matches    = new LinkedList<RegexGroup>();
	private final List<List<RegexGroup>> allMatches = new LinkedList<List<RegexGroup>>();
	private Boolean                      matched;
	
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
		
		try {
			this.pattern = new NamedPattern(pattern, flags);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(StringEscapeUtils.escapeJava(pattern));
		}
		
		reset();
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
		reset();
		
		this.matcher = this.pattern.matcher(text);
		if (this.matcher.find()) {
			this.matched = true;
			
			for (int i = 1; i < this.matcher.groupCount(); ++i) {
				this.matches.add(new RegexGroup(this.pattern.toString(), text, this.matcher.group(i), i, this.pattern
				        .getGroupName(i)));
				
				if (this.pattern.getGroupName(i) != null) {
					this.groupNames.put(this.pattern.getGroupName(i), i);
				}
			}
		}
		
		return (this.matches.size() > 0 ? this.matches : null);
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
		reset();
		
		this.matcher = this.pattern.matcher(text);
		MatchIterator findAll = this.matcher.findAll();
		
		this.matched = findAll.hasMore();
		
		while (findAll.hasMore()) {
			MatchResult match = findAll.nextMatch();
			List<RegexGroup> matches = new ArrayList<RegexGroup>(this.matcher.groupCount());
			
			for (int i = 1; i < match.groupCount(); ++i) {
				matches.add(new RegexGroup(this.pattern.toString(), text, match.group(i), i, this.pattern
				        .getGroupName(i)));
				if (this.pattern.getGroupName(i) != null) {
					this.groupNames.put(this.pattern.getGroupName(i), i);
				}
			}
			this.allMatches.add(matches);
		}
		
		return (this.allMatches.size() == 0 ? null : this.allMatches);
	}
	
	/**
	 * This uses non-breaking search to find all possible occurrences of the
	 * pattern, including those that are intersecting or nested. This is
	 * achieved by using the Matcher's method proceed() instead of find().
	 * 
	 * @param text
	 *            the text to be scanned
	 * @return a list of single element lists containing a {@link RegexGroup}
	 */
	public List<List<RegexGroup>> findAllPossibleMatches(final String text) {
		assert (text != null);
		reset();
		
		this.matcher = this.pattern.matcher(text);
		
		while (this.matcher.proceed()) {
			this.matched = true;
			LinkedList<RegexGroup> candidates = new LinkedList<RegexGroup>();
			candidates.add(new RegexGroup(getPattern(), text, this.matcher.toString(), 0, null));
			this.allMatches.add(candidates);
		}
		
		return this.allMatches;
	}
	
	/**
	 * @return the allMatches
	 */
	public List<List<RegexGroup>> getAllMatches() {
		return this.allMatches;
	}
	
	/**
	 * @param i
	 * @return
	 */
	public String getGroup(final int i) {
		return this.matcher.group(i);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public String getGroup(final String name) {
		return this.matcher.group(name);
	}
	
	/**
	 * @return the groupCount
	 */
	public Integer getGroupCount() {
		return this.pattern.groupCount() - 1;
	}
	
	/**
	 * @return the groupNames
	 */
	public Set<String> getGroupNames() {
		return this.groupNames.keySet();
	}
	
	/**
	 * @return the matches
	 */
	public List<RegexGroup> getMatches() {
		return this.matches;
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
	 * @return
	 */
	public Boolean matched() {
		return this.matched;
	}
	
	/**
	 * @param text
	 * @return
	 */
	public boolean matches(final String text) {
		assert (text != null);
		reset();
		
		find(text);
		return (this.matches != null) && (this.matches.size() > 0);
	}
	
	/**
	 * Checks if the specified pattern matches the text (completely).
	 * 
	 * @param text
	 *            the text to be matched
	 * @return if there is a full match
	 */
	public boolean matchesFull(final String text) {
		assert (text != null);
		reset();
		
		this.matcher = this.pattern.matcher(text);
		this.matched = this.matcher.matches();
		for (int i = 1; i < getGroupCount(); ++i) {
			this.groupNames.put(this.pattern.getGroupName(i), i);
		}
		
		return this.matched;
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
		reset();
		
		Matcher matcher = this.pattern.matcher();
		this.matched = matcher.matchesPrefix();
		return this.matched;
	}
	
	/**
	 * Removes all matches in the text.
	 * 
	 * @param text
	 *            the base text
	 * @return the reduced string
	 */
	public String removeAll(final String text) {
		assert (text != null);
		reset();
		
		this.replacer = this.pattern.replacer("");
		String returnString = this.replacer.replace(text);
		this.matched = returnString.length() < text.length();
		
		return returnString;
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
		assert (text != null);
		assert (replacement != null);
		// we do not allow references/patterns in this method
		assert (new Regex("(?<!\\\\)\\$|^\\$").find(replacement) == null);
		// this is done to ensure matches are not replaced by themselves
		assert ((find(text) == null) || (this.matched && !this.pattern.replacer(replacement).replace(text).equals(text)));
		
		reset();
		
		this.replacer = this.pattern.replacer(replacement);
		String returnString = this.replacer.replace(text);
		this.matched = !returnString.equals(text);
		return returnString;
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
		assert (text != null);
		assert (replacement != null);
		// this is done to ensure matches are not replaced by themselves
		assert ((find(text) == null) || (this.matched && !this.pattern.replacer(replacement).replace(text).equals(text)));
		// we do enforce references/patterns in this method
		assert (new Regex("(?<!\\\\)\\$|^\\$").find(replacement) != null);
		reset();
		
		this.replacer = this.pattern.replacer(replacement);
		String returnString = this.replacer.replace(text);
		this.matched = !returnString.equals(text);
		
		return returnString;
	}
	
	/**
	 * Resets storage to guarantee consistent getter outputs.
	 */
	private void reset() {
		this.matcher = null;
		this.replacer = null;
		this.matches.clear();
		this.allMatches.clear();
		this.matched = null;
		this.groupNames = new HashMap<String, Integer>();
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	private void setPattern(final String pattern) {
		assert (pattern != null);
		assert (pattern.length() > 0);
		// assert (checkRegex(pattern));
		try {
			this.pattern = new NamedPattern(pattern);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(StringEscapeUtils.escapeJava(pattern));
		}
		reset();
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
		String[] split = new RETokenizer(this.pattern, text).split();
		this.matched = (split != null) && (split.length > 0);
		return split;
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

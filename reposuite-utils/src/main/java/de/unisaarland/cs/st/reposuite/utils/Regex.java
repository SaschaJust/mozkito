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
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.MinSize;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * This class provides regular expression support and as well interfaces as
 * extends JRegex.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Regex {
	
	public static final String emailPattern = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
	
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
	public static boolean checkRegex(@NotNull ("Patterns to be checked by checkRegex may not be null.") @MinSize (min = 1,
	                                                                                                              value = "Patterns to be checked by checkRegex may have to be strings of length > 0.") final String pattern) {
		// avoid captured positive look ahead
		if (Logger.logTrace()) {
			Logger.trace("Checking pattern: " + pattern);
		}
		
		if (pattern.matches(".*\\[:[a-zA-Z]+:\\].*")) {
			
			if (Logger.logWarn()) {
				Logger.warn(Regex.class.getSimpleName()
				        + "does not support posix character classes like: [:alpha:], [:punct:], etc...");
			}
			return false;
		}
		
		// remove all character classes []
		Regex characterGroups = new Regex("((?<!\\\\)\\[|^\\[)[^\\]]*\\][*+]?\\??");
		String patternWithoutCharacterClasses = characterGroups.removeAll(pattern);
		
		if (Logger.logTrace()) {
			Logger.trace("Pattern without character classes: " + patternWithoutCharacterClasses);
		}
		
		// check for closed matching groups
		Regex beginMatch = new Regex("(?<!\\\\)\\(|^\\(");
		Regex endMatch = new Regex("(?<!\\\\)\\)");
		
		List<List<RegexGroup>> allMatchingGroupsOpen = beginMatch.findAll(patternWithoutCharacterClasses);
		List<List<RegexGroup>> allMatchingGroupsClosed = endMatch.findAll(patternWithoutCharacterClasses);
		
		int beginCount = (allMatchingGroupsOpen != null
		                                               ? allMatchingGroupsOpen.size()
		                                               : 0);
		int endCount = (allMatchingGroupsClosed != null
		                                               ? allMatchingGroupsClosed.size()
		                                               : 0);
		
		if (beginCount != endCount) {
			if (beginCount > endCount) {
				
				if (Logger.logWarn()) {
					Logger.warn("Too many opening '(' parenthesis.");
				}
			} else {
				if (Logger.logWarn()) {
					Logger.warn("Too many closing ')' parenthesis.");
				}
			}
			return false;
		}
		
		// check for empty matching groups
		Regex emptyGroups = new Regex("(\\((\\?<?[!=])?(\\{\\w+\\})?\\))");
		List<List<RegexGroup>> emptyGroupsList = emptyGroups.findAll(pattern);
		
		if (emptyGroupsList != null) {
			
			if (Logger.logWarn()) {
				Logger.warn("Empty matching groups: " + CollectionUtils.collect(emptyGroupsList, new Transformer() {
					
					@Override
					public Object transform(final Object input) {
						@SuppressWarnings ("unchecked")
						List<RegexGroup> list = (List<RegexGroup>) input;
						
						return list.get(0);
					}
				}));
			}
			return false;
		}
		
		// check for closed character groups
		beginMatch = new Regex("(?<!\\\\)\\[|^\\[");
		endMatch = new Regex("(?<!\\\\)\\][*+]?\\??");
		// TODO remove trailing multiplicators
		
		List<List<RegexGroup>> allClosedCharGroupsOpen = beginMatch.findAll(patternWithoutCharacterClasses);
		List<List<RegexGroup>> allClosedCharGroupsClosed = endMatch.findAll(patternWithoutCharacterClasses);
		
		beginCount = (allClosedCharGroupsOpen != null
		                                             ? allClosedCharGroupsOpen.size()
		                                             : 0);
		endCount = (allClosedCharGroupsClosed != null
		                                             ? allClosedCharGroupsClosed.size()
		                                             : 0);
		
		if (beginCount != endCount) {
			if (Logger.logWarn()) {
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
			
			if (Logger.logWarn()) {
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
			
			if (Logger.logWarn()) {
				Logger.warn("Naming of uncaptured group makes no sense: " + JavaUtils.collectionToString(findAll));
			}
		}
		
		// check for \ at the end
		if (pattern.endsWith("\\")) {
			
			if (Logger.logWarn()) {
				Logger.warn("'\\' at the end of a regex is not supported.");
			}
			return false;
		}
		
		try {
			new NamedPattern(pattern);
		} catch (Exception e) {
			
			// if (Logger.logError()) {
			// Logger.error(e.getMessage(), e);
			// }
			
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
	public static String findLongestMatchingPattern(@NotNull ("When trying to find the longest matching pattern to a given text, the pattern is required to be a non-null string.") String pattern,
	                                                @NotNull ("When trying to find the longest matching pattern to a given text, the text is required to be a non-null string.") final String text) {
		Regex regex = new Regex("placeholder");
		regex.setPattern(pattern);
		Regex bsReplacer = new Regex("\\\\+$");
		
		while ((regex.find(text) == null) && (pattern.length() > 2)) {
			
			try {
				pattern = pattern.substring(0, pattern.length() - 1);
				pattern = bsReplacer.removeAll(pattern);
				
				if (checkRegex(pattern)) {
					regex.setPattern(pattern);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Skipping invalid pattern: " + pattern);
					}
				}
			} catch (PatternSyntaxException e) {
				
			}
		}
		
		return (regex.matched
		                     ? pattern
		                     : "");
	}
	
	private final List<List<RegexGroup>> allMatches = new LinkedList<List<RegexGroup>>();
	
	private Map<String, Integer>         groupNames = new HashMap<String, Integer>();
	private Boolean                      matched;
	private Matcher                      matcher;
	
	private final List<RegexGroup>       matches    = new LinkedList<RegexGroup>();
	private NamedPattern                 pattern;
	private Replacer                     replacer;
	
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
	public Regex(@NotNull final String pattern, final int flags) {
		CompareCondition.greater(pattern.length(), 0, "We don't accept empty patterns.");
		
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
		if (pattern == null) {
			if (other.pattern != null) {
				return false;
			}
		} else if (!pattern.equals(other.pattern)) {
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
	public List<RegexGroup> find(@NotNull final String text) {
		reset();
		
		matcher = pattern.matcher(text);
		if (matcher.find()) {
			matched = true;
			
			for (int i = 0; i < matcher.groupCount(); ++i) {
				matches.add(new RegexGroup(pattern.toString(), text, matcher.group(i), i, pattern.getGroupName(i)));
				
				if (pattern.getGroupName(i) != null) {
					groupNames.put(pattern.getGroupName(i), i);
				}
			}
		}
		
		return (matches.size() > 0
		                          ? matches
		                          : null);
	}
	
	/**
	 * Finds all occurrences in the text
	 * 
	 * @param text
	 *            the text to be analyzed
	 * @see Regex#find(String)
	 * @return a {@link List} of {@link List}s of {@link RegexGroup}
	 */
	public List<List<RegexGroup>> findAll(@NotNull final String text) {
		reset();
		
		matcher = pattern.matcher(text);
		MatchIterator findAll = matcher.findAll();
		
		matched = findAll.hasMore();
		
		while (findAll.hasMore()) {
			MatchResult match = findAll.nextMatch();
			List<RegexGroup> matches = new ArrayList<RegexGroup>(matcher.groupCount());
			
			for (int i = 1; i < match.groupCount(); ++i) {
				matches.add(new RegexGroup(pattern.toString(), text, match.group(i), i, pattern.getGroupName(i)));
				if (pattern.getGroupName(i) != null) {
					groupNames.put(pattern.getGroupName(i), i);
				}
			}
			allMatches.add(matches);
		}
		
		return (allMatches.size() == 0
		                              ? null
		                              : allMatches);
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
	public List<List<RegexGroup>> findAllPossibleMatches(@NotNull final String text) {
		reset();
		
		matcher = pattern.matcher(text);
		
		while (matcher.proceed()) {
			matched = true;
			LinkedList<RegexGroup> candidates = new LinkedList<RegexGroup>();
			candidates.add(new RegexGroup(getPattern(), text, matcher.toString(), 0, null));
			allMatches.add(candidates);
		}
		
		return allMatches;
	}
	
	/**
	 * @return the allMatches
	 */
	public List<List<RegexGroup>> getAllMatches() {
		return allMatches;
	}
	
	/**
	 * @param i
	 * @return
	 */
	public String getGroup(final int i) {
		return matcher.group(i);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public String getGroup(final String name) {
		return matcher.group(name);
	}
	
	/**
	 * @return the groupCount
	 */
	public Integer getGroupCount() {
		return pattern.groupCount() - 1;
	}
	
	/**
	 * @return the groupNames
	 */
	public Set<String> getGroupNames() {
		return groupNames.keySet();
	}
	
	/**
	 * @return the matches
	 */
	public List<RegexGroup> getMatches() {
		return matches;
	}
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pattern == null)
		                                            ? 0
		                                            : pattern.hashCode());
		return result;
	}
	
	/**
	 * @return
	 */
	public Boolean matched() {
		return matched;
	}
	
	/**
	 * @param text
	 * @return
	 */
	public boolean matches(@NotNull final String text) {
		reset();
		
		find(text);
		return (matches != null) && (matches.size() > 0);
	}
	
	/**
	 * Checks if the specified pattern matches the text (completely).
	 * 
	 * @param text
	 *            the text to be matched
	 * @return if there is a full match
	 */
	public boolean matchesFull(@NotNull final String text) {
		reset();
		
		matcher = pattern.matcher(text);
		matched = matcher.matches();
		for (int i = 1; i < getGroupCount(); ++i) {
			groupNames.put(pattern.getGroupName(i), i);
		}
		
		return matched;
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
	public boolean prefixMatches(@NotNull final String text) {
		reset();
		
		Matcher matcher = pattern.matcher();
		matched = matcher.matchesPrefix();
		return matched;
	}
	
	/**
	 * Removes all matches in the text.
	 * 
	 * @param text
	 *            the base text
	 * @return the reduced string
	 */
	public String removeAll(@NotNull final String text) {
		reset();
		
		replacer = pattern.replacer("");
		String returnString = replacer.replace(text);
		matched = returnString.length() < text.length();
		
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
	@NoneNull
	public String replaceAll(final String text,
	                         final String replacement) {
		Condition.isNull(new Regex("(?<!\\\\)\\$|^\\$").find(replacement),
		                 "We do not allow references/patterns in this method");
		Condition.check((find(text) == null) || (matched && !pattern.replacer(replacement).replace(text).equals(text)),
		                "This is done to ensure matches are not replaced by themselves");
		
		reset();
		
		replacer = pattern.replacer(replacement);
		String returnString = replacer.replace(text);
		matched = !returnString.equals(text);
		return returnString;
	}
	
	/**
	 * Resets storage to guarantee consistent getter outputs.
	 */
	private void reset() {
		matcher = null;
		replacer = null;
		matches.clear();
		allMatches.clear();
		matched = null;
		groupNames = new HashMap<String, Integer>();
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	private void setPattern(@NotNull @MinSize (min = 0) final String pattern) {
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
		String[] split = new RETokenizer(pattern, text).split();
		matched = (split != null) && (split.length > 0);
		return split;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Regex [pattern=" + pattern + "]";
	}
	
}

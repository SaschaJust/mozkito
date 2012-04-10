/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package net.ownhero.dev.regex;

import java.util.List;
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
import net.ownhero.dev.kisa.Logger;

/**
 * This class provides regular expression support and as well interfaces as extends JRegex.
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
		final Regex characterGroups = new Regex("((?<!\\\\)\\[|^\\[)[^\\]]*\\][*+]?\\??");
		final String patternWithoutCharacterClasses = characterGroups.removeAll(pattern);
		
		if (Logger.logTrace()) {
			Logger.trace("Pattern without character classes: " + patternWithoutCharacterClasses);
		}
		
		// check for closed matching groups
		Regex beginMatch = new Regex("(?<!\\\\)\\(|^\\(");
		Regex endMatch = new Regex("(?<!\\\\)\\)");
		
		final MultiMatch allMatchingGroupsOpen = beginMatch.findAll(patternWithoutCharacterClasses);
		final MultiMatch allMatchingGroupsClosed = endMatch.findAll(patternWithoutCharacterClasses);
		
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
		final Regex emptyGroups = new Regex("(\\((\\?<?[!=])?(\\{\\w+\\})?\\))");
		final MultiMatch emptyGroupsList = emptyGroups.findAll(pattern);
		
		if (emptyGroupsList != null) {
			
			if (Logger.logWarn()) {
				
				Logger.warn("Empty matching groups: " + emptyGroupsList.getGroup(0));
			}
			return false;
		}
		
		// check for closed character groups
		beginMatch = new Regex("(?<!\\\\)\\[|^\\[");
		endMatch = new Regex("(?<!\\\\)\\][*+]?\\??");
		// TODO remove trailing multiplicators
		
		final MultiMatch allClosedCharGroupsOpen = beginMatch.findAll(patternWithoutCharacterClasses);
		final MultiMatch allClosedCharGroupsClosed = endMatch.findAll(patternWithoutCharacterClasses);
		
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
		MultiMatch findAll = regex.findAll(patternWithoutCharacterClasses);
		
		if (findAll != null) {
			
			if (Logger.logWarn()) {
				Logger.warn("Capturing negative lookahead groups is not supported. Affected groups: " + findAll);
			}
			return false;
		}
		
		// check for named lookbehind/lookahead (must be avoided since not
		// captured)
		regex = new Regex("(\\(\\?(<=|<!|!|=)\\{[^}]\\}[^)]\\))");
		findAll = regex.findAll(patternWithoutCharacterClasses);
		if (findAll != null) {
			
			if (Logger.logWarn()) {
				Logger.warn("Naming of uncaptured group makes no sense: " + findAll);
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
		} catch (final Exception e) {
			
			// if (Logger.logError()) {
			// Logger.error(e);
			// }
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reduces the pattern to find the longest substring of the pattern (starting at the beginning that matches the
	 * text). If the specified pattern already matches the text, the pattern is returned.
	 * 
	 * @param text
	 *            to be checked against
	 * @return the {@link String} representation of the matching pattern
	 */
	public static String findLongestMatchingPattern(@NotNull ("When trying to find the longest matching pattern to a given text, the pattern is required to be a non-null string.") final String pattern,
	                                                @NotNull ("When trying to find the longest matching pattern to a given text, the text is required to be a non-null string.") final String text) {
		final Regex regex = new Regex("placeholder");
		String localPattern = pattern;
		regex.setPattern(localPattern);
		final Regex bsReplacer = new Regex("\\\\+$");
		
		while ((regex.find(text) == null) && (localPattern.length() > 2)) {
			
			try {
				localPattern = localPattern.substring(0, localPattern.length() - 1);
				localPattern = bsReplacer.removeAll(localPattern);
				
				if (checkRegex(localPattern)) {
					regex.setPattern(localPattern);
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Skipping invalid pattern: " + localPattern);
					}
				}
			} catch (final PatternSyntaxException ignore) {
				// ignore
			}
		}
		
		return (regex.matched
		                     ? localPattern
		                     : "");
	}
	
	private MultiMatchImpl multiMatch = new MultiMatchImpl();
	
	private Boolean        matched;
	private Matcher        matcher;
	
	private MatchImpl      match      = null;
	private NamedPattern   pattern;
	private Replacer       replacer;
	
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
		} catch (final ArrayIndexOutOfBoundsException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw e;
		} catch (final PatternSyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw e;
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
		final Regex other = (Regex) obj;
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
	 * @return a {@link List} of {@link Group} representing the matches.
	 */
	public Match find(@NotNull final String text) {
		reset();
		
		this.matcher = this.pattern.matcher(text);
		if (this.matcher.find()) {
			this.matched = true;
			this.match = new MatchImpl(new Group(this.pattern.toString(), text, this.matcher.group(0), 0, null,
			                                     this.matcher.start(), this.matcher.end()));
			for (int i = 1; i < this.matcher.groupCount(); ++i) {
				if (this.matcher.group(i) != null) {
					this.match.add(new Group(this.pattern.toString(), text, this.matcher.group(i), i,
					                         this.pattern.getGroupName(i), this.matcher.start(), this.matcher.end()));
				}
			}
		}
		
		return this.match;
	}
	
	/**
	 * Finds all occurrences in the text
	 * 
	 * @param text
	 *            the text to be analyzed
	 * @see Regex#find(String)
	 * @return a {@link List} of {@link List}s of {@link Group}
	 */
	public MultiMatch findAll(@NotNull final String text) {
		reset();
		
		this.matcher = this.pattern.matcher(text);
		final MatchIterator findAll = this.matcher.findAll();
		
		this.matched = findAll.hasMore();
		this.multiMatch = new MultiMatchImpl();
		
		while (findAll.hasMore()) {
			final MatchResult matchResult = findAll.nextMatch();
			final MatchImpl match = new MatchImpl(new Group(this.pattern.toString(), text, matchResult.group(0), 0,
			                                                null, this.matcher.start(), this.matcher.end()));
			
			for (int i = 1; i < matchResult.groupCount(); ++i) {
				if (matchResult.group(i) != null) {
					match.add(new Group(this.pattern.toString(), text, matchResult.group(i), i,
					                    this.pattern.getGroupName(i), this.matcher.start(), this.matcher.end()));
				}
			}
			this.multiMatch.add(match);
		}
		
		return (this.multiMatch.size() == 0
		                                   ? null
		                                   : this.multiMatch);
	}
	
	/**
	 * This uses non-breaking search to find all possible occurrences of the pattern, including those that are
	 * intersecting or nested. This is achieved by using the Matcher's method proceed() instead of find().
	 * 
	 * @param text
	 *            the text to be scanned
	 * @return a list of single element lists containing a {@link Group}
	 */
	public MultiMatch findAllPossibleMatches(@NotNull final String text) {
		reset();
		
		this.matcher = this.pattern.matcher(text);
		this.multiMatch = new MultiMatchImpl();
		
		while (this.matcher.proceed()) {
			this.matched = true;
			final MatchImpl candidates = new MatchImpl(new Group(getPattern(), text, this.matcher.toString(), 0, null,
			                                                     this.matcher.start(), this.matcher.end()));
			this.multiMatch.add(candidates);
		}
		
		return this.multiMatch;
	}
	
	/**
	 * @return the allMatches
	 */
	public MultiMatch getAllMatches() {
		return this.multiMatch;
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
		return this.pattern.getGroupsNames();
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/**
	 * @return the matches
	 */
	public Match getMatches() {
		return this.match;
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
		result = (prime * result) + ((this.pattern == null)
		                                                   ? 0
		                                                   : this.pattern.hashCode());
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
	public boolean matches(@NotNull final String text) {
		reset();
		
		find(text);
		return (this.match != null) && (this.match.getGroupCount() > 0);
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
		
		this.matcher = this.pattern.matcher(text);
		this.matched = this.matcher.matches();
		
		return this.matched;
	}
	
	/**
	 * This feature allows to find out whether the string could match by examining only its beginning part. For example,
	 * the string is being typed into a text field, and you want to reject the rest characters after the first few ones
	 * appear incorrect.
	 * 
	 * @param text
	 *            the text to be analyzed
	 * @return if the text is a prefix of a matching string
	 */
	public boolean prefixMatches(@NotNull final String text) {
		reset();
		
		final Matcher matcher = this.pattern.matcher();
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
	public String removeAll(@NotNull final String text) {
		reset();
		
		this.replacer = this.pattern.replacer("");
		final String returnString = this.replacer.replace(text);
		this.matched = returnString.length() < text.length();
		
		return returnString;
	}
	
	/**
	 * Replaces all occurrences of the pattern in the text with the given string.
	 * 
	 * @param text
	 * @param replacement
	 * @return
	 */
	@NoneNull
	public String replaceAll(final String text,
	                         final String replacement) {
		Condition.check((find(text) == null)
		                        || (this.matched && !this.pattern.replacer(replacement).replace(text).equals(text)),
		                "This is done to ensure matches are not replaced by themselves");
		
		reset();
		
		this.replacer = this.pattern.replacer(replacement);
		final String returnString = this.replacer.replace(text);
		this.matched = !returnString.equals(text);
		return returnString;
	}
	
	/**
	 * Resets storage to guarantee consistent getter outputs.
	 */
	private void reset() {
		// PRECONDITIONS
		
		try {
			this.matcher = null;
			this.replacer = null;
			this.match = null;
			this.multiMatch = null;
			this.matched = null;
		} finally {
			// POSTCONDITIONS
			Condition.isNull(this.match, "Field '%s' in '%s'.", "match", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.isNull(this.replacer, "Field '%s' in '%s'.", "replacer", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.isNull(this.match, "Field '%s' in '%s'.", "match", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.isNull(this.multiMatch, "Field '%s' in '%s'.", "multiMatch", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			Condition.isNull(this.matched, "Field '%s' in '%s'.", "matched", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @param pattern
	 *            the pattern to set
	 */
	private void setPattern(@NotNull @MinSize (min = 0) final String pattern) {
		try {
			this.pattern = new NamedPattern(pattern);
		} catch (final ArrayIndexOutOfBoundsException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw e;
		} catch (final PatternSyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw e;
		}
		reset();
	}
	
	/**
	 * String tokenizing is pretty similar to using a standard StringTokenizer class. The only difference is that this
	 * one uses a pattern occurrence as a token delimiter. You can refine your search criteria by adding
	 * backward/forward scanning, e.g. when using a <code>"---"</code> delimiter you can use the pattern
	 * <code>"(?<!\")---(?!\")"</code>. This makes sure the hyphens are not enclosed by quote marks.
	 * 
	 * @param text
	 *            to be split using the pattern
	 * @return a string array containing all tokens
	 */
	public String[] tokenize(final String text) {
		final String[] split = new RETokenizer(this.pattern, text).split();
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
